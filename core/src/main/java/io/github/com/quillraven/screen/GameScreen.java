package io.github.com.quillraven.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.MapAsset;
import io.github.com.quillraven.system.RenderSystem;
import io.github.com.quillraven.system.TiledServiceTestSystem;
import io.github.com.quillraven.tiled.TiledService;

public class GameScreen extends ScreenAdapter {
    private final TiledService tiledService;
    private final Engine engine;

    public GameScreen(GdxGame game) {
        this.tiledService = new TiledService(game.getAssetService());
        this.engine = new Engine();
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(), game.getCamera()));
        this.engine.addSystem(new TiledServiceTestSystem(this.tiledService));
    }

    @Override
    public void show() {
        this.tiledService.setMapChangeConsumer(this.engine.getSystem(RenderSystem.class)::setMap);
        TiledMap startMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(startMap);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 30f, delta);
        engine.update(delta);
    }
}
