package io.github.com.quillraven.asset;

public enum MapAsset {
    MAIN("mainmap.tmx");

    private final String path;

    MapAsset(String mapName) {
        this.path = "maps/" + mapName;
    }

    public String getPath() {
        return path;
    }
}
