package io.github.com.quillraven.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.AssetService;
import io.github.com.quillraven.asset.MapAsset;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TiledService {
    private final AssetService assetService;
    private final World physicWorld;

    private TiledMap currentMap;

    private Consumer<TiledMap> mapChangeConsumer;
    private BiConsumer<String, MapObject> loadTriggerConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectConsumer;
    private LoadTileConsumer loadTileConsumer;

    public TiledService(AssetService assetService, World physicWorld) {
        this.assetService = assetService;
        this.physicWorld = physicWorld;
        this.mapChangeConsumer = null;
        this.loadTriggerConsumer = null;
        this.loadObjectConsumer = null;
        this.loadTileConsumer = null;
        this.currentMap = null;
    }

    public TiledMap loadMap(MapAsset mapAsset) {
        TiledMap tiledMap = this.assetService.load(mapAsset);
        tiledMap.getProperties().put("mapAsset", mapAsset);
        return tiledMap;
    }

    public void setMap(TiledMap tiledMap) {
        if (this.currentMap != null) {
            this.assetService.unload(this.currentMap.getProperties().get("mapAsset", MapAsset.class));
        }

        this.currentMap = tiledMap;
        loadMapObjects(tiledMap);
        if (this.mapChangeConsumer != null) {
            this.mapChangeConsumer.accept(tiledMap);
        }
    }

    public void setMapChangeConsumer(Consumer<TiledMap> mapChangeConsumer) {
        this.mapChangeConsumer = mapChangeConsumer;
    }

    public void setLoadObjectConsumer(Consumer<TiledMapTileMapObject> loadObjectConsumer) {
        this.loadObjectConsumer = loadObjectConsumer;
    }

    public void setLoadTriggerConsumer(BiConsumer<String, MapObject> loadTriggerConsumer) {
        this.loadTriggerConsumer = loadTriggerConsumer;
    }

    public void setLoadTileConsumer(LoadTileConsumer loadTileConsumer) {
        this.loadTileConsumer = loadTileConsumer;
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
        bodyDef.type = BodyDef.BodyType.StaticBody;
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
        if (loadTileConsumer == null) return;

        for (int y = 0; y < tileLayer.getHeight(); y++) {
            for (int x = 0; x < tileLayer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                if (cell == null) continue;

                loadTileConsumer.accept(cell.getTile(), x, y);
            }
        }
    }

    private void loadTriggerLayer(MapLayer triggerLayer) {
        if (loadTriggerConsumer == null) return;

        for (MapObject mapObject : triggerLayer.getObjects()) {
            if (mapObject.getName() == null || mapObject.getName().isBlank()) {
                throw new GdxRuntimeException("Trigger must have a name: " + mapObject);
            }

            if (mapObject instanceof RectangleMapObject rectMapObj) {
                loadTriggerConsumer.accept(mapObject.getName(), rectMapObj);
            } else {
                throw new GdxRuntimeException("Unsupported trigger: " + mapObject.getClass().getSimpleName());
            }
        }
    }

    private void loadObjectLayer(MapLayer objectLayer) {
        if (loadObjectConsumer == null) return;

        for (MapObject mapObject : objectLayer.getObjects()) {
            if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
                loadObjectConsumer.accept(tileMapObject);
            } else {
                throw new GdxRuntimeException("Unsupported object: " + mapObject.getClass().getSimpleName());
            }
        }
    }

    @FunctionalInterface
    public interface LoadTileConsumer {
        void accept(TiledMapTile tile, float x, float y);
    }
}
