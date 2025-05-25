package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public record Transform(
    Vector2 position,
    int z,
    Vector2 size,
    Vector2 scaling,
    float rotationDeg
) implements Component, Comparable<Transform> {
    public static final ComponentMapper<Transform> MAPPER = ComponentMapper.getFor(Transform.class);

    @Override
    public int compareTo(Transform other) {
        if (this.z != other.z) {
            return Float.compare(this.z, other.z);
        }
        if (this.position.y != other.position.y) {
            return Float.compare(other.position.y, this.position.y);
        }
        return Float.compare(this.position.x, other.position.x);
    }
}
