package com.app.box;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureRegion background;
    private Player player;
    private Array<Bullet> bullets;


    @Override
    public void create() {
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("atlas/textures.atlas"));

        background = atlas.findRegion("starrybg");
        TextureRegion playerRegion = atlas.findRegion("rocket");

        TextureRegion bulletRegion = atlas.findRegion("bullet");
        player = new Player(288.0f, 0.0f, playerRegion,  atlas, bulletRegion);

        bullets = new Array<>();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        player.update(delta);

        Bullet newBullet = player.shootBullet();
        if (newBullet != null) {
            bullets.add(newBullet);
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (!bullet.isAlive) {
                bullets.removeIndex(i);
            }
        }



/*
        // DEBUG ARRAY
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            System.out.println("=== Bullets (" + bullets.size + ") ===");
            if (bullets.isEmpty()) {
                System.out.println("  (no bullets)");
            } else {
                for (int i = 0; i < bullets.size; i++) {
                    Bullet b = bullets.get(i);
                    System.out.printf("  [%d] x=%.1f  y=%.1f  alive=%s%n", i, b.x, b.y, b.isAlive);
                }
            }
            System.out.println("=".repeat(50));
        }
*/


        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player.render(batch);

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }
}
