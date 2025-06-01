package io.github.com.quillraven.ui.view;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.ui.model.MenuViewModel;

public class MenuView extends View<MenuViewModel> {
    private final Image selectionImg;
    private Group selectedItem;

    public MenuView(Stage stage, Skin skin, MenuViewModel viewModel) {
        super(stage, skin, viewModel);

        this.selectionImg = new Image(skin, "selection");
        this.selectionImg.setTouchable(Touchable.disabled);
        this.selectedItem = findActor(MenuOption.START_GAME.name());
        selectMenuItem(this.selectedItem);
    }

    private void selectMenuItem(Group menuItem) {
        if (selectionImg.getParent() != null) {
            selectionImg.getParent().removeActor(selectionImg);
        }
        this.selectedItem = menuItem;

        float extraSize = 7f;
        float halfExtraSize = extraSize * 0.5f;
        float resizeTime = 0.2f;

        menuItem.addActor(selectionImg);
        selectionImg.setPosition(-halfExtraSize, -halfExtraSize);
        selectionImg.setSize(menuItem.getWidth() + extraSize, menuItem.getHeight() + extraSize);
        selectionImg.clearActions();
        selectionImg.addAction(Actions.forever(Actions.sequence(
            Actions.parallel(
                Actions.sizeBy(extraSize, extraSize, resizeTime, Interpolation.linear),
                Actions.moveBy(-halfExtraSize, -halfExtraSize, resizeTime, Interpolation.linear)
            ),
            Actions.parallel(
                Actions.sizeBy(-extraSize, -extraSize, resizeTime, Interpolation.linear),
                Actions.moveBy(halfExtraSize, halfExtraSize, resizeTime, Interpolation.linear)
            )
        )));
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
        textButton.setName(MenuOption.START_GAME.name());
        onClick(textButton, viewModel::startGame);
        onEnter(textButton, this::selectMenuItem);
        contentTable.add(textButton).row();

        Slider musicSlider = setupVolumeSlider(contentTable, "Music Volume", MenuOption.MUSIC_VOLUME);
        musicSlider.setValue(viewModel.getMusicVolume());
        onChange(musicSlider, (slider) -> viewModel.setMusicVolume(slider.getValue()));

        Slider soundSlider = setupVolumeSlider(contentTable, "Sound Volume", MenuOption.SOUND_VOLUME);
        soundSlider.setValue(viewModel.getSoundVolume());
        onChange(soundSlider, (slider) -> viewModel.setSoundVolume(slider.getValue()));

        textButton = new TextButton("Quit Game", skin);
        textButton.setName(MenuOption.QUIT_GAME.name());
        onClick(textButton, viewModel::quitGame);
        onEnter(textButton, this::selectMenuItem);
        contentTable.add(textButton).padTop(10.0f);

        add(contentTable).align(Align.top).expandY().padTop(20f).row();
    }

    private Slider setupVolumeSlider(Table contentTable, String title, MenuOption menuOption) {
        Table table = new Table();
        table.setName(menuOption.name());
        Label label = new Label(title, skin);
        label.setColor(skin.getColor("sand"));
        table.add(label).row();

        Slider slider = new Slider(0.0f, 1f, 0.05f, false, skin);
        table.add(slider);
        contentTable.add(table).padTop(10.0f).row();

        onEnter(table, this::selectMenuItem);
        return slider;
    }

    @Override
    public void onDown() {
        Group menuContentTable = this.selectedItem.getParent();
        int currentIdx = menuContentTable.getChildren().indexOf(this.selectedItem, true);
        if (currentIdx == -1) {
            throw new GdxRuntimeException("'selectedItem' is not a child of 'menuContentTable'");
        }

        int numOptions = menuContentTable.getChildren().size;
        currentIdx = (currentIdx + 1) % numOptions;
        selectMenuItem((Group) menuContentTable.getChild(currentIdx));
    }

    @Override
    public void onUp() {
        Group menuContentTable = this.selectedItem.getParent();
        int currentIdx = menuContentTable.getChildren().indexOf(this.selectedItem, true);
        if (currentIdx == -1) {
            throw new GdxRuntimeException("'selectedItem' is not a child of 'menuContentTable'");
        }

        int numOptions = menuContentTable.getChildren().size;
        currentIdx = currentIdx == 0 ? numOptions - 1 : currentIdx - 1;
        selectMenuItem((Group) menuContentTable.getChild(currentIdx));
    }

    @Override
    public void onRight() {
        MenuOption menuOption = MenuOption.valueOf(this.selectedItem.getName());
        switch (menuOption) {
            case MUSIC_VOLUME, SOUND_VOLUME -> {
                Slider slider = (Slider) this.selectedItem.getChild(1);
                slider.setValue(slider.getValue() + slider.getStepSize());
            }
        }
    }

    @Override
    public void onLeft() {
        MenuOption menuOption = MenuOption.valueOf(this.selectedItem.getName());
        switch (menuOption) {
            case MUSIC_VOLUME, SOUND_VOLUME -> {
                Slider slider = (Slider) this.selectedItem.getChild(1);
                slider.setValue(slider.getValue() - slider.getStepSize());
            }
        }
    }

    @Override
    public void onSelect() {
        MenuOption menuOption = MenuOption.valueOf(this.selectedItem.getName());
        switch (menuOption) {
            case START_GAME -> viewModel.startGame();
            case QUIT_GAME -> viewModel.quitGame();
        }
    }

    private enum MenuOption {
        START_GAME,
        MUSIC_VOLUME,
        SOUND_VOLUME,
        QUIT_GAME
    }
}
