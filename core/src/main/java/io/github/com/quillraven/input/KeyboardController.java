package io.github.com.quillraven.input;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class KeyboardController extends InputAdapter {
    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.D, Command.RIGHT)
    );

    private final boolean[] commandState;
    private final Map<Class<? extends ControllerState>, ControllerState> stateCache;
    private ControllerState activeState;

    public KeyboardController(Class<? extends ControllerState> initialState, ImmutableArray<Entity> controllerEntities) {
        this.commandState = new boolean[Command.values().length];
        this.stateCache = new HashMap<>();
        this.activeState = null;

        this.stateCache.put(IdleControllerState.class, new IdleControllerState());
        this.stateCache.put(GameControllerState.class, new GameControllerState(controllerEntities));
        setActiveState(initialState);
    }

    public void setActiveState(Class<? extends ControllerState> stateClass) {
        ControllerState state = stateCache.get(stateClass);
        if (state == null) {
            throw new GdxRuntimeException("State " + stateClass.getSimpleName() + " not found in cache");
        }

        for (Command command : Command.values()) {
            if (this.activeState != null && this.commandState[command.ordinal()]) {
                this.activeState.keyUp(command);
            }
            this.commandState[command.ordinal()] = false;
        }
        this.activeState = state;
    }

    @Override
    public boolean keyDown(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if (command == null) return false;

        this.commandState[command.ordinal()] = true;
        this.activeState.keyDown(command);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if (command == null) return false;
        // if a button was not pressed before, ignore it
        if (!this.commandState[command.ordinal()]) return false;

        this.commandState[command.ordinal()] = false;
        this.activeState.keyUp(command);
        return true;
    }
}
