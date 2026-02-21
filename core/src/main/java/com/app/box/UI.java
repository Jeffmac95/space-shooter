package com.app.box;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class UI implements Disposable {


    private BitmapFont font;
    private BitmapFont smallFont;
    private Player player;
    private int score = 0;


    public UI(TextureAtlas atlas, Player player) {
        this.player = player;

        font = new BitmapFont(Gdx.files.internal("font/font.fnt"));
        smallFont = new BitmapFont(Gdx.files.internal("font/font.fnt"));

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        smallFont.getData().setScale(0.5f);
        smallFont.setColor(Color.WHITE);
    }

    public void update(int score) {
        this.score = score;
    }

    public void render(SpriteBatch batch) {
        float accuracy = player.getAccuracy();

        String scoreText = "SCORE: " + score;
        float scoreX = 20;
        float scoreY = Gdx.graphics.getHeight() - 20;
        font.draw(batch, scoreText, scoreX, scoreY);

        String hpText = "HP: " + player.hp + "/10";
        float hpX = Gdx.graphics.getWidth() - 150;
        float hpY = Gdx.graphics.getHeight() - 20;
        font.draw(batch, hpText, hpX, hpY);

        String accuracyText = String.format("Accuracy: %.2f%%", accuracy);
        float accuracyX = (Gdx.graphics.getWidth() / 2.0f) - 60;
        float accuracyY = Gdx.graphics.getHeight() - 10.0f;
        smallFont.draw(batch, accuracyText, accuracyX, accuracyY);
    }

    @Override
    public void dispose() {
        font.dispose();
        smallFont.dispose();
    }
}
