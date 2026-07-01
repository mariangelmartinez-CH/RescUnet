package com.juego.progra;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.juego.progra.Mapas.MapasNiveles;
import com.juego.progra.Personajes.Jugador;

public class Main extends Game {
    private SpriteBatch batch;
    private MapasNiveles mapa;
    private Jugador jugador;

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        jugador = new Jugador(100, 100);
    }

    public void render(){
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        jugador.actualizar(mapa, delta);

        batch.begin();

        mapa.dibujar(batch);
        //jugador.dibujar(batch);

        batch.end();
    }
    @Override
    public void dispose() {
        batch.dispose();
        jugador.dispose();
    }
}
