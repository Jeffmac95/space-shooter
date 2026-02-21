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


    public Rock(float x, float y, RockSize size, TextureAtlas atlas) {
        super(x, y, getTexture(size, atlas).getRegionWidth(), getTexture(size, atlas).getRegionHeight(), getTexture(size, atlas));
        this.size = size;
        isAlive = true;

        switch(size) {
            case SMALL:
                speed = 125.0f;
                hp = 1;
                break;
            case MEDIUM:
                speed = 100.0f;
                hp = 1;
                break;
            case LARGE:
                speed = 75.0f;
                hp = 2;
                break;
        }
    }

    private static TextureRegion getTexture(RockSize size, TextureAtlas atlas) {
        switch(size) {
            case SMALL:
                return atlas.findRegion("smRock");
            case MEDIUM:
                return atlas.findRegion("medrock");
            case LARGE:
                return atlas.findRegion("lrgrock");
        }
        return null;
    }

    @Override
    public void update(float delta) {
        y -= speed * delta;

        if (y + height < 0) {
            isAlive = false;
        }
    }
}
