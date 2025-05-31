package io.github.com.quillraven.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.SkinAsset;

public class MenuScreen extends ScreenAdapter {

    private final GdxGame game;
    private final Stage stage;
    private final Skin skin;
    private final Viewport uiViewport;
    private final InputMultiplexer inputMultiplexer;

    public MenuScreen(GdxGame game) {
        this.game = game;
        this.uiViewport = new FitViewport(800f, 450f);
        this.stage = new Stage(uiViewport, game.getBatch());
        this.skin = game.getAssetService().get(SkinAsset.DEFAULT);
        this.inputMultiplexer = game.getInputMultiplexer();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        this.inputMultiplexer.clear();
        this.inputMultiplexer.addProcessor(stage);

        menuUI();
    }

    @Override
    public void hide() {
        this.stage.clear();
    }

    private void menuUI() {
        Table table = new Table();
        table.setFillParent(true);

        Image image = new Image(skin, "banner");
        table.add(image);

        table.row();
        Table table1 = new Table();
        table1.setBackground(skin.getDrawable("frame"));
        table1.padLeft(40.0f);
        table1.padRight(40.0f);
        table1.padTop(25.0f);
        table1.padBottom(20.0f);

        TextButton textButton = new TextButton("Start Game", skin);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(GameScreen.class);
            }
        });
        table1.add(textButton);

        table1.row();
        Table table2 = new Table();

        Label label = new Label("Music Volume", skin);
        label.setColor(skin.getColor("sand"));
        table2.add(label);

        table2.row();
        ProgressBar progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);
        table2.add(progressBar);
        table1.add(table2).padTop(10.0f);

        table1.row();
        table2 = new Table();

        label = new Label("Sound Volume", skin);
        label.setColor(skin.getColor("sand"));
        table2.add(label);

        table2.row();
        progressBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, skin);
        table2.add(progressBar);
        table1.add(table2).padTop(10.0f);

        table1.row();
        textButton = new TextButton("Quit Game", skin);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table1.add(textButton).padTop(10.0f);
        table.add(table1).align(Align.top).expandY().padTop(20f);

        table.row();
        label = new Label("by Quillraven 2025", skin, "small");
        label.setColor(skin.getColor("white"));
        table.add(label).padRight(5.0f).padBottom(5f).expand().align(Align.bottomRight);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
