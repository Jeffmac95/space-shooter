package com.app.box;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public abstract class Entity {

    protected float x;
    protected float y;
    protected float speed;
    protected int width;
    protected int height;
    protected TextureRegion texture;
    boolean isAlive;

    public Entity(float x, float y, int width, int height, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.isAlive = true;
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
}
