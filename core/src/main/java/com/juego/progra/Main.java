package com.juego.progra;
import com.badlogic.gdx.Input;
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

    public int getNivelActual() {
        return nivelActual;
    }

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

        //Cada nivel tiene su punto de aparicion: el de los niveles 1 y 2 cae
        //dentro de un estante en el nivel 3, por eso no puede ser el mismo.
        if (nivelActual == 3) {
            jugador = new Jugador(480, 200);
        } else {
            jugador = new Jugador(220, 300);
        }

        enemigos = new ArrayList<>();
        if (nivelActual == 1) {
            // Enemigos del Nivel 1
            enemigos.add(new Enemigo(700, 520, Enemigo.Tipo.HORIZONTAL));
            enemigos.add(new Enemigo(700, 230, Enemigo.Tipo.ALEATORIO));

            Enemigo enemigoAbajo = new Enemigo(700, 100, Enemigo.Tipo.HORIZONTAL);
            enemigoAbajo.setCajaTamañito(15);
            enemigos.add(enemigoAbajo);

        } else if (nivelActual == 2) {
            // Nivel 2: cuatro zorros, dos por mitad. Posiciones verificadas
            // contra los arbustos de nivel2.txt para que ninguno nazca encerrado.
            enemigos.add(new Enemigo(400, 600, Enemigo.Tipo.HORIZONTAL, 2));
            enemigos.add(new Enemigo(500, 90,  Enemigo.Tipo.HORIZONTAL, 2));
            enemigos.add(new Enemigo(700, 580, Enemigo.Tipo.HORIZONTAL, 2));
            enemigos.add(new Enemigo(850, 140, Enemigo.Tipo.ALEATORIO, 2));
        }
        // El nivel 3 todavia no tiene enemigos: falta decidir cual es su villano
    }

    @Override
    public void render(){
        // --- TECLAS DE PRUEBA TEMPORALES: 1, 2 y 3 cambian de nivel ---
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            cambiarNivel(1);
            Gdx.app.log("PRUEBA", "Nivel 1");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            cambiarNivel(2);
            Gdx.app.log("PRUEBA", "Nivel 2");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
            cambiarNivel(3);
            Gdx.app.log("PRUEBA", "Nivel 3");
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
        mapa.dibujar(batch, jugador, enemigos,nivelActual);
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
