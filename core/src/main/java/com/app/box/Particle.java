package com.app.box;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Particle extends Entity {


    private float life;
    private float maxLife;
    private float vx;
    private float vy;
    private float scale;
    private Color rainbow = new Color();


    public Particle(float x, float y, TextureRegion texture) {
        super(x, y, texture.getRegionWidth(), texture.getRegionHeight(), texture);
        maxLife = life = MathUtils.random(0.4f, 0.9f);
        vx = MathUtils.random(-100.0f, 100.0f);
        vy = MathUtils.random(-50.0f, 150.0f);
        scale = MathUtils.random(0.3f, 0.8f);
        width *= scale;
        height *= scale;
    }

    @Override
    public void update(float delta) {
        x += vx * delta;
        y += vy * delta;
        vy -= 200.0f * delta;
        life -= delta;
        isAlive = life > 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        float hue = (1.0f - life / maxLife) * 360.0f;
        rainbow.fromHsv(hue + MathUtils.random(-15.0f, 15.0f), 1.0f, 1.0f);

        batch.setColor(rainbow.r, rainbow.g, rainbow.b, life / maxLife);
        batch.draw(texture, x-2, y-2, width+4, height+4);
        batch.setColor(Color.WHITE);
    }
}
