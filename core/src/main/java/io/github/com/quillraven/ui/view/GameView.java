package io.github.com.quillraven.ui.view;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.com.quillraven.ui.model.GameViewModel;

public class GameView extends View<GameViewModel> {
    private final HorizontalGroup lifeGroup;

    public GameView(Stage stage, Skin skin, GameViewModel viewModel) {
        super(stage, skin, viewModel);

        this.lifeGroup = findActor("lifeGroup");
        updateLife(viewModel.getLifePoints());

        viewModel.onPropertyChange(GameViewModel.LIFE_POINTS, Integer.class, this::updateLife);
    }

    @Override
    protected void setupUI() {
        align(Align.bottomLeft);
        setFillParent(true);

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.setName("lifeGroup");
        horizontalGroup.padLeft(5.0f);
        horizontalGroup.padBottom(5.0f);
        horizontalGroup.space(5.0f);
        add(horizontalGroup);
    }

    private void updateLife(int lifePoints) {
        lifeGroup.clear();

        int maxLife = viewModel.getMaxLife();
        while (maxLife > 0) {
            int imgIdx = MathUtils.clamp(lifePoints, 0, 4);
            Image image = new Image(skin, "life_0" + imgIdx);
            lifeGroup.addActor(image);

            maxLife -= 4;
            lifePoints -= 4;
        }
    }
}
