package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public record Physic(
    Body body,
    Vector2 prevPosition
) implements Component {
    public static final ComponentMapper<Physic> MAPPER = ComponentMapper.getFor(Physic.class);
}
