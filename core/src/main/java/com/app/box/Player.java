package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.Text;

public class Player extends Entity {


    private TextureRegion bulletTexture;
    private Bullet bulletToSpawn;
    private Animation<TextureRegion> moveRightAnimation;
    private Animation<TextureRegion> moveLeftAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private TextureRegion hurtTexture;
    private float hurtTimer = 0.0f;
    private float hurtDuration = 0.3f;
    int shotsFired = 0;
    int shotsHit = 0;


    public Player(float x, float y, TextureRegion idleTexture, TextureAtlas atlas, TextureRegion bulletTexture) {
        super(x, y, idleTexture.getRegionWidth(), idleTexture.getRegionHeight(), idleTexture);
        speed = 300.0f;
        isAlive = true;
        this.hp = 10;
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

        hurtTexture = atlas.findRegion("rocket_hurt");
    }

    @Override
    public void update(float delta) {
        boolean moving = false;

        if (hurtTimer > 0) {
            hurtTimer -= delta;
        }

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
        if (x > Gdx.graphics.getWidth() - width) {
            x = Gdx.graphics.getWidth() - width;
        }
        if (x < 0) {
            x = 0;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (hurtTimer > 0) {
            batch.draw(hurtTexture, x, y, width, height);
        } else {
            batch.draw(currentFrame, x, y, width, height);
        }
    }

    public Bullet shootBullet() {
        Bullet temp = bulletToSpawn;
        bulletToSpawn = null;
        return temp;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        hurtTimer = hurtDuration;
    }

    public float getAccuracy() {
        if (shotsFired == 0) {
            return 0.0f;
        }
        return (shotsHit / (float)shotsFired) * 100.0f;
    }
}
