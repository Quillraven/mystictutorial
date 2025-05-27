package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMap;
import io.github.com.quillraven.asset.MapAsset;
import io.github.com.quillraven.component.Animation2D;
import io.github.com.quillraven.component.Facing;
import io.github.com.quillraven.component.Facing.FacingDirection;
import io.github.com.quillraven.tiled.TiledService;

public class TestSystem extends EntitySystem {
    private final TiledService tiledService;

    public TestSystem(TiledService tiledService) {
        super();
        this.tiledService = tiledService;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            Gdx.app.debug("TiledServiceTestSystem", "Setting map to MAIN");
            TiledMap tiledMap = tiledService.loadMap(MapAsset.MAIN);
            tiledService.setMap(tiledMap);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Gdx.app.debug("TiledServiceTestSystem", "Setting map to SECOND");
            TiledMap tiledMap = tiledService.loadMap(MapAsset.SECOND);
            tiledService.setMap(tiledMap);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Facing.class).get());
            for (Entity entity : entities) {
                Facing.MAPPER.get(entity).setDirection(FacingDirection.LEFT);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Facing.class).get());
            for (Entity entity : entities) {
                Facing.MAPPER.get(entity).setDirection(FacingDirection.RIGHT);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Animation2D.class).get());
            for (Entity entity : entities) {
                Facing.MAPPER.get(entity).setDirection(FacingDirection.DOWN);
                Animation2D.MAPPER.get(entity).setType(Animation2D.AnimationType.IDLE);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Animation2D.class).get());
            for (Entity entity : entities) {
                Animation2D animation2D = Animation2D.MAPPER.get(entity);
                animation2D.setSpeed(animation2D.getSpeed() * 1.2f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Animation2D.class).get());
            for (Entity entity : entities) {
                Animation2D animation2D = Animation2D.MAPPER.get(entity);
                animation2D.setSpeed(animation2D.getSpeed() / 1.2f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Animation2D.class).get());
            for (Entity entity : entities) {
                Animation2D.MAPPER.get(entity).setPlayMode(Animation.PlayMode.LOOP_RANDOM);
            }
        }
    }
}
