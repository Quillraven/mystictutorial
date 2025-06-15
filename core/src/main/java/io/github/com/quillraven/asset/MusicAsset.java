package io.github.com.quillraven.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;

public enum MusicAsset implements Asset<Music> {
    TOWN("town.ogg"),
    MENU("menu.ogg");

    private final AssetDescriptor<Music> descriptor;

    MusicAsset(String musicFile) {
        this.descriptor = new AssetDescriptor<>("audio/" + musicFile, Music.class);
    }

    @Override
    public AssetDescriptor<Music> getDescriptor() {
        return descriptor;
    }
}
