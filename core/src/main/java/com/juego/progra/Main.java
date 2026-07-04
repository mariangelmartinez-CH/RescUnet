package com.juego.progra;
import com.juego.progra.Mapas.MapasNiveles;
import com.juego.progra.Personajes.Enemigo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Main extends Game {
    private SpriteBatch batch;
    private MapasNiveles mapa;
    private Jugador jugador;
    private List<Enemigo> enemigos;

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        jugador = new Jugador(220, 300);

        enemigos = new ArrayList<>();
        enemigos.add(new Enemigo(700, 520, Enemigo.Tipo.HORIZONTAL));
        enemigos.add(new Enemigo(700, 230, Enemigo.Tipo.ALEATORIO));

        Enemigo enemigoAbajo = new Enemigo(700, 100, Enemigo.Tipo.HORIZONTAL);
        enemigoAbajo.setCajaTamañito(15);
        enemigos.add(enemigoAbajo); // FILA VILLANO INTENTANDO PASAR POR ABAJO HORIZONTAL
    }

    public void render(){
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        jugador.actualizar(mapa, delta);
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(mapa, delta);
            if (Math.abs(jugador.getX() - enemigo.getX()) < 50 &&
                Math.abs(jugador.getY() - enemigo.getY()) < 50) {
                jugador.recibirGolpe();
            }
        }

        batch.begin();
        mapa.dibujar(batch,jugador);
        for (Enemigo enemigo :enemigos) {
            enemigo.dibujar(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        jugador.dispose();
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
        mapa.dipose();
    }
}
