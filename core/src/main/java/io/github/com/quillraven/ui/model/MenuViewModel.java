package io.github.com.quillraven.ui.model;

import com.badlogic.gdx.Gdx;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.screen.GameScreen;

public class MenuViewModel extends ViewModel {
    public static final String MUSIC_VOLUME_PROPERTY = "musicVolume";

    private final AudioService audioService;

    public MenuViewModel(GdxGame game) {
        super(game);
        this.audioService = game.getAudioService();
    }

    public float getMusicVolume() {
        return audioService.getMusicVolume();
    }

    public void setMusicVolume(float volume) {
        this.propertyChangeSupport.firePropertyChange(MUSIC_VOLUME_PROPERTY, getMusicVolume(), volume);
        this.audioService.setMusicVolume(volume);
    }

    public float getSoundVolume() {
        return audioService.getSoundVolume();
    }

    public void startGame() {
        game.setScreen(GameScreen.class);
    }

    public void quitGame() {
        Gdx.app.exit();
    }
}
