package io.github.com.quillraven.ui.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.com.quillraven.ui.model.ViewModel;

public abstract class View<T extends ViewModel> extends Table {

    protected final Stage stage;
    protected final Skin skin;
    protected final T viewModel;

    public View(Stage stage, Skin skin, T viewModel) {
        super(skin);
        this.stage = stage;
        this.skin = skin;
        this.viewModel = viewModel;
        setupUI();
    }

    protected abstract void setupUI();

    public static void onClick(Actor actor, OnClickConsumer consumer) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                consumer.onClick(event, x, y);
            }
        });
    }

    @FunctionalInterface
    public interface OnClickConsumer {
        void onClick(InputEvent event, float x, float y);
    }
}
