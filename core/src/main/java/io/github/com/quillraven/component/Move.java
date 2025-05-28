package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Move implements Component {
    public static final ComponentMapper<Move> MAPPER = ComponentMapper.getFor(Move.class);

    private float maxSpeed;
    private final Vector2 direction;

    public Move(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.direction = new Vector2();
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public Vector2 getDirection() {
        return direction;
    }
}
