package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.com.quillraven.component.Move;
import io.github.com.quillraven.component.Physic;

public class PhysicMoveSystem extends IteratingSystem {
    private static final Vector2 TMP_VEC2 = new Vector2();

    public PhysicMoveSystem() {
        super(Family.all(Physic.class, Move.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        Physic physic = Physic.MAPPER.get(entity);
        Body body = physic.getBody();
        if (move.isRooted() || move.getDirection().isZero()) {
            // no direction given or rooted -> stop movement
            body.setLinearVelocity(0f, 0f);
            return;
        }

        float maxSpeed = move.getMaxSpeed();
        TMP_VEC2.set(move.getDirection()).nor();
        body.setLinearVelocity(maxSpeed * TMP_VEC2.x, maxSpeed * TMP_VEC2.y);
    }
}
