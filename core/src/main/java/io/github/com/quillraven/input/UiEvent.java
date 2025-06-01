package io.github.com.quillraven.input;

import com.badlogic.gdx.scenes.scene2d.Event;

public class UiEvent extends Event {
    private final Command command;

    public UiEvent(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
