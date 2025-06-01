package io.github.com.quillraven.input;

public interface ControllerState {
    void keyDown(Command command);

    default void keyUp(Command command) {
    }
}
