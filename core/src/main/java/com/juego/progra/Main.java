package com.juego.progra;
import com.juego.progra.Personajes.Enemigo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Mapas.MapasNiveles;
import com.juego.progra.Personajes.Jugador;

public class Main extends Game {
    private SpriteBatch batch;
    private MapasNiveles mapa;
    private Jugador jugador;
    private Enemigo[] enemigos;

    @Override
    public void create() {
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        jugador = new Jugador(384, 458);
        enemigos = new Enemigo[] {
            new Enemigo(300, 510, Enemigo.HORIZONTAL),
            new Enemigo(900, 185, Enemigo.HORIZONTAL),
            new Enemigo(700, 386, Enemigo.ALEATORIO)
        };
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();

        jugador.actualizar(mapa, delta);

        for (Enemigo e : enemigos) {
            e.actualizar(mapa, delta);
            if (Math.abs(jugador.getX() - e.getX()) < 50 &&
                Math.abs(jugador.getY() - e.getY()) < 50) {
                jugador.recibirGolpe();
            }
        }

        batch.begin();
        mapa.dibujar(batch);
        jugador.dibujar(batch);
        for (Enemigo e : enemigos) {
            e.dibujar(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        jugador.dispose();
        for (Enemigo e : enemigos) {
            e.dispose();
        }
    }
}
