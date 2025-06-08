package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.com.quillraven.component.Damaged;
import io.github.com.quillraven.component.Life;

public class DamagedSystem extends IteratingSystem {

    public DamagedSystem() {
        super(Family.all(Damaged.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Damaged damaged = Damaged.MAPPER.get(entity);
        entity.remove(Damaged.class);

        Life life = Life.MAPPER.get(entity);
        if (life != null) {
            life.addLife(-damaged.getDamage());
        }
    }
}
