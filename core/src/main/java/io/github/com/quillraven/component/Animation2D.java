package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.com.quillraven.asset.AtlasAsset;
import io.github.com.quillraven.component.Facing.FacingDirection;

public class Animation2D implements Component {
    public static final ComponentMapper<Animation2D> MAPPER = ComponentMapper.getFor(Animation2D.class);

    private final AtlasAsset atlasAsset;
    private final String atlasKey;
    private AnimationType type;
    private FacingDirection direction;
    private PlayMode playMode;
    private float speed;
    private float stateTime;
    private Animation<TextureRegion> animation;
    private boolean dirty;

    public Animation2D(AtlasAsset atlasAsset, String atlasKey, AnimationType type, PlayMode playMode, float speed) {
        this.atlasAsset = atlasAsset;
        this.atlasKey = atlasKey;
        this.type = type;
        this.playMode = playMode;
        this.speed = speed;
        this.stateTime = 0f;
        this.animation = null;
        this.dirty = true;
    }

    public void setAnimation(Animation<TextureRegion> animation, FacingDirection direction) {
        this.animation = animation;
        this.stateTime = 0f;
        this.direction = direction;
        this.dirty = false;
    }

    public FacingDirection getDirection() {
        return direction;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public AtlasAsset getAtlasAsset() {
        return atlasAsset;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public void setType(AnimationType type) {
        this.type = type;
        this.dirty = true;
    }

    public AnimationType getType() {
        return type;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public boolean isDirty() {
        return dirty;
    }

    public float incAndGetStateTime(float deltaTime) {
        this.stateTime += deltaTime * speed;
        return this.stateTime;
    }

    public enum AnimationType {
        IDLE,
        WALK;

        private final String atlasKey;

        AnimationType() {
            this.atlasKey = this.name().toLowerCase();
        }

        public String getAtlasKey() {
            return atlasKey;
        }
    }
}
