package com.juego.progra;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.juego.progra.Mapas.HUD;
import com.juego.progra.Mapas.MapaPrincipal;
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

    // MAPA PRINCIPAL (en pruebas): se entra con M y se sale con 1, 2 o 3
    private MapaPrincipal mapaPrincipal;
    private boolean enMapaPrincipal = false;

    public int getNivelActual() {
        return nivelActual;
    }

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        mapaPrincipal = new MapaPrincipal();
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

    //Entra al mapa de seleccion de niveles y pone al jugador en el arco
    private void irAlMapaPrincipal() {
        enMapaPrincipal = true;
        jugador = new Jugador(mapaPrincipal.getSpawnX(), mapaPrincipal.getSpawnY());
    }

    private void cambiarNivel(int nuevoNivel) {
        enMapaPrincipal = false;
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
            // Nivel 1: las dos filas de arriba y abajo se recorren enteras
            // (1025px cada una), por debajo y por encima de los setos.
            enemigos.add(new Enemigo(700, 520, Enemigo.Tipo.HORIZONTAL));
            enemigos.add(new Enemigo(700, 80,  Enemigo.Tipo.HORIZONTAL));
            // El del medio va donde tiene mas hueco alrededor (225x230)
            enemigos.add(new Enemigo(1020, 180, Enemigo.Tipo.ALEATORIO));

        } else if (nivelActual == 2) {
            // Nivel 2: un zorro en cada esquina. El tipo va segun el hueco que
            // tiene cada uno: los de pasillo largo patrullan en horizontal,
            // los que tienen mas altura que ancho se mueven al azar.
            enemigos.add(new Enemigo(300, 580, Enemigo.Tipo.HORIZONTAL, 2)); // arriba izq, 560px de pasillo
            enemigos.add(new Enemigo(880, 510, Enemigo.Tipo.HORIZONTAL, 2)); // arriba der, 260px
            enemigos.add(new Enemigo(180, 150, Enemigo.Tipo.ALEATORIO, 2));  // abajo izq, 250x375
            enemigos.add(new Enemigo(1110, 230, Enemigo.Tipo.ALEATORIO, 2)); // abajo der, 75x375
        } else if (nivelActual == 3) {
            // Nivel 3: dos serpientes al azar.
            enemigos.add(new Enemigo(480, 300, Enemigo.Tipo.ALEATORIO, 3)); // centro, suelta por el nivel
            // Esta queda encerrada en el cuadro de estantes de arriba a la
            // izquierda, para que entrar ahi cueste mas caro.
            enemigos.add(new Enemigo(190, 420, Enemigo.Tipo.ALEATORIO, 3));
            // Esta sube y baja por el pasillo vertical mas largo del nivel (405px)
            enemigos.add(new Enemigo(730, 160, Enemigo.Tipo.VERTICAL, 3));
        }
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
        // M entra al mapa principal, y K enciende una medalla mas (para probar)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            irAlMapaPrincipal();
            Gdx.app.log("PRUEBA", "Mapa principal");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K)) {
            mapaPrincipal.setNivelesGanados(mapaPrincipal.getNivelesGanados() + 1);
            Gdx.app.log("PRUEBA", "Medallas encendidas: " + mapaPrincipal.getNivelesGanados());
        }
        // ---------------------------------

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        if (enMapaPrincipal) {
            //En el mapa principal solo hay movimiento: ni enemigos, ni items, ni vidas
            jugador.moverEn(mapaPrincipal, delta);

            batch.begin();
            mapaPrincipal.dibujarFondo(batch);
            jugador.dibujar(batch);
            mapaPrincipal.dibujarFrente(batch);
            batch.end();
            return;
        }

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
        mapaPrincipal.dispose();
    }
}
