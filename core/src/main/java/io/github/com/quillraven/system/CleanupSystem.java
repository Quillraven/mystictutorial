package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.com.quillraven.component.Physic;

public class CleanupSystem extends EntitySystem implements EntityListener {

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        // !!! Important !!!
        // This does not work if the Physic component gets removed from an entity
        // because the component is no longer accessible here.
        // This ONLY works when an entity with a Physic component gets removed entirely from the engine.
        Physic physic = Physic.MAPPER.get(entity);
        if (physic != null) {
            Body body = physic.getBody();
            body.getWorld().destroyBody(body);
        }
    }
}
