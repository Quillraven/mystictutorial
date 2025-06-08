package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Attack implements Component {
    public static final ComponentMapper<Attack> MAPPER = ComponentMapper.getFor(Attack.class);

    private float damage;
    private float damageDelay;
    private float attackTimer;

    public Attack(float damage, float damageDelay) {
        this.damage = damage;
        this.damageDelay = damageDelay;
        this.attackTimer = 0f;
    }

    public boolean canAttack() {
        return this.attackTimer == 0f;
    }

    public boolean isAttacking() {
        return this.attackTimer > 0f;
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

}
