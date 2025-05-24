package io.github.com.quillraven;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.system.RenderSystem;

public class GameScreen extends ScreenAdapter {
    private final GdxGame game;

    private final SpriteBatch batch;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final OrthographicCamera camera;

    private Engine engine;

    public GameScreen(GdxGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.assetManager = game.getAssetManager();
        this.viewport = game.getViewport();
        this.camera = game.getCamera();
    }

    @Override
    public void show() {
        engine = new Engine();
        engine.addSystem(new RenderSystem(batch, viewport, camera, assetManager));
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 30f, delta);
        engine.update(delta);
    }
}
