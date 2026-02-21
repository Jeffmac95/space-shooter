package com.app.box;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private enum GameState {
        START_SCREEN,
        PLAYING,
        GAME_OVER
    }
    private GameState state = GameState.START_SCREEN;
    private BitmapFont titleFont;
    private BitmapFont instructionFont;

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
    private UI ui;
    private int score = 0;

    @Override
    public void create() {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("atlas/textures.atlas"));

        background = atlas.findRegion("starrybg");

        titleFont = new BitmapFont(Gdx.files.internal("font/font.fnt"));
        instructionFont = new BitmapFont(Gdx.files.internal("font/font.fnt"));
        titleFont.getData().setScale(1.5f);
        titleFont.setColor(1.0f, 0.9f, 0.4f, 1.0f);
        instructionFont.getData().setScale(1.0f);
        instructionFont.setColor(Color.WHITE);

        TextureRegion playerRegion = atlas.findRegion("rocket");
        TextureRegion bulletRegion = atlas.findRegion("bullet");
        player = new Player(288.0f, 0.0f, playerRegion, atlas, bulletRegion);

        ui = new UI(atlas, player);

        bullets = new Array<>();
        rocks = new Array<>();

        rockSpawnTimer = 0;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        batch.begin();

        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (state == GameState.START_SCREEN) {
            drawStartScreen();
            startGameInput();
        }
        else if (state == GameState.PLAYING) {
            updateGame(delta);
            player.render(batch);
            for (Bullet bullet : bullets) {
                bullet.render(batch);
            }
            for (Rock rock : rocks) {
                rock.render(batch);
            }
            ui.render(batch);
        }
        else if (state == GameState.GAME_OVER) {
            drawGameOver();
            handleGameOverInput();
        }

        batch.end();
    }

    private void drawStartScreen() {
        String title = "MILKY WAY DEFENDER";
        String instructions = "Left Click on the screen to PLAY";

        float titleX = (SCREEN_WIDTH / 2.0f) - 225;
        float titleY = SCREEN_HEIGHT - 100;
        float instructionX = (SCREEN_WIDTH / 2.0f) - 200;
        float instructionY = SCREEN_HEIGHT - 300;

        titleFont.draw(batch, title, titleX, titleY);
        instructionFont.draw(batch, instructions, instructionX, instructionY);
    }

    private void startGameInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            startGame();
        }
    }

    private void startGame() {
        state = GameState.PLAYING;

        score = 0;
        player.hp = 10;
        player.shotsFired = 0;
        player.shotsHit = 0;
        bullets.clear();
        rocks.clear();
        rockSpawnTimer = 0;
        player.x = 288.0f;
        player.y = 0;
    }

    private void updateGame(float delta) {
        rockSpawnTimer += delta;

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
            player.shotsFired++;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (!bullet.isAlive) {
                bullets.removeIndex(i);
            }
        }

        // COLLISION bullet-rock
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = rocks.size - 1; j >= 0; j--) {
                Rock rock = rocks.get(j);
                if (bullet.collision(rock)) {
                    bullet.isAlive = false;
                    rock.takeDamage(1);
                    player.shotsHit++;
                    if (!rock.isAlive) {
                        score += 10;
                    }
                    break;
                }
            }
        }

        // COLLISION player-rock
        for (Rock rock : rocks) {
            if (rock.isAlive && player.collision(rock)) {
                player.takeDamage(1);
                rock.takeDamage(1);
                rock.isAlive = false;
            }
        }

        ui.update(score);

        if (player.hp <= 0) {
            state = GameState.GAME_OVER;
        }
    }

    public void drawGameOver() {
        String gameOverText = "GAME OVER";
        String finalScoreText = "Final Score: " + score;
        String retryText = "Right Click to PLAY again!";

        float overX = SCREEN_WIDTH / 2 - 125;
        float overY = SCREEN_HEIGHT / 2 + 100;
        float scoreX = SCREEN_WIDTH / 2 - 100;
        float scoreY = (SCREEN_HEIGHT / 2) - 75;
        float retryX = SCREEN_WIDTH / 2 - 175;
        float retryY = (SCREEN_HEIGHT / 2) - 200;

        titleFont.draw(batch, gameOverText, overX, overY);
        instructionFont.draw(batch, finalScoreText, scoreX, scoreY);
        instructionFont.draw(batch, retryText, retryX, retryY);
    }

    private void handleGameOverInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            startGame();
        }
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
        ui.dispose();
        titleFont.dispose();
        instructionFont.dispose();
    }
}
