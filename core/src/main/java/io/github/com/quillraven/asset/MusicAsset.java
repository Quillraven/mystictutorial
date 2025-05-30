package io.github.com.quillraven.asset;

public enum MusicAsset {
    TOWN("town.ogg");

    private final String path;

    MusicAsset(String musicFile) {
        this.path = "audio/" + musicFile;
    }

    public String getPath() {
        return path;
    }
}
