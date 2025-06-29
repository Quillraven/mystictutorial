package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import io.github.com.quillraven.component.Physic;
import io.github.com.quillraven.component.Player;
import io.github.com.quillraven.component.Transform;
import io.github.com.quillraven.component.Trigger;

public class PhysicSystem extends IteratingSystem implements EntityListener, ContactListener {

    private final World world;
    private final float interval;
    private float accumulator;

    public PhysicSystem(World world, float interval) {
        super(Family.all(Physic.class, Transform.class).get());
        this.world = world;
        this.interval = interval;
        this.accumulator = 0f;
        world.setContactListener(this);
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

    /**
     * Updates physics simulation with fixed timestep and interpolation.
     */
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

    /**
     * Stores previous position for interpolation.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Physic physic = Physic.MAPPER.get(entity);
        physic.getPrevPosition().set(physic.getBody().getPosition());
    }

    /**
     * Interpolates entity position between physics steps.
     */
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

    /**
     * Handles collision detection between entities and triggers.
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Object userDataA = fixtureA.getBody().getUserData();
        Fixture fixtureB = contact.getFixtureB();
        Object userDataB = fixtureB.getBody().getUserData();

        if (!(userDataA instanceof Entity entityA) || !(userDataB instanceof Entity entityB)) {
            return;
        }

        playerTriggerContact(entityA, fixtureA, entityB, fixtureB);
    }

    private static void playerTriggerContact(Entity entityA, Fixture fixtureA, Entity entityB, Fixture fixtureB) {
        Trigger trigger = Trigger.MAPPER.get(entityA);
        boolean isPlayer = Player.MAPPER.get(entityB) != null && !fixtureB.isSensor();
        if (trigger != null && isPlayer) {
            trigger.setTriggeringEntity(entityB);
            return;
        }

        trigger = Trigger.MAPPER.get(entityB);
        isPlayer = Player.MAPPER.get(entityA) != null && !fixtureA.isSensor();
        if (trigger != null && isPlayer) {
            trigger.setTriggeringEntity(entityA);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
