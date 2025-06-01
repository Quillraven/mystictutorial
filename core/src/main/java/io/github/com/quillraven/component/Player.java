package io.github.com.quillraven.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Player implements Component {
    public static final ComponentMapper<Player> MAPPER = ComponentMapper.getFor(Player.class);
}
