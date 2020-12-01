package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;

    private static final float GAP_BETWEEN_FLOWERS = 200f;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;

    // Objects
    private Flappee flappee = new Flappee();

    // Flowers
    private Array<Flower> flowers = new Array<>();

    // Score
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private int score = 0;

    private Texture background;
    private Texture flowerBottom;
    private Texture flowerTop;

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);

        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();

        background = new Texture(Gdx.files.internal("bg.jpg"));
        flowerBottom = new Texture(Gdx.files.internal("pipeBottom.png"));
        flowerTop =  new Texture(Gdx.files.internal("pipeTop.png"));
    }

    @Override
    public void render(float delta) {
        clearScreen();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(background, 0,  0);

        for (Flower flower: flowers)
            flower.draw(batch);

        drawScore();
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        flappee.drawDebug(shapeRenderer);
        update(delta);
        drawDebug();
        shapeRenderer.end();
    }

    private void update(float delta) {
        flappee.update();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
            flappee.flyUp();
        blockFlappeeLeavingTheWorld();

        updateFlowers(delta);
        updateScore();
        if (checkForCollision())
            restart();
    }

    private void restart() {
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        flowers.clear();
        score = 0;
    }

    private void blockFlappeeLeavingTheWorld() {
        flappee.setPosition(flappee.getX(), MathUtils.clamp(flappee.getY(), 0, WORLD_HEIGHT));
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void createNewFlower() {
        Flower flower = new Flower(flowerBottom, flowerTop);
        flower.setPosition(WORLD_WIDTH + Flower.WIDTH);
        flowers.add(flower);
    }

    private void checkIfNewFlowerIsNeeded() {
        if (flowers.size == 0) {
            createNewFlower();
        } else {
            Flower flower = flowers.peek();
            if (flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS) {
                createNewFlower();
            }
        }
    }

    private void removeFlowersIfPassed() {
        if (flowers.size > 0) {
            Flower firstFlower = flowers.first();
            if (firstFlower.getX() < -Flower.WIDTH) {
                flowers.removeValue(firstFlower,true);
            }
        }
    }

    private void updateFlowers(float delta) {
        for (Flower flower: flowers) {
            flower.update(delta);
        }

        checkIfNewFlowerIsNeeded();
        removeFlowersIfPassed();
    }

    private void drawDebug() {
        for (Flower flower: flowers) {
            flower.drawDebug(shapeRenderer);
        }
    }

    private boolean checkForCollision() {
        for (Flower flower: flowers) {
            if (flower.isFlappeeColliding(flappee)) {
                return true;
            }
        }
        return false;
    }

    private void updateScore() {
        Flower flower = flowers.first();
        if (flower.getX() < flappee.getX() && !flower.isPointClaimed()) {
            flower.markPointClaimed();
            score++;
        }
    }

    private void drawScore() {
        String scoreAsString = Integer.toString(score);
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString, (viewport.getWorldWidth() / 2),
                (4 * viewport.getWorldHeight() / 5 - glyphLayout.height / 2));

        bitmapFont.getData().setScale(5,5);
    }
}
