package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class ScreenManager implements Disposable {


    public enum GameState {
        START_SCREEN,
        PLAYING,
        GAME_OVER
    }

    private GameState state = GameState.START_SCREEN;
    private BitmapFont titleFont;
    private BitmapFont instructionFont;
    private final float screenWidth;
    private final float screenHeight;


    public interface ScreenListener {
        void onStartGame();
        void onRestartGame();
    }

    private final ScreenListener listener;

    public ScreenManager(ScreenListener listener) {
        this.listener = listener;
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        titleFont = new BitmapFont(Gdx.files.internal("font/font.fnt"));
        instructionFont = new BitmapFont(Gdx.files.internal("font/font.fnt"));
        titleFont.getData().setScale(1.5f);
        titleFont.setColor(1.0f, 0.9f, 0.4f, 1.0f);
        instructionFont.getData().setScale(1.0f);
        instructionFont.setColor(Color.WHITE);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }


    public void update(SpriteBatch batch) {
        switch (state) {
            case START_SCREEN:
                drawStartScreen(batch);
                handleStartInput();
                break;
            case GAME_OVER:
                drawGameOver(batch, 0);
                break;
            default:
                break;
        }
    }


    public void update(SpriteBatch batch, int score) {
        switch (state) {
            case START_SCREEN:
                drawStartScreen(batch);
                handleStartInput();
                break;
            case GAME_OVER:
                drawGameOver(batch, score);
                handleGameOverInput();
                break;
            default:
                break;
        }
    }

    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }

    private void drawStartScreen(SpriteBatch batch) {
        String title = "MILKY WAY DEFENDER";
        String instructions = "Left Click on the screen to PLAY";

        float titleX = (screenWidth / 2.0f) - 225;
        float titleY = screenHeight - 100;
        float instructionX = (screenWidth / 2.0f) - 200;
        float instructionY = screenHeight - 300;

        titleFont.draw(batch, title, titleX, titleY);
        instructionFont.draw(batch, instructions, instructionX, instructionY);
    }

    private void drawGameOver(SpriteBatch batch, int score) {
        String gameOverText = "GAME OVER";
        String finalScoreText = "Final Score: " + score;
        String retryText = "Right Click to PLAY again!";

        float overX = screenWidth / 2 - 125;
        float overY = screenHeight / 2 + 100;
        float scoreX = screenWidth / 2 - 110;
        float scoreY = (screenHeight / 2) - 75;
        float retryX = screenWidth / 2 - 175;
        float retryY = (screenHeight / 2) - 200;

        titleFont.draw(batch, gameOverText, overX, overY);
        instructionFont.draw(batch, finalScoreText, scoreX, scoreY);
        instructionFont.draw(batch, retryText, retryX, retryY);
    }

    private void handleStartInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            state = GameState.PLAYING;
            listener.onStartGame();
        }
    }

    private void handleGameOverInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            state = GameState.PLAYING;
            listener.onRestartGame();
        }
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        instructionFont.dispose();
    }
}
