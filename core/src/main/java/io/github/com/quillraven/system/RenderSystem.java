package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.component.Graphic;
import io.github.com.quillraven.component.Transform;

/**
 * System responsible for rendering entities with Transform and Graphic components.
 * Also handles rendering the tiled map.
 */
public class RenderSystem extends SortedIteratingSystem implements Disposable {
    private static final String MAP_PATH = "maps/mainmap.tmx";

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final AssetManager assetManager;

    private TiledMapRenderer mapRenderer;
    private TiledMap map;

    public RenderSystem(SpriteBatch batch, Viewport viewport, OrthographicCamera camera, AssetManager assetManager) {
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
        this.assetManager = assetManager;

        loadMap();
    }

    private void loadMap() {
        if (!assetManager.isLoaded(MAP_PATH)) {
            assetManager.setLoader(TiledMap.class, new TmxMapLoader());
            assetManager.load(MAP_PATH, TiledMap.class);
            assetManager.finishLoading(); // Block until loaded
        }

        map = assetManager.get(MAP_PATH, TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 32f, batch);
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
        if (graphic.region == null) {
            return;
        }

        batch.setColor(graphic.color);
        batch.draw(
            graphic.region,
            transform.position.x, transform.position.y,
            transform.size.x / 2, transform.size.y / 2,
            transform.size.x, transform.size.y,
            1, 1,
            0
        );
    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }
}
