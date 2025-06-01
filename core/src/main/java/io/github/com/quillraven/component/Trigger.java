package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class Trigger implements Component {
    public static final ComponentMapper<Trigger> MAPPER = ComponentMapper.getFor(Trigger.class);

    private final String name;
    private Entity triggeringEntity;

    public Trigger(String name) {
        this.name = name;
        this.triggeringEntity = null;
    }

    public String getName() {
        return name;
    }

    public void setTriggeringEntity(Entity triggeringEntity) {
        this.triggeringEntity = triggeringEntity;
    }

    public Entity getTriggeringEntity() {
        return triggeringEntity;
    }
}
