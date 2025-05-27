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
    private final MapObjects tmpMapObjects;
    private final Vector2 tmpVec2;

    public TiledAshleySpawner(Engine engine, World physicWorld) {
        this.engine = engine;
        this.physicWorld = physicWorld;
        this.tmpMapObjects = new MapObjects();
        this.tmpVec2 = new Vector2();
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
                Rectangle rect = rectMapObj.getRectangle();
                addEntityTransform(
                    rect.getX(), rect.getY(),
                    rect.getWidth(), rect.getHeight(),
                    1f, 1f,
                    entity);
                addEntityPhysic(
                    rectMapObj,
                    BodyDef.BodyType.StaticBody,
                    tmpVec2.set(rect.getX(), rect.getY()).scl(GdxGame.UNIT_SCALE),
                    entity);
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

        TextureRegion textureRegion = tileMapObject.getTile().getTextureRegion();
        addEntityTransform(
            tileMapObject.getX(), tileMapObject.getY(),
            textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
            tileMapObject.getScaleX(), tileMapObject.getScaleY(),
            entity);
        addEntityPhysic(
            tileMapObject.getTile().getObjects(),
            BodyDef.BodyType.DynamicBody,
            Vector2.Zero,
            entity);
        entity.add(new Graphic(textureRegion, Color.WHITE.cpy()));

        this.engine.addEntity(entity);
    }

    private void addEntityPhysic(MapObject mapObject, BodyDef.BodyType bodyType, Vector2 relativeTo, Entity entity) {
        if (tmpMapObjects.getCount() > 0) tmpMapObjects.remove(0);

        tmpMapObjects.add(mapObject);
        addEntityPhysic(tmpMapObjects, bodyType, relativeTo, entity);
    }

    private void addEntityPhysic(MapObjects mapObjects, BodyDef.BodyType bodyType, Vector2 relativeTo, Entity entity) {
        if (mapObjects.getCount() == 0) return;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        Transform transform = entity.getComponent(Transform.class);
        bodyDef.position.set(transform.getPosition());
        bodyDef.fixedRotation = true;

        Body body = this.physicWorld.createBody(bodyDef);
        body.setUserData(entity);
        for (MapObject object : mapObjects) {
            FixtureDef fixtureDef = TiledPhysics.fixtureDefOfMapObject(object, transform.getScaling(), relativeTo);
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }

        entity.add(new Physic(body, new Vector2(body.getPosition())));
    }

    private static void addEntityTransform(
        float x, float y,
        float w, float h,
        float scaleX, float scaleY,
        Entity entity
    ) {
        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(w, h);
        Vector2 scaling = new Vector2(scaleX, scaleY);

        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        entity.add(new Transform(position, 0, size, scaling, 0f));
    }

}
