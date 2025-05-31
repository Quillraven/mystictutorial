package io.github.com.quillraven.asset;

public enum SkinAsset {
    DEFAULT("skin.json");

    private final String path;

    SkinAsset(String skinJsonFile) {
        this.path = "ui/" + skinJsonFile;
    }

    public String getPath() {
        return path;
    }
}
