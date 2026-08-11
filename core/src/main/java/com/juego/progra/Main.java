package com.juego.progra;
import com.badlogic.gdx.graphics.Texture;
import com.juego.progra.Mapas.HUD;
import com.juego.progra.Mapas.MapasNiveles;
import com.juego.progra.Personajes.Enemigo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Jugador;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Game {
    private SpriteBatch batch;
    private MapasNiveles mapa;
    private HUD HUD;
    private Texture corazonLleno;
    private Texture corazonVacio;
    private Jugador jugador;
    private List<Enemigo> enemigos;
    private int nivelActual = 1;

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        HUD = new HUD();
        mapa.setHud(HUD);
        jugador = new Jugador(220, 300);

        // HERRAMIENTA DE DEBUG: al hacer clic imprime la coordenada del mundo
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int yCorregida = Gdx.graphics.getHeight() - screenY;
                System.out.println("clic, " + screenX + ", " + yCorregida);
                return true;
            }
        });

        cambiarNivel(1);
    }

    private void cambiarNivel(int nuevoNivel) {
        nivelActual = nuevoNivel;

        if (enemigos != null) {
            for (Enemigo e : enemigos) {
                e.dispose();
            }
        }

        mapa.cargarNivel(nivelActual);
        HUD.actualizarHud(nivelActual);
        jugador = new Jugador(220, 300);

        enemigos = new ArrayList<>();
        if (nivelActual == 1) {
            // Enemigos del Nivel 1
            enemigos.add(new Enemigo(700, 520, Enemigo.Tipo.HORIZONTAL));
            enemigos.add(new Enemigo(700, 230, Enemigo.Tipo.ALEATORIO));

            Enemigo enemigoAbajo = new Enemigo(700, 100, Enemigo.Tipo.HORIZONTAL);
            enemigoAbajo.setCajaTamañito(15);
            enemigos.add(enemigoAbajo);

        } /*else if (nivelActual == 2) {
            enemigos.add(new Enemigo(600, 400, Enemigo.Tipo.ALEATORIO));
            enemigos.add(new Enemigo(400, 200, Enemigo.Tipo.HORIZONTAL));

        }*/
    }

    @Override
    public void render(){
        // --- TECLA DE PRUEBA TEMPORAL ---
        // Presiona '2' para cambiar al nivel 2 limpiamente con sus nuevos enemigos
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            cambiarNivel(2);
            Gdx.app.log("PRUEBA", "Cambiado al Nivel 2 con nuevos enemigos");
        }
        // Opcional: Presiona '1' para regresar al nivel 1 si quieres probar volver
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            cambiarNivel(1);
            Gdx.app.log("PRUEBA", "Regresado al Nivel 1");
        }
        // ---------------------------------

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
        mapa.dibujar(batch, jugador, enemigos);
        HUD.dibujarCorazones(batch, jugador.getVidas());
        batch.end();
        HUD.render();
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
