package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.component.Graphic;
import io.github.com.quillraven.component.Transform;
import io.github.com.quillraven.tiled.TiledRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderSystem extends SortedIteratingSystem implements Disposable {
    private final Batch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final TiledRenderer tiledRenderer;
    private final List<MapLayer> fgdLayers;
    private final List<MapLayer> bgdLayers;

    public RenderSystem(Batch batch, Viewport viewport, OrthographicCamera camera) {
        super(
            Family.all(Transform.class, Graphic.class).get(),
            Comparator.comparing(Transform.MAPPER::get)
        );

        this.batch = batch;
        this.viewport = viewport;
        this.camera = camera;
        this.tiledRenderer = new TiledRenderer(batch);
        this.fgdLayers = new ArrayList<>();
        this.bgdLayers = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        viewport.apply();

        batch.begin();
        batch.setColor(Color.WHITE);
        this.tiledRenderer.setView(camera);
        this.tiledRenderer.renderLayers(bgdLayers);

        forceSort();
        super.update(deltaTime);

        batch.setColor(Color.WHITE);
        this.tiledRenderer.renderLayers(fgdLayers);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Graphic graphic = Graphic.MAPPER.get(entity);
        if (graphic.region() == null) {
            return;
        }

        Vector2 position = transform.position();
        Vector2 size = transform.size();
        batch.setColor(graphic.color());
        batch.draw(
            graphic.region(),
            position.x, position.y,
            size.x * 0.5f, size.y * 0.5f,
            size.x, size.y,
            1, 1,
            0
        );
    }

    public void setMap(TiledMap tiledMap) {
        this.tiledRenderer.setMap(tiledMap);

        this.fgdLayers.clear();
        this.bgdLayers.clear();
        List<MapLayer> currentLayers = bgdLayers;
        for (MapLayer layer : tiledMap.getLayers()) {
            if ("objects".equals(layer.getName())) {
                currentLayers = fgdLayers;
                continue;
            }
            if (layer.getClass().equals(MapLayer.class)) {
                continue;
            }
            currentLayers.add(layer);
        }
    }

    @Override
    public void dispose() {
        this.tiledRenderer.dispose();
    }
}
