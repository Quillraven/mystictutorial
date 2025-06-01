package io.github.com.quillraven.asset;

public enum SoundAsset {
    SWORD_HIT("sword_hit.wav");

    private final String path;

    SoundAsset(String musicFile) {
        this.path = "audio/" + musicFile;
    }

    public String getPath() {
        return path;
    }
}
