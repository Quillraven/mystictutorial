package io.github.com.quillraven.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
        String path = mapAsset.getPath();
        this.assetManager.load(path, TiledMap.class);
        this.assetManager.finishLoading();
        return this.assetManager.get(path, TiledMap.class);
    }

    public void unload(MapAsset mapAsset) {
        this.assetManager.unload(mapAsset.getPath());
        this.assetManager.finishLoading();
    }

    public void queue(AtlasAsset atlasAsset) {
        this.assetManager.load(atlasAsset.getPath(), TextureAtlas.class);
    }

    public TextureAtlas get(AtlasAsset atlasAsset) {
        return this.assetManager.get(atlasAsset.getPath(), TextureAtlas.class);
    }

    public Music load(MusicAsset musicAsset) {
        String path = musicAsset.getPath();
        this.assetManager.load(path, Music.class);
        this.assetManager.finishLoading();
        return this.assetManager.get(path, Music.class);
    }

    public void unload(MusicAsset musicAsset) {
        this.assetManager.unload(musicAsset.getPath());
        this.assetManager.finishLoading();
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
