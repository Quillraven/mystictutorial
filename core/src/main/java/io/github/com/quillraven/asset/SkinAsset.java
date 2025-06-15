package io.github.com.quillraven.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public enum SkinAsset implements Asset<Skin> {
    DEFAULT("skin.json");

    private final AssetDescriptor<Skin> descriptor;

    SkinAsset(String skinJsonFile) {
        this.descriptor = new AssetDescriptor<>("ui/" + skinJsonFile, Skin.class);
    }

    @Override
    public AssetDescriptor<Skin> getDescriptor() {
        return descriptor;
    }
}
