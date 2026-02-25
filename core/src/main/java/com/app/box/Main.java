package com.app.box;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter implements ScreenManager.ScreenListener {


    private ScreenManager screenManager;

    private SpriteBatch batch;
    private TextureAtlas atlas;
    private TextureRegion background;
    private Player player;
    private Array<Bullet> bullets;
    private Array<Rock> rocks;
    private Array<Particle> particles;

    private float rockSpawnTimer;
    private float rockSpawnInterval = 1.5f;
    private int maxRocks = 4;
    private static float SCREEN_WIDTH;
    private static float SCREEN_HEIGHT;

    private float survivalTime = 0f;

    private UI ui;
    private int score = 0;

    private Music introMusic;
    private Music gameMusic;
    private Music outroMusic;
    private Sound laserSound;
    private Sound collisionSound;


    @Override
    public void create() {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("atlas/textures.atlas"));
        background = atlas.findRegion("starrybg");

        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser1.wav"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));

        TextureRegion playerRegion = atlas.findRegion("rocket");
        TextureRegion bulletRegion = atlas.findRegion("bullet");
        player = new Player(288.0f, 0.0f, playerRegion, atlas, bulletRegion, laserSound);

        ui = new UI(atlas, player);
        screenManager = new ScreenManager(this);

        bullets = new Array<>();
        rocks = new Array<>();
        particles = new Array<>();

        introMusic = Gdx.audio.newMusic(Gdx.files.internal("music/throughspace.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music/DST-TowerDefenseTheme.mp3"));
        outroMusic = Gdx.audio.newMusic(Gdx.files.internal("music/ruskerdax_-_soul_release.mp3"));
        introMusic.setLooping(true);
        introMusic.setVolume(0.6f);
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.8f);
        outroMusic.setLooping(true);
        outroMusic.setVolume(0.6f);

        playIntroMusic();
        rockSpawnTimer = 0;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);

        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (screenManager.isPlaying()) {
            updateGame(delta);
            player.render(batch);
            for (Bullet bullet : bullets) { bullet.render(batch); }
            for (Rock rock : rocks) { rock.render(batch); }
            for (Particle particle : particles) { particle.render(batch); }
            ui.render(batch);
        } else {
            screenManager.update(batch, score);
        }

        batch.end();
    }

    @Override
    public void onStartGame() {
        resetGame();
        playGameMusic();
    }

    @Override
    public void onRestartGame() {
        resetGame();
        playGameMusic();
    }

    private void resetGame() {
        score = 0;
        player.hp = 10;
        player.shotsFired = 0;
        player.shotsHit = 0;
        player.x = 288.0f;
        player.y = 0;
        bullets.clear();
        rocks.clear();
        particles.clear();
        rockSpawnTimer = 0;
        survivalTime = 0;
    }

    private void updateGame(float delta) {
        survivalTime += delta;
        updateDifficulty();

        rockSpawnTimer += delta;
        player.update(delta);

        if (rockSpawnTimer >= rockSpawnInterval && rocks.size < maxRocks) {
            spawnRock();
            rockSpawnTimer = 0;
        }

        for (int i = rocks.size - 1; i >= 0; i--) {
            Rock rock = rocks.get(i);
            rock.update(delta);
            if (!rock.isAlive) { rocks.removeIndex(i); }
        }

        Bullet newBullet = player.shootBullet();
        if (newBullet != null) {
            bullets.add(newBullet);
            player.shotsFired++;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (!bullet.isAlive) { bullets.removeIndex(i); }
        }

        for (int i = particles.size - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(delta);
            if (!particle.isAlive) { particles.removeIndex(i); }
        }

        // bullet-rock collision
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = rocks.size - 1; j >= 0; j--) {
                Rock rock = rocks.get(j);
                if (bullet.collision(rock)) {
                    bullet.isAlive = false;
                    rock.takeDamage(1);
                    player.shotsHit++;
                    collisionSound.play(0.4f);
                    spawnParticles(bullet.x + bullet.width / 2.0f, bullet.y + bullet.height / 2.0f, 8);
                    if (!rock.isAlive) { score += 10; }
                    break;
                }
            }
        }

        // player-rock collision
        for (Rock rock : rocks) {
            if (rock.isAlive && player.collision(rock)) {
                player.takeDamage(1);
                rock.takeDamage(1);
                rock.isAlive = false;
                collisionSound.play(0.4f);
            }
        }

        ui.update(score);

        if (player.hp <= 0) {
            screenManager.setState(ScreenManager.GameState.GAME_OVER);
            playOutroMusic();
        }
    }

    private void spawnRock() {
        RockSize[] sizes = RockSize.values();
        RockSize randomSize = sizes[(int)(MathUtils.random() * sizes.length)];
        Rock temp = new Rock(0, 0, randomSize, atlas);
        float maxX = SCREEN_WIDTH - temp.width;
        float randomX = MathUtils.random() * maxX;
        rocks.add(new Rock(randomX, SCREEN_HEIGHT, randomSize, atlas));
    }

    private void spawnParticles(float x, float y, int count) {
        TextureRegion spark = atlas.findRegion("particle");
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(x, y, spark));
        }
    }

    private void playIntroMusic() {
        gameMusic.stop();
        outroMusic.stop();
        introMusic.play();
    }

    private void playGameMusic() {
        introMusic.stop();
        outroMusic.stop();
        gameMusic.play();
    }

    private void playOutroMusic() {
        gameMusic.stop();
        introMusic.stop();
        outroMusic.play();
    }

    private void updateDifficulty() {
        rockSpawnInterval = Math.max(0.5f, 1.5f - survivalTime * 0.05f);
        maxRocks = Math.min(14, 4 + (int)(survivalTime / 10.0f));
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        ui.dispose();
        screenManager.dispose();
        introMusic.dispose();
        gameMusic.dispose();
        outroMusic.dispose();
        laserSound.dispose();
        collisionSound.dispose();
    }
}
