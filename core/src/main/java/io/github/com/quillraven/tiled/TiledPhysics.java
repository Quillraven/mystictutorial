package io.github.com.quillraven.tiled;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.com.quillraven.GdxGame;

import java.util.ArrayList;
import java.util.List;

public final class TiledPhysics {

    public static List<FixtureDef> toFixtureDefs(TiledMapTile tiledMapTile) {
        List<FixtureDef> result = new ArrayList<>();
        for (MapObject mapObject : tiledMapTile.getObjects()) {
            result.add(toFixtureDef(mapObject));
        }
        return result;
    }

    // relativeTo is necessary for map objects that are directly placed on a layer because
    // their x/y is equal to the position of the object, but we need it relative to 0,0 like it
    // is in the collision editor of a tile.
    public static FixtureDef toFixtureDef(MapObject mapObject, Vector2 relativeTo) {
        if (mapObject instanceof RectangleMapObject rectMapObj) {
            return rectangleFixtureDef(rectMapObj, relativeTo);
        } else if (mapObject instanceof EllipseMapObject ellipseMapObj) {
            return ellipseFixtureDef(ellipseMapObj, relativeTo);
        } else if (mapObject instanceof PolygonMapObject polygonMapObj) {
            return polygonFixtureDef(polygonMapObj, polygonMapObj.getPolygon().getVertices(), relativeTo);
        } else if (mapObject instanceof PolylineMapObject polylineMapObj) {
            return polygonFixtureDef(polylineMapObj, polylineMapObj.getPolyline().getVertices(), relativeTo);
        } else {
            throw new GdxRuntimeException("Unsupported MapObject: " + mapObject);
        }
    }

    public static FixtureDef toFixtureDef(MapObject mapObject) {
        return toFixtureDef(mapObject, Vector2.Zero);
    }

    // Box is centered around body position in Box2D, but we want to have it aligned in a way
    // that the body position is the bottom left corner of the box.
    // That's why we use a 'boxOffset' below.
    private static FixtureDef rectangleFixtureDef(RectangleMapObject mapObject, Vector2 relativeTo) {
        Rectangle rectangle = mapObject.getRectangle();
        float rectX = rectangle.x;
        float rectY = rectangle.y;
        float rectW = rectangle.width;
        float rectH = rectangle.height;

        float boxX = rectX * GdxGame.UNIT_SCALE - relativeTo.x;
        float boxY = rectY * GdxGame.UNIT_SCALE - relativeTo.y;
        float boxW = rectW * GdxGame.UNIT_SCALE * 0.5f;
        float boxH = rectH * GdxGame.UNIT_SCALE * 0.5f;

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxW, boxH, new Vector2(boxX + boxW, boxY + boxH), 0f);
        initFixtureDef(mapObject, fixtureDef, shape);

        return fixtureDef;
    }

    private static FixtureDef ellipseFixtureDef(EllipseMapObject mapObject, Vector2 relativeTo) {
        Ellipse ellipse = mapObject.getEllipse();
        float x = ellipse.x;
        float y = ellipse.y;
        float w = ellipse.width;
        float h = ellipse.height;

        float ellipseX = x * GdxGame.UNIT_SCALE - relativeTo.x;
        float ellipseY = y * GdxGame.UNIT_SCALE - relativeTo.y;
        float ellipseW = w * GdxGame.UNIT_SCALE / 2f;
        float ellipseH = h * GdxGame.UNIT_SCALE / 2f;

        FixtureDef fixtureDef = new FixtureDef();
        if (MathUtils.isEqual(ellipseW, ellipseH, 0.1f)) {
            // width and height are equal -> return a circle shape
            CircleShape shape = new CircleShape();
            shape.setPosition(new Vector2(ellipseX + ellipseW, ellipseY + ellipseH));
            shape.setRadius(ellipseW);
            initFixtureDef(mapObject, fixtureDef, shape);
        } else {
            // width and height are not equal -> return an ellipse shape (=polygon with 'numVertices' vertices)
            // PolygonShape only supports 8 vertices
            // ChainShape supports more but does not properly collide in some scenarios
            final int numVertices = 8;
            float angleStep = MathUtils.PI2 / numVertices;
            Vector2[] vertices = new Vector2[numVertices];

            for (int vertexIdx = 0; vertexIdx < numVertices; vertexIdx++) {
                float angle = vertexIdx * angleStep;
                float offsetX = ellipseW * MathUtils.cos(angle);
                float offsetY = ellipseH * MathUtils.sin(angle);
                vertices[vertexIdx] = new Vector2(ellipseX + ellipseW + offsetX, ellipseY + ellipseH + offsetY);
            }

            PolygonShape shape = new PolygonShape();
            shape.set(vertices);
            initFixtureDef(mapObject, fixtureDef, shape);
        }

        return fixtureDef;
    }

    private static FixtureDef polygonFixtureDef(
        MapObject mapObject, // Could be PolygonMapObject or PolylineMapObject
        float[] polyVertices,
        Vector2 relativeTo
    ) {
        final float offsetX;
        final float offsetY;
        if (mapObject instanceof PolygonMapObject polygonMapObject) {
            offsetX = polygonMapObject.getPolygon().getX() * GdxGame.UNIT_SCALE - relativeTo.x;
            offsetY = polygonMapObject.getPolygon().getY() * GdxGame.UNIT_SCALE - relativeTo.y;
        } else {
            PolylineMapObject polylineMapObject = (PolylineMapObject) mapObject;
            offsetX = polylineMapObject.getPolyline().getX() * GdxGame.UNIT_SCALE - relativeTo.x;
            offsetY = polylineMapObject.getPolyline().getY() * GdxGame.UNIT_SCALE - relativeTo.y;
        }
        float[] vertices = new float[polyVertices.length];
        for (int vertexIdx = 0; vertexIdx < polyVertices.length; vertexIdx += 2) {
            // x-coordinate
            vertices[vertexIdx] = offsetX + polyVertices[vertexIdx] * GdxGame.UNIT_SCALE;
            // y-coordinate
            vertices[vertexIdx + 1] = offsetY + polyVertices[vertexIdx + 1] * GdxGame.UNIT_SCALE;
        }

        FixtureDef fixtureDef = new FixtureDef();
        ChainShape shape = new ChainShape();

        if (mapObject instanceof PolygonMapObject) {
            shape.createLoop(vertices);
        } else { // PolylineMapObject
            shape.createChain(vertices);
        }
        initFixtureDef(mapObject, fixtureDef, shape);

        return fixtureDef;
    }

    private static void initFixtureDef(MapObject mapObject, FixtureDef fixtureDef, Shape shape) {
        fixtureDef.shape = shape;
        fixtureDef.friction = mapObject.getProperties().get("friction", 0f, Float.class);
        fixtureDef.restitution = mapObject.getProperties().get("restitution", 0f, Float.class);
        fixtureDef.density = mapObject.getProperties().get("density", 0f, Float.class);
        fixtureDef.isSensor = mapObject.getProperties().get("sensor", false, Boolean.class);
    }
}
