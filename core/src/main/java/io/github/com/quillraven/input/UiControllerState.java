package io.github.com.quillraven.input;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class UiControllerState implements ControllerState {

    private final Stage stage;

    public UiControllerState(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void keyDown(Command command) {
        this.stage.getRoot().fire(new UiEvent(command));
    }
}
