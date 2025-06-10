package io.github.com.quillraven.ui.model;

import com.badlogic.gdx.math.Vector2;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.SoundAsset;
import io.github.com.quillraven.audio.AudioService;

import java.util.Map;

public class GameViewModel extends ViewModel {
    public static final String LIFE_POINTS = "lifePoints";
    public static final String MAX_LIFE = "maxLife";
    public static final String PLAYER_DAMAGE = "playerDamage";

    private final AudioService audioService;
    private int lifePoints;
    private int maxLife;
    private final Vector2 tmpVec2;
    private Map.Entry<Vector2, Integer> playerDamage;

    public GameViewModel(GdxGame game) {
        super(game);
        this.audioService = game.getAudioService();
        this.lifePoints = 0;
        this.maxLife = 0;
        this.tmpVec2 = new Vector2();
        this.playerDamage = null;
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

    public void playerDamage(int amount, float x, float y) {
        tmpVec2.set(x, y);
        game.getViewport().project(tmpVec2);

        this.playerDamage = Map.entry(tmpVec2, amount);
        this.propertyChangeSupport.firePropertyChange(PLAYER_DAMAGE, null, this.playerDamage);
    }
}
