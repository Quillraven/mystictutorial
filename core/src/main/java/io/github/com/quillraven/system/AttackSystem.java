package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.component.Attack;
import io.github.com.quillraven.component.Damaged;
import io.github.com.quillraven.component.Facing;
import io.github.com.quillraven.component.Facing.FacingDirection;
import io.github.com.quillraven.component.Life;
import io.github.com.quillraven.component.Move;
import io.github.com.quillraven.component.Physic;

public class AttackSystem extends IteratingSystem {
    public static final Rectangle attackAABB = new Rectangle();

    private final AudioService audioService;
    private final World world;
    private final Vector2 tmpVertex;
    private Body attackerBody;
    private float attackDamage;

    public AttackSystem(World world, AudioService audioService) {
        super(Family.all(Attack.class, Facing.class, Physic.class).get());
        this.audioService = audioService;
        this.world = world;
        this.tmpVertex = new Vector2();
        this.attackerBody = null;
        this.attackDamage = 0f;
    }

    /**
     * Processes attack logic including sound effects and damage detection.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Attack attack = Attack.MAPPER.get(entity);
        // can attack = true means that the attack was not started yet
        if (attack.canAttack()) return;

        if (attack.hasAttackStarted() && attack.getSfx() != null) {
            audioService.playSound(attack.getSfx());
            Move move = Move.MAPPER.get(entity);
            if (move != null) {
                move.setRooted(true);
            }
        }

        attack.decAttackTimer(deltaTime);
        if (attack.canAttack()) {
            FacingDirection facingDirection = Facing.MAPPER.get(entity).getDirection();
            attackerBody = Physic.MAPPER.get(entity).getBody();
            PolygonShape attackPolygonShape = getAttackFixture(attackerBody, facingDirection);
            updateAttackAABB(attackerBody.getPosition(), attackPolygonShape);

            this.attackDamage = attack.getDamage();
            world.QueryAABB(this::attackCallback, attackAABB.x, attackAABB.y, attackAABB.width, attackAABB.height);

            Move move = Move.MAPPER.get(entity);
            if (move != null) {
                move.setRooted(false);
            }
        }
    }

    private boolean attackCallback(Fixture fixture) {
        Body body = fixture.getBody();
        if (body.equals(attackerBody)) return true;
        if (!(body.getUserData() instanceof Entity entity)) return true;

        Life life = Life.MAPPER.get(entity);
        if (life == null) {
            return true;
        }

        Damaged damaged = Damaged.MAPPER.get(entity);
        if (damaged == null) {
            entity.add(new Damaged(this.attackDamage));
        } else {
            damaged.addDamage(this.attackDamage);
        }
        return true;
    }

    private void updateAttackAABB(Vector2 bodyPosition, PolygonShape attackPolygonShape) {
        attackPolygonShape.getVertex(0, tmpVertex);
        tmpVertex.add(bodyPosition);
        attackAABB.setPosition(tmpVertex.x, tmpVertex.y);

        attackPolygonShape.getVertex(2, tmpVertex);
        tmpVertex.add(bodyPosition);
        attackAABB.setSize(tmpVertex.x, tmpVertex.y);
    }

    private PolygonShape getAttackFixture(Body body, FacingDirection direction) {
        Array<Fixture> fixtureList = body.getFixtureList();
        String fixtureName = "attack_sensor_" + direction.getAtlasKey();
        for (Fixture fixture : fixtureList) {
            if (fixtureName.equals(fixture.getUserData()) && Type.Polygon.equals(fixture.getShape().getType())) {
                return (PolygonShape) fixture.getShape();
            }
        }

        throw new GdxRuntimeException("Entity has no polygon attack sensor with userData '" + fixtureName + "'");
    }
}
