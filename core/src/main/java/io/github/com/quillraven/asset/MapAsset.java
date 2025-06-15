package io.github.com.quillraven.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.maps.tiled.TiledMap;

public enum MapAsset implements Asset<TiledMap> {
    MAIN("mainmap.tmx");

    private final AssetDescriptor<TiledMap> descriptor;

    MapAsset(String mapName) {
        this.descriptor = new AssetDescriptor<>("maps/" + mapName, TiledMap.class);
    }

    @Override
    public AssetDescriptor<TiledMap> getDescriptor() {
        return descriptor;
    }
}
