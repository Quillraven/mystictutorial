package io.github.com.quillraven.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.AssetService;
import io.github.com.quillraven.asset.AtlasAsset;

public class LoadingScreen extends ScreenAdapter {

    private final GdxGame game;
    private final AssetService assetService;

    public LoadingScreen(GdxGame game) {
        this.game = game;
        this.assetService = game.getAssetService();
    }

    @Override
    public void show() {
        for (AtlasAsset atlasAsset : AtlasAsset.values()) {
            assetService.queue(atlasAsset);
        }
    }

    @Override
    public void render(float delta) {
        if (assetService.update()) {
            Gdx.app.debug("LoadingScreen", "Finished loading assets");
            createScreens();
            this.game.removeScreen(this);
            this.dispose();
            this.game.setScreen(GameScreen.class);
        }
    }

    private void createScreens() {
        this.game.addScreen(new GameScreen(this.game));
    }
}
