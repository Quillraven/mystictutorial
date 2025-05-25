package io.github.com.quillraven.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.com.quillraven.asset.MapAsset;
import io.github.com.quillraven.tiled.TiledService;

public class TiledServiceTestSystem extends EntitySystem {
    private final TiledService tiledService;

    public TiledServiceTestSystem(TiledService tiledService) {
        super();
        this.tiledService = tiledService;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            Gdx.app.debug("TiledServiceTestSystem", "Setting map to MAIN");
            var tiledMap = tiledService.loadMap(MapAsset.MAIN);
            tiledService.setMap(tiledMap);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Gdx.app.debug("TiledServiceTestSystem", "Setting map to SECOND");
            var tiledMap = tiledService.loadMap(MapAsset.SECOND);
            tiledService.setMap(tiledMap);
        }
    }
}
