package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Timer;
import io.github.com.quillraven.asset.SoundAsset;
import io.github.com.quillraven.audio.AudioService;
import io.github.com.quillraven.component.Animation2D;
import io.github.com.quillraven.component.Life;
import io.github.com.quillraven.component.Tiled;
import io.github.com.quillraven.component.Trigger;

public class TriggerSystem extends IteratingSystem {
    private final AudioService audioService;

    public TriggerSystem(AudioService audioService) {
        super(Family.all(Trigger.class).get());
        this.audioService = audioService;
    }

    /**
     * Processes triggered entities and fires appropriate trigger effects.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Trigger trigger = Trigger.MAPPER.get(entity);
        if (trigger.getTriggeringEntity() == null) return;

        fireTrigger(trigger.getName(), trigger.getTriggeringEntity());
        trigger.setTriggeringEntity(null);
    }

    private Entity getByTiledId(int tiledId) {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(Tiled.class).get());
        for (Entity entity : entities) {
            if (Tiled.MAPPER.get(entity).getId() == tiledId) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Routes trigger events to appropriate handlers based on trigger name.
     */
    private void fireTrigger(String triggerName, Entity triggeringEntity) {
        switch (triggerName) {
            case "trap_trigger" -> trapTrigger(triggeringEntity);
            default -> throw new GdxRuntimeException("Unsupported trigger: " + triggerName);
        }
    }

    /**
     * Handles trap trigger effects including animation and damage.
     */
    private void trapTrigger(Entity triggeringEntity) {
        Entity trapEntity = getByTiledId(15);
        if (trapEntity != null) {
            // play trap animation
            Animation2D animation2D = Animation2D.MAPPER.get(trapEntity);
            animation2D.setSpeed(1f);
            animation2D.setPlayMode(Animation.PlayMode.NORMAL);
            audioService.playSound(SoundAsset.TRAP);
            // reset animation
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    animation2D.setSpeed(0f);
                    animation2D.setType(Animation2D.AnimationType.IDLE);
                }
            }, 2.5f);

            // damage player
            Life life = Life.MAPPER.get(triggeringEntity);
            if (life.getLife() > 2) {
                life.addLife(-2f);
            }
        }
    }
}
