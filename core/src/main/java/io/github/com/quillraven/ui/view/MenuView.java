package io.github.com.quillraven.ui.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import io.github.com.quillraven.ui.model.MenuViewModel;

public class MenuView extends View<MenuViewModel> {

    public MenuView(Stage stage, Skin skin, MenuViewModel viewModel) {
        super(stage, skin, viewModel);
    }

    @Override
    protected void setupUI() {
        setFillParent(true);

        Image image = new Image(skin, "banner");
        add(image).row();

        setupMenuContent();

        Label label = new Label("by Quillraven 2025", skin, "small");
        label.setColor(skin.getColor("white"));
        add(label).padRight(5.0f).padBottom(5f).expand().align(Align.bottomRight);
    }

    private void setupMenuContent() {
        Table contentTable = new Table();
        contentTable.setBackground(skin.getDrawable("frame"));
        contentTable.padLeft(40.0f);
        contentTable.padRight(40.0f);
        contentTable.padTop(25.0f);
        contentTable.padBottom(20.0f);

        TextButton textButton = new TextButton("Start Game", skin);
        onClick(textButton, ((event, x, y) -> viewModel.startGame()));
        contentTable.add(textButton).row();

        ProgressBar musicBar = setupVolumeBar(contentTable, "Music Volume", viewModel.getMusicVolume());
        viewModel.addPropertyChangeListener(MenuViewModel.MUSIC_VOLUME_PROPERTY, (event) ->
            musicBar.setValue((Float) event.getNewValue())
        );
        setupVolumeBar(contentTable, "Sound Volume", viewModel.getSoundVolume());

        textButton = new TextButton("Quit Game", skin);
        onClick(textButton, ((event, x, y) -> viewModel.quitGame()));
        contentTable.add(textButton).padTop(10.0f);

        add(contentTable).align(Align.top).expandY().padTop(20f).row();
    }

    private ProgressBar setupVolumeBar(Table contentTable, String title, float initialValue) {
        Table table = new Table();
        Label label = new Label(title, skin);
        label.setColor(skin.getColor("sand"));
        table.add(label).row();

        ProgressBar progressBar = new ProgressBar(0.0f, 1f, 0.05f, false, skin);
        progressBar.setValue(initialValue);
        table.add(progressBar).fill();
        contentTable.add(table).padTop(10.0f).row();
        return progressBar;
    }
}
