package com.juego.progra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

    public class Menu implements Screen {
    private Game game;
    private SpriteBatch batch;
    private BitmapFont fuente;
    private Texture fondo;

    public Menu(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fuente = new BitmapFont();
        fuente.getData().setScale(3);
        fondo = new Texture("Fondo.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, 1408, 768);
        fuente.draw(batch, "GARDEN RESCUE", 500, 500);
        fuente.draw(batch, "Presiona ENTER para jugar", 450, 300);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("MENU", "Se presiono jugar - falta conectar con JuegoScreen");
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        fuente.dispose();
        fondo.dispose();
    }
}
