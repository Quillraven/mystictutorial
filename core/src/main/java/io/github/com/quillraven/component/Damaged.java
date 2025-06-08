package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Damaged implements Component {
    public static final ComponentMapper<Damaged> MAPPER = ComponentMapper.getFor(Damaged.class);

    private float damage;

    public Damaged(float damage) {
        this.damage = damage;
    }

    public void addDamage(float amount) {
        this.damage += amount;
    }

    public float getDamage() {
        return damage;
    }
}
