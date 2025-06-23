package io.github.com.quillraven.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.com.quillraven.GdxGame;
import io.github.com.quillraven.component.CameraFollow;
import io.github.com.quillraven.component.Transform;

public class CameraSystem extends IteratingSystem {
    private static final float CAM_OFFSET_Y = 1f; // make the camera look up 'X' additional tiles

    private final Camera camera;
    private final float smoothingFactor;
    private final Vector2 targetPosition;
    private float mapW;
    private float mapH;

    public CameraSystem(Camera camera) {
        super(Family.all(CameraFollow.class, Transform.class).get());
        this.camera = camera;
        this.smoothingFactor = 4f; // lower value = slower camera follow
        this.targetPosition = new Vector2();
    }

    /**
     * Updates camera position with smooth following and boundary constraints.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        calcTargetPosition(transform.getPosition());

        // Apply smoothing using linear interpolation (LERP)
        float progress = smoothingFactor * deltaTime;
        float smoothedX = MathUtils.lerp(camera.position.x, this.targetPosition.x, progress);
        float smoothedY = MathUtils.lerp(camera.position.y, this.targetPosition.y, progress);
        camera.position.set(smoothedX, smoothedY, camera.position.z);
    }

    /**
     * Calculates target camera position within map boundaries.
     */
    private void calcTargetPosition(Vector2 entityPosition) {
        float targetX = entityPosition.x;
        float targetY = entityPosition.y + CAM_OFFSET_Y;
        float camHalfW = camera.viewportWidth * 0.5f;
        if (mapW > camHalfW) {
            float min = Math.min(camHalfW, mapW - camHalfW);
            float max = Math.max(camHalfW, mapW - camHalfW);
            targetX = MathUtils.clamp(targetX, min, max);
        }
        float camHalfH = camera.viewportHeight * 0.5f;
        if (mapH > camHalfH) {
            float min = Math.min(camHalfH, mapH - camHalfH);
            float max = Math.max(camHalfH, mapH - camHalfH);
            targetY = MathUtils.clamp(targetY, min, max);
        }
        this.targetPosition.set(targetX, targetY);
    }

    /**
     * Sets up camera for a new map and positions it at the target entity.
     */
    public void setMap(TiledMap tiledMap) {
        int width = tiledMap.getProperties().get("width", 0, Integer.class);
        int tileW = tiledMap.getProperties().get("tilewidth", 0, Integer.class);
        int height = tiledMap.getProperties().get("height", 0, Integer.class);
        int tileH = tiledMap.getProperties().get("tileheight", 0, Integer.class);
        mapW = width * tileW * GdxGame.UNIT_SCALE;
        mapH = height * tileH * GdxGame.UNIT_SCALE;

        Entity camEntity = getEntities().first();
        if (camEntity == null) {
            return;
        }

        Transform transform = Transform.MAPPER.get(camEntity);
        calcTargetPosition(transform.getPosition());
        camera.position.set(this.targetPosition.x, this.targetPosition.y, camera.position.z);
    }
}
