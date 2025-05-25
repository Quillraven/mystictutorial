package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.component.Graphic;
import io.github.com.quillraven.component.Transform;

public class RenderSystem extends SortedIteratingSystem {
    private final Batch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;

    public RenderSystem(Batch batch, Viewport viewport, OrthographicCamera camera) {
        super(
            Family.all(Transform.class, Graphic.class).get(),
            (entity1, entity2) -> {
                Transform transform1 = Transform.MAPPER.get(entity1);
                Transform transform2 = Transform.MAPPER.get(entity2);
                return transform1.compareTo(transform2);
            }
        );

        this.batch = batch;
        this.viewport = viewport;
        this.camera = camera;
        this.mapRenderer = new OrthogonalTiledMapRenderer(null, GdxGame.UNIT_SCALE, batch);
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Graphic graphic = Graphic.MAPPER.get(entity);
        if (graphic.region() == null) {
            return;
        }

        batch.setColor(graphic.color());
        batch.draw(
            graphic.region(),
            transform.position().x, transform.position().y,
            transform.size().x / 2, transform.size().y / 2,
            transform.size().x, transform.size().y,
            1, 1,
            0
        );
    }

    public void onMapChange(TiledMap tiledMap) {
        this.mapRenderer.setMap(tiledMap);
    }
}
