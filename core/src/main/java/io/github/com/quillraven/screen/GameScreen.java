package io.github.com.quillraven.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.MapAsset;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.component.Controller;
import io.github.com.quillraven.input.GameControllerState;
import io.github.com.quillraven.input.KeyboardController;
import io.github.com.quillraven.system.AnimationSystem;
import io.github.com.quillraven.system.CameraSystem;
import io.github.com.quillraven.system.FacingSystem;
import io.github.com.quillraven.system.FsmSystem;
import io.github.com.quillraven.system.PhysicDebugRenderSystem;
import io.github.com.quillraven.system.PhysicMoveSystem;
import io.github.com.quillraven.system.PhysicSystem;
import io.github.com.quillraven.system.RenderSystem;
import io.github.com.quillraven.tiled.TiledAshleySpawner;
import io.github.com.quillraven.tiled.TiledService;

import java.util.function.Consumer;

public class GameScreen extends ScreenAdapter {
    private final GdxGame game;
    private final TiledService tiledService;
    private final Engine engine;
    private final TiledAshleySpawner tiledAshleySpawner;
    private final World physicWorld;
    private final KeyboardController keyboardController;
    private final AudioService audioService;

    public GameScreen(GdxGame game) {
        this.game = game;
        this.audioService = game.getAudioService();
        this.tiledService = new TiledService(game.getAssetService());
        this.physicWorld = new World(Vector2.Zero, true);
        this.physicWorld.setAutoClearForces(false);
        this.engine = new Engine();
        this.tiledAshleySpawner = new TiledAshleySpawner(this.engine, this.physicWorld);
        ImmutableArray<Entity> controllerEntities = this.engine.getEntitiesFor(Family.all(Controller.class).get());
        this.keyboardController = new KeyboardController(GameControllerState.class, controllerEntities);

        // add ECS systems
        this.engine.addSystem(new PhysicMoveSystem());
        this.engine.addSystem(new PhysicSystem(physicWorld, 1 / 60f));
        this.engine.addSystem(new FacingSystem());
        this.engine.addSystem(new FsmSystem());
        this.engine.addSystem(new AnimationSystem(game.getAssetService()));
        this.engine.addSystem(new CameraSystem(game.getCamera()));
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(), game.getCamera()));
        this.engine.addSystem(new PhysicDebugRenderSystem(this.physicWorld, game.getCamera()));
    }

    @Override
    public void show() {
        this.game.getInputMultiplexer().clear();
        this.game.getInputMultiplexer().addProcessor(keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        Consumer<TiledMap> ashleySpawnerConsumer = this.tiledAshleySpawner::loadMapObjects;
        Consumer<TiledMap> cameraConsumer = this.engine.getSystem(CameraSystem.class)::setMap;
        Consumer<TiledMap> audioConsumer = this.audioService::setMap;
        this.tiledService.setMapChangeConsumer(
            renderConsumer.andThen(ashleySpawnerConsumer).andThen(cameraConsumer).andThen(audioConsumer)
        );

        TiledMap startMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(startMap);
    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 30f, delta);
        engine.update(delta);

        // TODO refactor
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.game.setScreen(MenuScreen.class);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (EntitySystem system : this.engine.getSystems()) {
            if (system instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
        this.physicWorld.dispose();
    }
}
