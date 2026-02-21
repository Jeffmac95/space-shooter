package com.app.box;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

enum RockSize {
    SMALL,
    MEDIUM,
    LARGE
}

public class Rock extends Entity {


    private RockSize size;
    private TextureAtlas atlas;
    private TextureRegion normalTexture;
    private TextureRegion damagedTexture;


    public Rock(float x, float y, RockSize size, TextureAtlas atlas) {
        super(x, y, 0, 0, null);
        this.size = size;
        this.atlas = atlas;
        isAlive = true;

        switch(size) {
            case SMALL:
                normalTexture = atlas.findRegion("smRock");
                texture = normalTexture;
                speed = 125.0f;
                hp = 1;
                break;
            case MEDIUM:
                normalTexture = atlas.findRegion("medrock");
                texture = normalTexture;
                speed = 100.0f;
                hp = 1;
                break;
            case LARGE:
                normalTexture = atlas.findRegion("lrgrock");
                damagedTexture = atlas.findRegion("lrgrock_hurt");
                texture = normalTexture;
                speed = 75.0f;
                hp = 2;
                break;
        }
        width = texture.getRegionWidth();
        height = texture.getRegionHeight();
    }


    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (size == RockSize.LARGE && hp == 1) {
            texture = damagedTexture;
        }
    }

    @Override
    public void update(float delta) {
        y -= speed * delta;

        if (y + height < 0) {
            isAlive = false;
        }

    }
}
