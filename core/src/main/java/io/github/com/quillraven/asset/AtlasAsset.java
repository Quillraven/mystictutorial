package io.github.com.quillraven.asset;

public enum AtlasAsset {
    OBJECTS("objects.atlas");

    private final String path;

    AtlasAsset(String atlasName) {
        this.path = "graphics/" + atlasName;
    }

    public String getPath() {
        return path;
    }
}
