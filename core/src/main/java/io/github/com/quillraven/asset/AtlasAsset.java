package io.github.com.quillraven.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum AtlasAsset implements Asset<TextureAtlas> {
    OBJECTS("objects.atlas");

    private final AssetDescriptor<TextureAtlas> descriptor;

    AtlasAsset(String atlasName) {
        this.descriptor = new AssetDescriptor<>("graphics/" + atlasName, TextureAtlas.class);
    }

    @Override
    public AssetDescriptor<TextureAtlas> getDescriptor() {
        return descriptor;
    }
}
