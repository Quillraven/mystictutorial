package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.com.quillraven.component.Facing;
import io.github.com.quillraven.component.Move;

public class FacingSystem extends IteratingSystem {

    public FacingSystem() {
        super(Family.all(Facing.class, Move.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        Vector2 moveDirection = move.getDirection();
        if (moveDirection.isZero()) {
            return;
        }

        Facing facing = Facing.MAPPER.get(entity);
        if (moveDirection.y > 0f) {
            facing.setDirection(Facing.FacingDirection.UP);
        } else if (moveDirection.y < 0f) {
            facing.setDirection(Facing.FacingDirection.DOWN);
        } else if (moveDirection.x > 0f) {
            facing.setDirection(Facing.FacingDirection.RIGHT);
        } else {
            facing.setDirection(Facing.FacingDirection.LEFT);
        }
    }
}
