package io.github.com.quillraven.tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.AssetService;
import io.github.com.quillraven.asset.AtlasAsset;
import io.github.com.quillraven.component.Animation2D;
import io.github.com.quillraven.component.Animation2D.AnimationType;
import io.github.com.quillraven.component.CameraFollow;
import io.github.com.quillraven.component.Controller;
import io.github.com.quillraven.component.Facing;
import io.github.com.quillraven.component.Facing.FacingDirection;
import io.github.com.quillraven.component.Fsm;
import io.github.com.quillraven.component.Graphic;
import io.github.com.quillraven.component.Move;
import io.github.com.quillraven.component.Physic;
import io.github.com.quillraven.component.Transform;

public class TiledAshleySpawner {
    private static final Vector2 DEFAULT_SCALING = new Vector2(1f, 1f);

    private final Engine engine;
    private final World physicWorld;
    private final MapObjects tmpMapObjects;
    private final Vector2 tmpVec2;
    private final AssetService assetService;

    public TiledAshleySpawner(Engine engine, World physicWorld, AssetService assetService) {
        this.engine = engine;
        this.physicWorld = physicWorld;
        this.tmpMapObjects = new MapObjects();
        this.tmpVec2 = new Vector2();
        this.assetService = assetService;
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

        spawnMapBoundary(tiledMap);
    }

