package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity {

    private int hp = 10;
    private TextureRegion bulletTexture;
    private Bullet bulletToSpawn;

    public Player(float x, float y, TextureRegion texture, TextureRegion bulletTexture) {
        super(x, y, texture.getRegionWidth(), texture.getRegionHeight(), texture);
        speed = 300.0f;
        isAlive = true;
        this.bulletTexture = bulletTexture;
    }

    @Override
    public void update(float delta) {
        // Input
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float bulletX = x + width /2.0f - bulletTexture.getRegionWidth() / 2.0f;
            float bulletY = y + height;
            bulletToSpawn = new Bullet(bulletX, bulletY, bulletTexture);
        }


        // Screen bounds
        if (x > Gdx.graphics.getWidth()) {
            x = 0.0f;
        }
        if (x < 0) {
            x = Gdx.graphics.getWidth();
        }
    }

    public Bullet shootBullet() {
        Bullet temp = bulletToSpawn;
        bulletToSpawn = null;
        return temp;
    }
}
