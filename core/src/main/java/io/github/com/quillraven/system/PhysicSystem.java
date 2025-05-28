package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.com.quillraven.component.Physic;
import io.github.com.quillraven.component.Transform;

public class PhysicSystem extends IteratingSystem implements EntityListener {

    private final World world;
    private final float interval;
    private float accumulator;

    public PhysicSystem(World world, float interval) {
        super(Family.all(Physic.class, Transform.class).get());
        this.world = world;
        this.interval = interval;
        this.accumulator = 0f;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }

    @Override
    public void update(float deltaTime) {
        this.accumulator += deltaTime;

        while (this.accumulator >= this.interval) {
            this.accumulator -= this.interval;
            super.update(interval);
            this.world.step(interval, 6, 2);
        }
        world.clearForces();

        float alpha = this.accumulator / this.interval;
        for (int i = 0; i < getEntities().size(); ++i) {
            this.interpolateEntity(getEntities().get(i), alpha);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Physic physic = Physic.MAPPER.get(entity);
        physic.getPrevPosition().set(physic.getBody().getPosition());
    }

    private void interpolateEntity(Entity entity, float alpha) {
        Transform transform = Transform.MAPPER.get(entity);
        Physic physic = Physic.MAPPER.get(entity);

        transform.getPosition().set(
            MathUtils.lerp(physic.getPrevPosition().x, physic.getBody().getPosition().x, alpha),
            MathUtils.lerp(physic.getPrevPosition().y, physic.getBody().getPosition().y, alpha)
        );
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