    private void spawnMapBoundary(TiledMap tiledMap) {
        // create four boxes for the map boundary (left, right, bottom and top edge)
        int width = tiledMap.getProperties().get("width", 0, Integer.class);
        int tileW = tiledMap.getProperties().get("tilewidth", 0, Integer.class);
        int height = tiledMap.getProperties().get("height", 0, Integer.class);
        int tileH = tiledMap.getProperties().get("tileheight", 0, Integer.class);
        float mapW = width * tileW * GdxGame.UNIT_SCALE;
        float mapH = height * tileH * GdxGame.UNIT_SCALE;
        float halfW = mapW * 0.5f;
        float halfH = mapH * 0.5f;
        float boxThickness = 0.5f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.setZero();
        bodyDef.fixedRotation = true;
        Body body = physicWorld.createBody(bodyDef);

        // left edge
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxThickness, halfH, new Vector2(-boxThickness, halfH), 0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();
        // right edge
        shape = new PolygonShape();
        shape.setAsBox(boxThickness, halfH, new Vector2(mapW + boxThickness, halfH), 0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();
        // bottom edge
        shape = new PolygonShape();
        shape.setAsBox(halfW, boxThickness, new Vector2(halfW, -boxThickness), 0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();
        // top edge
        shape = new PolygonShape();
        shape.setAsBox(halfW, boxThickness, new Vector2(halfW, mapH + boxThickness), 0f);
        body.createFixture(shape, 0f).setFriction(0f);
        shape.dispose();
    }

    private void loadTileLayer(TiledMapTileLayer tileLayer) {
        for (int y = 0; y < tileLayer.getHeight(); y++) {
            for (int x = 0; x < tileLayer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                if (cell == null) continue;

                TiledMapTile tile = cell.getTile();
                createBody(tile.getObjects(),
                    new Vector2(x, y),
                    DEFAULT_SCALING,
                    BodyType.StaticBody,
                    Vector2.Zero,
                    "environment");
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
                    0,
                    entity);
                addEntityPhysic(
                    rectMapObj,
                    BodyType.StaticBody,
                    tmpVec2.set(rect.getX(), rect.getY()).scl(GdxGame.UNIT_SCALE),
                    entity);
                this.engine.addEntity(entity);
            } else {
                throw new GdxRuntimeException("Unsupported trigger: " + mapObject.getClass().getSimpleName());
            }
        }
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
        TiledMapTile tile = tileMapObject.getTile();
        TextureRegion textureRegion = getTextureRegion(tile);
        String classType = tile.getProperties().get("type", "", String.class);
        float sortOffsetY = tile.getProperties().get("sortOffsetY", 0, Integer.class);
        sortOffsetY *= GdxGame.UNIT_SCALE;

        addEntityTransform(
            tileMapObject.getX(), tileMapObject.getY(),
            textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
            tileMapObject.getScaleX(), tileMapObject.getScaleY(),
            sortOffsetY,
            entity);
        addEntityPhysic(
            tile.getObjects(),
            "Prop".equals(classType) ? BodyType.StaticBody : BodyType.DynamicBody,
            Vector2.Zero,
            entity);
        addEntityAnimation(tile, entity);
        addEntityMove(tile, entity);
        addEntityController(tileMapObject, entity);
        addEntityCameraFollow(tileMapObject, entity);
        entity.add(new Facing(FacingDirection.DOWN));
        entity.add(new Fsm(entity));
        entity.add(new Graphic(textureRegion, Color.WHITE.cpy()));

        this.engine.addEntity(entity);
    }

    private TextureRegion getTextureRegion(TiledMapTile tile) {
        String atlasAssetStr = tile.getProperties().get("atlasAsset", "OBJECTS", String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        FileTextureData textureData = (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        String atlasKey = textureData.getFileHandle().nameWithoutExtension();
        TextureAtlas textureAtlas = assetService.get(atlasAsset);
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(atlasKey + "/" + atlasKey);
        if (region != null) {
            return region;
        }

        // Region not part of an atlas, or the object has an animation.
        // If it has an animation, then its region is updated in the AnimationSystem.
        // If it has no region, then we render the region of the Tiled editor to show something, but
        // that will add one render call due to texture swapping.
        return tile.getTextureRegion();
    }

    private void addEntityCameraFollow(TiledMapTileMapObject tileMapObject, Entity entity) {
        boolean cameraFollow = tileMapObject.getProperties().get("camFollow", false, Boolean.class);
        if (!cameraFollow) return;

        entity.add(new CameraFollow());
    }

    private void addEntityController(TiledMapTileMapObject tileMapObject, Entity entity) {
        boolean controller = tileMapObject.getProperties().get("controller", false, Boolean.class);
        if (!controller) return;

        entity.add(new Controller());
    }

    private void addEntityMove(TiledMapTile tile, Entity entity) {
        float speed = tile.getProperties().get("speed", 0f, Float.class);
        if (speed == 0f) return;

        entity.add(new Move(speed));
    }

    private void addEntityAnimation(TiledMapTile tile, Entity entity) {
        String animationStr = tile.getProperties().get("animation", "", String.class);
        if (animationStr.isBlank()) {
            return;
        }
        AnimationType animationType = AnimationType.valueOf(animationStr);

        String atlasAssetStr = tile.getProperties().get("atlasAsset", "OBJECTS", String.class);
        AtlasAsset atlasAsset = AtlasAsset.valueOf(atlasAssetStr);
        FileTextureData textureData = (FileTextureData) tile.getTextureRegion().getTexture().getTextureData();
        String atlasKey = textureData.getFileHandle().nameWithoutExtension();

        entity.add(new Animation2D(atlasAsset, atlasKey, animationType, Animation.PlayMode.LOOP, 1f));
    }

    private void addEntityPhysic(MapObject mapObject, @SuppressWarnings("SameParameterValue") BodyType bodyType, Vector2 relativeTo, Entity entity) {
        if (tmpMapObjects.getCount() > 0) tmpMapObjects.remove(0);

        tmpMapObjects.add(mapObject);
        addEntityPhysic(tmpMapObjects, bodyType, relativeTo, entity);
    }

    private void addEntityPhysic(MapObjects mapObjects, BodyType bodyType, Vector2 relativeTo, Entity entity) {
        if (mapObjects.getCount() == 0) return;

        Transform transform = entity.getComponent(Transform.class);
        Body body = createBody(mapObjects,
            transform.getPosition(),
            transform.getScaling(),
            bodyType,
            relativeTo,
            entity);

        entity.add(new Physic(body, new Vector2(body.getPosition())));
    }

    private Body createBody(MapObjects mapObjects,
                            Vector2 position,
                            Vector2 scaling,
                            BodyType bodyType,
                            Vector2 relativeTo,
                            Object userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true;

        Body body = this.physicWorld.createBody(bodyDef);
        body.setUserData(userData);
        for (MapObject object : mapObjects) {
            FixtureDef fixtureDef = TiledPhysics.fixtureDefOf(object, scaling, relativeTo);
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }
        return body;
    }

    private static void addEntityTransform(
        float x, float y,
        float w, float h,
        float scaleX, float scaleY,
        float sortOffsetY,
        Entity entity
    ) {
        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(w, h);
        Vector2 scaling = new Vector2(scaleX, scaleY);

        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        entity.add(new Transform(position, 0, size, scaling, 0f, sortOffsetY));
    }

}
