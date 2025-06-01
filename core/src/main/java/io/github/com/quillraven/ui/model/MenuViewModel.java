package io.github.com.quillraven.ui.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.SoundAsset;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.screen.GameScreen;

public class MenuViewModel extends ViewModel {

    private final AudioService audioService;
    private long lastSndPlayTime;

    public MenuViewModel(GdxGame game) {
        super(game);
        this.audioService = game.getAudioService();
        this.lastSndPlayTime = 0L;
    }

    public float getMusicVolume() {
        return audioService.getMusicVolume();
    }

    public void setMusicVolume(float volume) {
        this.audioService.setMusicVolume(volume);
    }

    public float getSoundVolume() {
        return audioService.getSoundVolume();
    }

    public void setSoundVolume(float soundVolume) {
        this.audioService.setSoundVolume(soundVolume);
        if (TimeUtils.timeSinceMillis(lastSndPlayTime) > 500L) {
            this.lastSndPlayTime = TimeUtils.millis();
            this.audioService.playSound(SoundAsset.SWORD_HIT);
        }
    }

    public void startGame() {
        game.setScreen(GameScreen.class);
    }

    public void quitGame() {
        Gdx.app.exit();
    }
}
