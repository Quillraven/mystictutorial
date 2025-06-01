package io.github.com.quillraven.ui.model;

import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.SoundAsset;
import io.github.com.quillraven.audio.AudioService;

public class GameViewModel extends ViewModel {
    public static final String LIFE_POINTS = "lifePoints";
    public static final String MAX_LIFE = "maxLife";

    private final AudioService audioService;
    private int lifePoints;
    private int maxLife;

    public GameViewModel(GdxGame game) {
        super(game);
        this.audioService = game.getAudioService();
        this.lifePoints = 0;
        this.maxLife = 0;
    }

    public void setMaxLife(int maxLife) {
        if (this.maxLife != maxLife) {
            this.propertyChangeSupport.firePropertyChange(MAX_LIFE, this.maxLife, maxLife);
        }
        this.maxLife = maxLife;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setLifePoints(int lifePoints) {
        if (this.lifePoints != lifePoints) {
            this.propertyChangeSupport.firePropertyChange(LIFE_POINTS, this.lifePoints, lifePoints);
            if (this.lifePoints != 0 && this.lifePoints < lifePoints) {
                audioService.playSound(SoundAsset.LIFE_REG);
            }
        }
        this.lifePoints = lifePoints;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public void updateLifeInfo(float maxLife, float life) {
        setMaxLife((int) maxLife);
        setLifePoints((int) life);
    }
}
