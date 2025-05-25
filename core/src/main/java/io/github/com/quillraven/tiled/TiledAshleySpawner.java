package io.github.com.quillraven.tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.component.Graphic;
import io.github.com.quillraven.component.Physic;
import io.github.com.quillraven.component.Transform;

public class TiledAshleySpawner {
    private final Engine engine;
    private final World physicWorld;

    public TiledAshleySpawner(Engine engine, World physicWorld) {
        this.engine = engine;
        this.physicWorld = physicWorld;
    }

    public void loadMapObjects(TiledMap tiledMap) {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer tileLayer) {
                loadTileLayer(tileLayer);
            } else if ("objects".equals(layer.getName())) {
                loadObjectLayer(layer);
            } else if ("trigger".equals(layer.getName())) {
                loadTriggerLayer(layer);
            }
        }
    }

    private void loadTriggerLayer(MapLayer triggerLayer) {
        for (MapObject mapObject : triggerLayer.getObjects()) {
            if (mapObject instanceof RectangleMapObject rectMapObj) {
                Entity entity = this.engine.createEntity();
                addEntityTransform(rectMapObj, entity);
                addEntityPhysic(rectMapObj, entity);
                this.engine.addEntity(entity);
            } else {
                throw new GdxRuntimeException("Unsupported trigger: " + mapObject.getClass().getSimpleName());
            }
        }
    }

    private void loadTileLayer(TiledMapTileLayer tileLayer) {
    }

    private void loadObjectLayer(MapLayer objectLayer) {
        for (MapObject mapObject : objectLayer.getObjects()) {
            if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
                spawnEntityOf(tileMapObject);
            } else {
                throw new GdxRuntimeException("Unsupported object: " + mapObject.getClass().getSimpleName());
            }
        }
    }

    private void spawnEntityOf(TiledMapTileMapObject tileMapObject) {
        Entity entity = this.engine.createEntity();

        addEntityTransform(tileMapObject, entity);
        addEntityPhysic(tileMapObject.getTile().getObjects(), entity);
        entity.add(new Graphic(tileMapObject.getTile().getTextureRegion(), Color.WHITE.cpy()));

        this.engine.addEntity(entity);
    }

    private void addEntityPhysic(RectangleMapObject rectMapObj, Entity entity) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Transform transform = entity.getComponent(Transform.class);
        bodyDef.position.set(transform.position());
        bodyDef.fixedRotation = true;

        Body body = this.physicWorld.createBody(bodyDef);
        body.setUserData(entity);
        FixtureDef fixtureDef = TiledPhysics.fixtureDefOfMapObject(rectMapObj, transform.scaling(), new Vector2(transform.position()));
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        entity.add(new Physic(body, new Vector2(body.getPosition())));
    }

    private void addEntityPhysic(MapObjects objects, Entity entity) {
        if (objects.getCount() == 0) return;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Transform transform = entity.getComponent(Transform.class);
        bodyDef.position.set(transform.position());
        bodyDef.fixedRotation = true;

        Body body = this.physicWorld.createBody(bodyDef);
        body.setUserData(entity);
        for (MapObject object : objects) {
            FixtureDef fixtureDef = TiledPhysics.fixtureDefOfMapObject(object, transform.scaling(), Vector2.Zero);
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }

        entity.add(new Physic(body, new Vector2(body.getPosition())));
    }

    private static void addEntityTransform(TiledMapTileMapObject tileMapObject, Entity entity) {
        Vector2 position = new Vector2(tileMapObject.getX(), tileMapObject.getY());
        TextureRegion textureRegion = tileMapObject.getTile().getTextureRegion();
        Vector2 size = new Vector2(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        Vector2 scaling = new Vector2(tileMapObject.getScaleX(), tileMapObject.getScaleY());

        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        entity.add(new Transform(position, 0, size, scaling, 0f));
    }

    private void addEntityTransform(RectangleMapObject rectMapObj, Entity entity) {
        Rectangle rect = rectMapObj.getRectangle();
        Vector2 position = new Vector2(rect.getX(), rect.getY());
        Vector2 size = new Vector2(rect.getWidth(), rect.getHeight());

        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        entity.add(new Transform(position, 0, size, new Vector2(1f, 1f), 0f));
    }
}
