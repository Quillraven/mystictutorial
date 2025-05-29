package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Transform implements Component, Comparable<Transform> {
    public static final ComponentMapper<Transform> MAPPER = ComponentMapper.getFor(Transform.class);

    private final Vector2 position;
    private final int z;
    private final Vector2 size;
    private final Vector2 scaling;
    private float rotationDeg;
    private float sortOffsetY;

    public Transform(
        Vector2 position,
        int z,
        Vector2 size,
        Vector2 scaling,
        float rotationDeg,
        float sortOffsetY
    ) {
        this.position = position;
        this.z = z;
        this.size = size;
        this.scaling = scaling;
        this.rotationDeg = rotationDeg;
        this.sortOffsetY = sortOffsetY;
    }

    @Override
    public int compareTo(Transform other) {
        if (this.z != other.z) {
            return Float.compare(this.z, other.z);
        }
        if (this.position.y + this.sortOffsetY != other.position.y + other.sortOffsetY) {
            return Float.compare(other.position.y + other.sortOffsetY, this.position.y + this.sortOffsetY);
        }
        return Float.compare(this.position.x, other.position.x);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getSize() {
        return size;
    }

    public Vector2 getScaling() {
        return scaling;
    }

    public float getRotationDeg() {
        return rotationDeg;
    }

}
