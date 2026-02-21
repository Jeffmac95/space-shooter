package com.app.box;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureRegion background;
    private Player player;
    private Array<Bullet> bullets;
    private Array<Rock> rocks;
    private float rockSpawnTimer;
    private float rockSpawnInterval = 1.5f;
    private static int MAX_ROCKS = 4;
    private static float SCREEN_WIDTH;
    private static float SCREEN_HEIGHT;


    @Override
    public void create() {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("atlas/textures.atlas"));

        background = atlas.findRegion("starrybg");
        TextureRegion playerRegion = atlas.findRegion("rocket");

        TextureRegion bulletRegion = atlas.findRegion("bullet");
        player = new Player(288.0f, 0.0f, playerRegion,  atlas, bulletRegion);

        bullets = new Array<>();
        rocks = new Array<>();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        rockSpawnTimer += delta;

        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        player.update(delta);

        if (rockSpawnTimer >= rockSpawnInterval && rocks.size < MAX_ROCKS) {
            spawnRock();
            rockSpawnTimer = 0;
        }

        for (int i = rocks.size - 1; i >= 0; i--) {
            Rock rock = rocks.get(i);
            rock.update(delta);
            if (!rock.isAlive) {
                rocks.removeIndex(i);
            }
        }

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

        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.render(batch);

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
        for (Rock rock : rocks) {
            rock.render(batch);
        }

        batch.end();
    }

    private void spawnRock() {
        RockSize[] sizes = RockSize.values();
        RockSize randomSize = sizes[(int)(MathUtils.random() * sizes.length)];

        Rock temp = new Rock(0, 0, randomSize, atlas);

        float maxX = SCREEN_WIDTH - temp.width;
        float randomX = MathUtils.random() * maxX;
        float spawnY = SCREEN_HEIGHT;

        rocks.add(new Rock(randomX, spawnY, randomSize, atlas));
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }
}
