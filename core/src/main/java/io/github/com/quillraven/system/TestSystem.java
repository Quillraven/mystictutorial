package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.com.quillraven.component.Move;
import io.github.com.quillraven.tiled.TiledService;

public class TestSystem extends EntitySystem {
    private final TiledService tiledService;

    public TestSystem(TiledService tiledService) {
        super();
        this.tiledService = tiledService;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Move.class).get());
            for (Entity entity : entities) {
                Move.MAPPER.get(entity).getDirection().set(0f, 1f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Move.class).get());
            for (Entity entity : entities) {
                Move.MAPPER.get(entity).getDirection().set(0f, -1f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Move.class).get());
            for (Entity entity : entities) {
                Move.MAPPER.get(entity).getDirection().set(-1f, 0f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Move.class).get());
            for (Entity entity : entities) {
                Move.MAPPER.get(entity).getDirection().set(1f, 0f);
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Move.class).get());
            for (Entity entity : entities) {
                Move.MAPPER.get(entity).getDirection().setZero();
            }
        }
    }
}
