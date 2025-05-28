package io.github.com.quillraven.input;

public interface ControllerState {
    void keyDown(Command command);

    void keyUp(Command command);
}
