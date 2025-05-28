package io.github.com.quillraven.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.Map;

public class KeyboardController extends InputAdapter {
    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.D, Command.RIGHT)
    );

    private final boolean[] commandState;
    private ControllerState activeState;

    public KeyboardController(ControllerState initialState) {
        this.commandState = new boolean[Command.values().length];
        setActiveState(initialState);
    }

    public void setActiveState(ControllerState controllerState) {
        for (Command command : Command.values()) {
            this.commandState[command.ordinal()] = false;
        }
        this.activeState = controllerState;
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
