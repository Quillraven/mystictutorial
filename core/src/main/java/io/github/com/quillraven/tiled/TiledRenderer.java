package io.github.com.quillraven.tiled;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.com.quillraven.GdxGame;

import java.util.List;

public class TiledRenderer extends OrthogonalTiledMapRenderer {
    public TiledRenderer(Batch batch) {
        super(null, GdxGame.UNIT_SCALE, batch);
    }

    public void renderLayers(List<MapLayer> layers) {
        for (MapLayer layer : layers) {
            this.renderMapLayer(layer);
        }
    }
}
