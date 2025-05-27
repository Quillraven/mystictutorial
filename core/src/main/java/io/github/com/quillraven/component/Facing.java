package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Facing implements Component {
    public static final ComponentMapper<Facing> MAPPER = ComponentMapper.getFor(Facing.class);

    private FacingDirection direction;

    public Facing(FacingDirection direction) {
        this.direction = direction;
    }

    public FacingDirection getDirection() {
        return direction;
    }

    public void setDirection(FacingDirection direction) {
        this.direction = direction;
    }

    public enum FacingDirection {
        UP, DOWN, LEFT, RIGHT;

        private final String atlasKey;

        FacingDirection() {
            this.atlasKey = this.name().toLowerCase();
        }

        public String getAtlasKey() {
            return atlasKey;
        }
    }
}
