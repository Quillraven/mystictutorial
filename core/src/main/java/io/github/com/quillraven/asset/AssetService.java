package io.github.com.quillraven.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.github.tommyettinger.freetypist.FreeTypistSkinLoader;

public class AssetService implements Disposable {
    private final AssetManager assetManager;

    public AssetService(FileHandleResolver fileHandleResolver) {
        this.assetManager = new AssetManager(fileHandleResolver);
        this.assetManager.setLoader(TiledMap.class, new TmxMapLoader());
        assetManager.setLoader(Skin.class, new FreeTypistSkinLoader(assetManager.getFileHandleResolver()));
    }

    public <T> T load(Asset<T> asset) {
        this.assetManager.load(asset.getDescriptor());
        this.assetManager.finishLoading();
        return this.assetManager.get(asset.getDescriptor());
    }

    public <T> void unload(Asset<T> asset) {
        this.assetManager.unload(asset.getDescriptor().fileName);
        this.assetManager.finishLoading();
    }

    public <T> void queue(Asset<T> asset) {
        this.assetManager.load(asset.getDescriptor());
    }

    public <T> T get(Asset<T> asset) {
        return this.assetManager.get(asset.getDescriptor());
    }

    public boolean update() {
        return this.assetManager.update();
    }

    public void debugDiagnostics() {
        Gdx.app.debug("AssetService", this.assetManager.getDiagnostics());
    }

    @Override
    public void dispose() {
        this.assetManager.dispose();
    }
}
