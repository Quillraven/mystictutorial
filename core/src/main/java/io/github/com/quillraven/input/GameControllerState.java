package io.github.com.quillraven.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.com.quillraven.component.Controller;
import io.github.com.quillraven.component.Move;

public class GameControllerState implements ControllerState {

    private final ImmutableArray<Entity> controllerEntities;

    public GameControllerState(Engine engine) {
        this.controllerEntities = engine.getEntitiesFor(Family.all(Controller.class).get());
    }

    private void moveEntities(float dx, float dy) {
        for (Entity entity : controllerEntities) {
            Move move = Move.MAPPER.get(entity);
            if (move != null) {
                move.getDirection().x += dx;
                move.getDirection().y += dy;
            }
        }
    }

    @Override
    public void keyDown(Command command) {
        switch (command) {
            case UP -> moveEntities(0f, 1f);
            case DOWN -> moveEntities(0f, -1f);
            case LEFT -> moveEntities(-1f, 0f);
            case RIGHT -> moveEntities(1f, 0f);
        }
    }

    @Override
    public void keyUp(Command command) {
        switch (command) {
            case UP -> moveEntities(0f, -1f);
            case DOWN -> moveEntities(0f, 1f);
            case LEFT -> moveEntities(1f, 0f);
            case RIGHT -> moveEntities(-1f, 0f);
        }
    }
}
