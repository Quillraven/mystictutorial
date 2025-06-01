package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;

public class Life implements Component {
    public static final ComponentMapper<Life> MAPPER = ComponentMapper.getFor(Life.class);

    private float maxLife;
    private float life;
    private float lifePerSec;

    public Life(int maxLife, float lifePerSec) {
        this.maxLife = maxLife;
        this.life = maxLife;
        this.lifePerSec = lifePerSec;
    }

    public float getMaxLife() {
        return maxLife;
    }

    public float getLife() {
        return life;
    }

    public void addLife(float value) {
        this.life = MathUtils.clamp(life + value, 0f, maxLife);
    }

    public float getLifePerSec() {
        return lifePerSec;
    }
}
