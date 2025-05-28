package io.github.com.quillraven.input;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.com.quillraven.component.Move;

public class GameControllerState implements ControllerState {

    private final ImmutableArray<Entity> controllerEntities;

    public GameControllerState(ImmutableArray<Entity> controllerEntities) {
        this.controllerEntities = controllerEntities;
    }

    @Override
    public void keyDown(Command command) {
        switch (command) {
            case UP:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().y += 1f;
                    }
                }
                break;
            case DOWN:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().y -= 1f;
                    }
                }
                break;
            case LEFT:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().x -= 1f;
                    }
                }
                break;
            case RIGHT:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().x += 1f;
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void keyUp(Command command) {
        switch (command) {
            case UP:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().y -= 1f;
                    }
                }
                break;
            case DOWN:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().y += 1f;
                    }
                }
                break;
            case LEFT:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().x += 1f;
                    }
                }
                break;
            case RIGHT:
                for (Entity entity : controllerEntities) {
                    Move move = Move.MAPPER.get(entity);
                    if (move != null) {
                        move.getDirection().x -= 1f;
                    }
                }
                break;
            default:
        }
    }
}
