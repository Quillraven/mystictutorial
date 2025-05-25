package io.github.com.quillraven.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class AssetService implements Disposable {
    private final AssetManager assetManager;

    public AssetService(FileHandleResolver fileHandleResolver) {
        this.assetManager = new AssetManager(fileHandleResolver);
        this.assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public TiledMap load(MapAsset mapAsset) {
        this.assetManager.load(mapAsset.getPath(), TiledMap.class);
        this.assetManager.finishLoading();
        return this.assetManager.get(mapAsset.getPath(), TiledMap.class);
    }

    public void unload(MapAsset mapAsset) {
        this.assetManager.unload(mapAsset.getPath());
        this.assetManager.finishLoading();
    }

    public void debugDiagnostics() {
        Gdx.app.debug("AssetService", this.assetManager.getDiagnostics());
    }

    @Override
    public void dispose() {
        this.assetManager.dispose();
    }
}
