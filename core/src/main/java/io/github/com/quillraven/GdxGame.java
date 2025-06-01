package io.github.com.quillraven;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.asset.AssetService;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.screen.LoadingScreen;

import java.util.HashMap;
import java.util.Map;

public class GdxGame extends Game {
    public static final float WORLD_HEIGHT = 9f;
    public static final float WORLD_WIDTH = 16f;
    public static final float UNIT_SCALE = 1f / 16f;

    private Batch batch;
    private AssetService assetService;
    private AudioService audioService;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GLProfiler glProfiler;
    private InputMultiplexer inputMultiplexer;

    private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        batch = new SpriteBatch();
        assetService = new AssetService(new InternalFileHandleResolver());
        audioService = new AudioService(assetService);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();

        addScreen(new LoadingScreen(this));
        setScreen(LoadingScreen.class);
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
        glProfiler.reset();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();

        Gdx.graphics.setTitle("Mystic Tutorial - Draw Calls: " + glProfiler.getDrawCalls());
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
        assetService.debugDiagnostics();
        assetService.dispose();
    }

    public Batch getBatch() {
        return batch;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setInputProcessors(InputProcessor... processors) {
        inputMultiplexer.clear();
        if (processors == null) return;

        for (InputProcessor processor : processors) {
            inputMultiplexer.addProcessor(processor);
        }
    }

    public AudioService getAudioService() {
        return audioService;
    }
}
