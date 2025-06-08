package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;
import io.github.com.quillraven.asset.SoundAsset;

public class Attack implements Component {
    public static final ComponentMapper<Attack> MAPPER = ComponentMapper.getFor(Attack.class);

    private float damage;
    private float damageDelay;
    private float attackTimer;
    private SoundAsset sfx;

    public Attack(float damage, float damageDelay, SoundAsset sfx) {
        this.damage = damage;
        this.damageDelay = damageDelay;
        this.sfx = sfx;
        this.attackTimer = 0f;
    }

    public boolean canAttack() {
        return this.attackTimer == 0f;
    }

    public boolean isAttacking() {
        return this.attackTimer > 0f;
    }

    public boolean hasAttackStarted() {
        return MathUtils.isEqual(this.attackTimer, this.damageDelay, 0.0001f);
    }

    public void startAttack() {
        this.attackTimer = this.damageDelay;
    }

    public void decAttackTimer(float deltaTime) {
        attackTimer = Math.max(0f, attackTimer - deltaTime);
    }

    public float getDamage() {
        return damage;
    }

    public SoundAsset getSfx() {
        return sfx;
    }
}
