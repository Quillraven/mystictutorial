package io.github.com.quillraven.tiled;

import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.com.quillraven.asset.AssetService;
import io.github.com.quillraven.asset.MapAsset;

import java.util.function.Consumer;

public class TiledService {
    private final AssetService assetService;
    private Consumer<TiledMap> mapChangeConsumer;
    private TiledMap currentMap;

    public TiledService(AssetService assetService) {
        this.assetService = assetService;
        this.mapChangeConsumer = null;
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
        if (this.mapChangeConsumer != null) {
            this.mapChangeConsumer.accept(tiledMap);
        }
    }

    public void setMapChangeConsumer(Consumer<TiledMap> mapChangeConsumer) {
        this.mapChangeConsumer = mapChangeConsumer;
    }
}
