package io.github.com.quillraven.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.MusicAsset;
import io.github.com.quillraven.asset.SkinAsset;
import io.github.com.quillraven.input.KeyboardController;
import io.github.com.quillraven.input.UiControllerState;
import io.github.com.quillraven.ui.model.MenuViewModel;
import io.github.com.quillraven.ui.view.MenuView;

public class MenuScreen extends ScreenAdapter {

    private final GdxGame game;
    private final Stage stage;
    private final Skin skin;
    private final Viewport uiViewport;
    private final KeyboardController keyboardController;

    public MenuScreen(GdxGame game) {
        this.game = game;
        this.uiViewport = new FitViewport(800f, 450f);
        this.stage = new Stage(uiViewport, game.getBatch());
        this.skin = game.getAssetService().get(SkinAsset.DEFAULT);
        this.keyboardController = new KeyboardController(UiControllerState.class, null, stage);
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        this.game.setInputProcessors(stage, keyboardController);

        this.stage.addActor(new MenuView(stage, skin, new MenuViewModel(game)));
        this.game.getAudioService().playMusic(MusicAsset.MENU);
    }

    @Override
    public void hide() {
        this.stage.clear();
    }

    @Override
    public void render(float delta) {
        uiViewport.apply();
        stage.getBatch().setColor(Color.WHITE);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
