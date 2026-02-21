package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bullet extends Entity {


    public Bullet(float x, float y, TextureRegion texture) {
        super(x, y, texture.getRegionWidth(), texture.getRegionHeight(), texture);
        speed = 200.0f;
        isAlive = true;
        this.hp = 1;
    }

    @Override
    public void update(float delta) {
        y += speed * delta;

        if (y > Gdx.graphics.getHeight()) {
            isAlive = false;
        }
    }
}
