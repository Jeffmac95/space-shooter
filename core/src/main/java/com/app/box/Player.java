package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {

    private int hp = 10;
    private TextureRegion bulletTexture;
    private Bullet bulletToSpawn;
    private Animation<TextureRegion> moveRightAnimation;
    private Animation<TextureRegion> moveLeftAnimation;
    private float stateTime;
    private TextureRegion currentFrame;

    public Player(float x, float y, TextureRegion idleTexture, TextureAtlas atlas, TextureRegion bulletTexture) {
        super(x, y, idleTexture.getRegionWidth(), idleTexture.getRegionHeight(), idleTexture);
        speed = 300.0f;
        isAlive = true;
        this.bulletTexture = bulletTexture;

        // Animation
        Array<TextureRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("rocket_mr1"));
        rightFrames.add(atlas.findRegion("rocket_mr2"));
        moveRightAnimation = new Animation<>(0.2f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> leftFrames = new Array<>();
        leftFrames.add(atlas.findRegion("rocket_ml1"));
        leftFrames.add(atlas.findRegion("rocket_ml2"));
        moveLeftAnimation = new Animation<>(0.2f, leftFrames, Animation.PlayMode.LOOP);

        currentFrame = idleTexture;
    }

    @Override
    public void update(float delta) {
        boolean moving = false;

        // Update animation
        stateTime += delta;

        // Input
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
            currentFrame = moveLeftAnimation.getKeyFrame(stateTime);
            moving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
            currentFrame = moveRightAnimation.getKeyFrame(stateTime);
            moving = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float bulletX = x + width /2.0f - bulletTexture.getRegionWidth() / 2.0f;
            float bulletY = y + height;
            bulletToSpawn = new Bullet(bulletX, bulletY, bulletTexture);
        }

        if (!moving) {
            stateTime = 0;
            currentFrame = texture;
        }


        // Screen bounds
        if (x > Gdx.graphics.getWidth()) {
            x = 0.0f;
        }
        if (x < 0) {
            x = Gdx.graphics.getWidth();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, x, y, width, height);
    }

    public Bullet shootBullet() {
        Bullet temp = bulletToSpawn;
        bulletToSpawn = null;
        return temp;
    }
}
