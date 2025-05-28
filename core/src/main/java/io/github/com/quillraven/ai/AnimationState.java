package io.github.com.quillraven.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.com.quillraven.component.Animation2D;
import io.github.com.quillraven.component.Animation2D.AnimationType;
import io.github.com.quillraven.component.Fsm;
import io.github.com.quillraven.component.Move;

public enum AnimationState implements State<Entity> {
    IDLE {
        @Override
        public void enter(Entity entity) {
            Animation2D.MAPPER.get(entity).setType(AnimationType.IDLE);
        }

        @Override
        public void update(Entity entity) {
            Move move = Move.MAPPER.get(entity);
            if (move != null && !move.getDirection().isZero()) {
                Fsm.MAPPER.get(entity).getAnimationFsm().changeState(WALK);
            }
        }

        @Override
        public void exit(Entity entity) {
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    WALK {
        @Override
        public void enter(Entity entity) {
            Animation2D.MAPPER.get(entity).setType(AnimationType.WALK);
        }

        @Override
        public void update(Entity entity) {
            Move move = Move.MAPPER.get(entity);
            if (move.getDirection().isZero()) {
                Fsm.MAPPER.get(entity).getAnimationFsm().changeState(IDLE);
            }
        }

        @Override
        public void exit(Entity entity) {
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    }
}
