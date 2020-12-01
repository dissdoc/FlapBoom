package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

public class Flappee {

    private static final float COLLISION_RADIUS = 24f;
    private final Circle collisionCircle;

    private float x = 0;
    private float y = 0;

    // Flapping
    private static final float DIVE_ACCEL = 0.30F;
    private static final float FLY_ACCEL = 5F;
    private float ySpeed = 0;

    public Flappee() {
        collisionCircle = new Circle(x, y, COLLISION_RADIUS);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(collisionCircle.x, collisionCircle.y, collisionCircle.radius);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        updateCollisionCircle();
    }

    public void update() {
        ySpeed -= DIVE_ACCEL;
        setPosition(x, y + ySpeed);
    }

    public void flyUp() {
        ySpeed = FLY_ACCEL;
        setPosition(x, y + ySpeed);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void updateCollisionCircle() {
        collisionCircle.setX(x);
        collisionCircle.setY(y);
    }
}
