package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Flower {

    private static final float COLLISION_RECTANGLE_WIDTH = 13f;
    private static final float COLLISION_RECTANGLE_HEIGHT = 447f;
    private static final float COLLISION_CIRCLE_RADIUS = 16f;
    private static final float MAX_SPEED_PER_SECONDS = 100f;
    private static final float HEIGHT_OFFSET = -400f;

    private static final float DISTANCE_BETWEEN_FLOOR_AND_CEILING = 225f;
    private final Circle floorCollisionCircle;
    private final Rectangle floorCollisionRectangle;
    private final Circle ceilingCollisionCircle;
    private final Rectangle ceilingCollisionRectangle;

    public static final float WIDTH = COLLISION_CIRCLE_RADIUS * 2;

    private boolean pointClaimed = false;

    private final Texture floorTexture;
    private final Texture ceilingTexture;

    private float x = 0;
    private float y = 0;

    public Flower(Texture floorTexture, Texture ceilingTexture) {
        this.floorTexture = floorTexture;
        this.ceilingTexture = ceilingTexture;

        y = MathUtils.random(HEIGHT_OFFSET);

        floorCollisionRectangle = new Rectangle(x, y,
                COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        floorCollisionCircle = new Circle(x + floorCollisionRectangle.width / 2,
                y + floorCollisionRectangle.height, COLLISION_CIRCLE_RADIUS);

        ceilingCollisionRectangle = new Rectangle(x,
                floorCollisionCircle.y + DISTANCE_BETWEEN_FLOOR_AND_CEILING,
                COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        ceilingCollisionCircle = new Circle(x + ceilingCollisionRectangle.width / 2,
                ceilingCollisionRectangle.y, COLLISION_CIRCLE_RADIUS);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x) {
        this.x = x;

        updateCollisionCircle();
        updateCollisionRectangle();
    }

    private void updateCollisionCircle() {
        floorCollisionCircle.setX(x + floorCollisionRectangle.width / 2);
        ceilingCollisionCircle.setX(x + ceilingCollisionRectangle.width / 2);
    }

    private void updateCollisionRectangle() {
        floorCollisionRectangle.setX(x);
        ceilingCollisionRectangle.setX(x);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(floorCollisionCircle.x, floorCollisionCircle.y,
                floorCollisionCircle.radius);
        shapeRenderer.rect(floorCollisionRectangle.x, floorCollisionRectangle.y,
                floorCollisionRectangle.width, floorCollisionRectangle.height);

        shapeRenderer.circle(ceilingCollisionCircle.x, ceilingCollisionCircle.y,
                ceilingCollisionCircle.radius);
        shapeRenderer.rect(ceilingCollisionRectangle.x, ceilingCollisionRectangle.y,
                ceilingCollisionRectangle.width, ceilingCollisionRectangle.height);
    }

    public void update(float delta) {
        setPosition(x - MAX_SPEED_PER_SECONDS * delta);
    }

    public boolean isFlappeeColliding(Flappee flappee) {
        Circle flappeeCollisionCircle = flappee.getCollisionCircle();

        return Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionCircle) ||
                Intersector.overlaps(flappeeCollisionCircle, floorCollisionCircle) ||
                Intersector.overlaps(flappeeCollisionCircle, ceilingCollisionRectangle) ||
                Intersector.overlaps(flappeeCollisionCircle, floorCollisionRectangle);
    }

    public boolean isPointClaimed() {
        return pointClaimed;
    }

    public void markPointClaimed() {
        pointClaimed = true;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(floorTexture, floorCollisionCircle.x - floorTexture.getWidth() / 2,
                floorCollisionRectangle.getY() + COLLISION_CIRCLE_RADIUS);

        batch.draw(ceilingTexture, ceilingCollisionCircle.x - ceilingTexture.getWidth() / 2,
                ceilingCollisionRectangle.getY() - COLLISION_CIRCLE_RADIUS);
    }
}
