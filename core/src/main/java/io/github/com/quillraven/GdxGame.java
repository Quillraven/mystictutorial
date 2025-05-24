package io.github.com.quillraven;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

public class GdxGame extends Game {
    public static final float WORLD_HEIGHT = 11f;
    public static final float WORLD_WIDTH = 6f;

    private SpriteBatch batch;
    private AssetManager assetManager;
    private OrthographicCamera camera;
    private Viewport viewport;

    private final HashMap<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        addScreen(new GameScreen(this));
        setScreen(GameScreen.class);
    }

    public void addScreen(Screen screen) {
        screenCache.put(screen.getClass(), screen);
    }

    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = screenCache.get(screenClass);
        if (screen == null) {
            throw new GdxRuntimeException("Screen " + screenClass.getSimpleName() + " not found in cache");
        }
        super.setScreen(screen);
    }

    public void removeScreen(Screen screen) {
        screenCache.remove(screen.getClass());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();

        batch.dispose();
        assetManager.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
