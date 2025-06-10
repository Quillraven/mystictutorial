package io.github.com.quillraven.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.asset.MapAsset;
import io.github.com.quillraven.asset.SkinAsset;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.input.GameControllerState;
import io.github.com.quillraven.input.KeyboardController;
import io.github.com.quillraven.system.AnimationSystem;
import io.github.com.quillraven.system.AttackSystem;
import io.github.com.quillraven.system.CameraSystem;
import io.github.com.quillraven.system.ControllerSystem;
import io.github.com.quillraven.system.DamagedSystem;
import io.github.com.quillraven.system.FacingSystem;
import io.github.com.quillraven.system.FsmSystem;
import io.github.com.quillraven.system.LifeSystem;
import io.github.com.quillraven.system.PhysicDebugRenderSystem;
import io.github.com.quillraven.system.PhysicMoveSystem;
import io.github.com.quillraven.system.PhysicSystem;
import io.github.com.quillraven.system.RenderSystem;
import io.github.com.quillraven.system.TriggerSystem;
import io.github.com.quillraven.tiled.TiledAshleyConfigurator;
import io.github.com.quillraven.tiled.TiledService;
import io.github.com.quillraven.ui.model.GameViewModel;
import io.github.com.quillraven.ui.view.GameView;

import java.util.function.Consumer;

public class GameScreen extends ScreenAdapter {
    private final GdxGame game;
    private final Stage stage;
    private final Skin skin;
    private final GameViewModel viewModel;
    private final Viewport uiViewport;
    private final TiledService tiledService;
    private final Engine engine;
    private final TiledAshleyConfigurator tiledAshleyConfigurator;
    private final World physicWorld;
    private final KeyboardController keyboardController;
    private final AudioService audioService;

    public GameScreen(GdxGame game) {
        this.game = game;
        this.uiViewport = new FitViewport(320f, 180f);
        this.stage = new Stage(uiViewport, game.getBatch());
        this.skin = game.getAssetService().get(SkinAsset.DEFAULT);
        this.viewModel = new GameViewModel(game);
        this.audioService = game.getAudioService();
        this.physicWorld = new World(Vector2.Zero, true);
        this.physicWorld.setAutoClearForces(false);
        this.tiledService = new TiledService(game.getAssetService(), this.physicWorld);
        this.engine = new Engine();
        this.tiledAshleyConfigurator = new TiledAshleyConfigurator(this.engine, this.physicWorld, this.game.getAssetService());
        this.keyboardController = new KeyboardController(GameControllerState.class, engine, null);

        // add ECS systems
        this.engine.addSystem(new PhysicMoveSystem());
        this.engine.addSystem(new PhysicSystem(physicWorld, 1 / 60f));
        this.engine.addSystem(new FacingSystem());
        this.engine.addSystem(new AttackSystem(physicWorld, audioService));
        this.engine.addSystem(new FsmSystem());
        // DamagedSystem must run after FsmSystem to correctly
        // detect when a damaged animation should be played.
        // This is done by checking if an entity has a Damaged component,
        // and this component is removed in the DamagedSystem.
        this.engine.addSystem(new DamagedSystem(viewModel));
        this.engine.addSystem(new TriggerSystem(audioService));
        this.engine.addSystem(new LifeSystem(this.viewModel));
        this.engine.addSystem(new AnimationSystem(game.getAssetService()));
        this.engine.addSystem(new CameraSystem(game.getCamera()));
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(), game.getCamera()));
        this.engine.addSystem(new PhysicDebugRenderSystem(this.physicWorld, game.getCamera()));
        this.engine.addSystem(new ControllerSystem(game));
    }

    @Override
    public void show() {
        this.game.setInputProcessors(stage, keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

        this.stage.addActor(new GameView(stage, skin, this.viewModel));

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        Consumer<TiledMap> cameraConsumer = this.engine.getSystem(CameraSystem.class)::setMap;
        Consumer<TiledMap> audioConsumer = this.audioService::setMap;
        this.tiledService.setMapChangeConsumer(renderConsumer.andThen(cameraConsumer).andThen(audioConsumer));
        this.tiledService.setLoadTriggerConsumer(tiledAshleyConfigurator::onLoadTrigger);
        this.tiledService.setLoadObjectConsumer(tiledAshleyConfigurator::onLoadObject);
        this.tiledService.setLoadTileConsumer(tiledAshleyConfigurator::onLoadTile);

        TiledMap startMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(startMap);
    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 30f, delta);
        engine.update(delta);

        uiViewport.apply();
        stage.getBatch().setColor(Color.WHITE);
        stage.act(delta);
        stage.draw();
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
