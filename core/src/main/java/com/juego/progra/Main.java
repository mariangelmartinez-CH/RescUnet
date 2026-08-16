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


    private MapaPrincipal mapaPrincipal;
    private boolean enMapaPrincipal = false;


    private Transicion transicion;


    private Menu menu;
    private boolean enMenu = true;
    private SeleccionPersonaje seleccionPersonaje;
    private boolean enSeleccion = false;
    private int personajeElegido = 2;
    private static final float TAMANO_EN_EL_MAPA = 125;

    private static final float CONTACTO_X = 35;
    private static final float CONTACTO_Y = 45;

    public int getNivelActual() {
        return nivelActual;
    }

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        mapaPrincipal = new MapaPrincipal();
        transicion = new Transicion();
        menu = new Menu();
        seleccionPersonaje = new SeleccionPersonaje();
        HUD = new HUD();
        mapa.setHud(HUD);


        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(HUD.getStage());
        Gdx.input.setInputProcessor(multiplexer);

        cambiarNivel(1);
    }


    private void nuevoJugador(float x, float y) {
        if (jugador != null) {
            jugador.dispose();
        }
        jugador = new Jugador(x, y, personajeElegido);
    }


    private void irAlMapaPrincipal() {
        enMapaPrincipal = true;
        nuevoJugador(mapaPrincipal.getSpawnX(), mapaPrincipal.getSpawnY());
    }


    private void volverAlMapaGanando() {

        if (mapaPrincipal.getNivelesGanados() < nivelActual) {
            mapaPrincipal.setNivelesGanados(nivelActual);
        }
        enMapaPrincipal = true;
        nuevoJugador(mapaPrincipal.getXPiedra(nivelActual),
                     mapaPrincipal.getYPiedra(nivelActual));
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


        nuevoJugador(mapa.getSpawnX(), mapa.getSpawnY());

        enemigos = new ArrayList<>();
        if (nivelActual == 1) {

            enemigos.add(new Enemigo(700, 520, Enemigo.Tipo.HORIZONTAL));
            enemigos.add(new Enemigo(700, 80,  Enemigo.Tipo.HORIZONTAL));
            enemigos.add(new Enemigo(1020, 180, Enemigo.Tipo.ALEATORIO));

        } else if (nivelActual == 2) {
            enemigos.add(new Enemigo(345, 590, Enemigo.Tipo.HORIZONTAL, 2));  // arriba izq, 594px de pasillo
            enemigos.add(new Enemigo(1000, 470, Enemigo.Tipo.HORIZONTAL, 2)); // arriba der, 241px por encima de los arbustos
            enemigos.add(new Enemigo(300, 175, Enemigo.Tipo.ALEATORIO, 2));   // abajo izq, 231x137
            enemigos.add(new Enemigo(1150, 250, Enemigo.Tipo.ALEATORIO, 2));  // abajo der, 56x389 (el pasillo pegado a la reja)
        } else if (nivelActual == 3) {

            enemigos.add(new Enemigo(480, 300, Enemigo.Tipo.ALEATORIO, 3)); // centro, suelta por el nivel

            enemigos.add(new Enemigo(190, 420, Enemigo.Tipo.ALEATORIO, 3));
            enemigos.add(new Enemigo(730, 160, Enemigo.Tipo.VERTICAL, 3));
        }
    }

    @Override
    public void render(){

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
        // C da el nivel por terminado: cambia la reja de arriba a la abierta
        // y deja subir por el camino, sin tener que recoger los 27 objetos
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.C)) {
            mapa.completarDePrueba();
            Gdx.app.log("PRUEBA", "Nivel dado por completado: reja de arriba abierta");
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K)) {
            mapaPrincipal.setNivelesGanados(mapaPrincipal.getNivelesGanados() + 1);
            Gdx.app.log("PRUEBA", "Medallas encendidas: " + mapaPrincipal.getNivelesGanados());
        }


        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(),
                                Gdx.graphics.getBackBufferHeight());

        float delta = Gdx.graphics.getDeltaTime();
        boolean enTransicion = transicion.estaActiva();
        if (enTransicion && transicion.actualizar(delta)) {
            int destino = transicion.getNivelDestino();
            if (destino > 0) {
                cambiarNivel(destino);
            } else if (destino == 0) {
                volverAlMapaGanando();
            } else if (destino == -1) {
                enMenu = false;
                enSeleccion = true;
            } else if (destino == -2) {
                enSeleccion = false;
                irAlMapaPrincipal();
            }
        }


        if (enMapaPrincipal) {
            mapaPrincipal.actualizarAnimales(delta);
        }

        if (!enTransicion) {
            if (enMenu) {
                Menu.Opcion elegida = menu.actualizar();
                if (elegida == Menu.Opcion.JUGAR) {
                    transicion.empezarPantalla(-1);
                } else if (elegida == Menu.Opcion.SALIR) {
                    Gdx.app.exit();
                }

            } else if (enSeleccion) {
                SeleccionPersonaje.Opcion elegido = seleccionPersonaje.actualizar();
                if (elegido != SeleccionPersonaje.Opcion.NINGUNA) {
                    personajeElegido = (elegido == SeleccionPersonaje.Opcion.PERSONAJE1) ? 1 : 2;
                    Gdx.app.log("SELECCION", "Personaje elegido: " + elegido);
                    transicion.empezarPantalla(-2);
                }
            } else if (enMapaPrincipal) {

                jugador.moverEn(mapaPrincipal, delta);

                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    int nivel = mapaPrincipal.nivelDisponible(
                            jugador.caja(jugador.getX(), jugador.getY()));
                    if (nivel > 0) {
                        transicion.empezar(nivel);
                        Gdx.app.log("MAPA", "Entrando al nivel " + nivel);
                    }
                }
            } else {
                jugador.actualizar(mapa, delta);
                for (Enemigo enemigo : enemigos) {
                    enemigo.actualizar(mapa, delta);
                    if (Math.abs(jugador.getX() - enemigo.getX()) < CONTACTO_X &&
                        Math.abs(jugador.getY() - enemigo.getY()) < CONTACTO_Y) {
                        jugador.recibirGolpe();
                    }
                }

                if (mapa.saliendoPorArriba(jugador.caja(jugador.getX(), jugador.getY()))) {
                    transicion.empezarVuelta();
                    Gdx.app.log("JUEGO", "Nivel " + nivelActual + " ganado");
                }
            }
        }

        if (transicion.pixelando()) {
            transicion.empezarCaptura();
            dibujarEscena();
            transicion.terminarCapturaYDibujar(batch);
        } else {
            dibujarEscena();
        }
    }

    private void dibujarEscena() {
        if (enMenu) {
            batch.begin();
            menu.dibujar(batch);
            batch.end();
        } else if (enSeleccion) {
            batch.begin();
            seleccionPersonaje.dibujar(batch);
            batch.end();
        } else if (enMapaPrincipal) {
            batch.begin();
            mapaPrincipal.dibujarFondo(batch);
            mapaPrincipal.dibujarAnimales(batch);

            if (transicion.mostrandoPose()) {
                transicion.dibujarPuno(batch, jugador, TAMANO_EN_EL_MAPA, personajeElegido);
            } else {
                jugador.dibujar(batch, TAMANO_EN_EL_MAPA);
            }
            mapaPrincipal.dibujarFrente(batch);
            batch.end();
        } else {
            batch.begin();
            mapa.dibujar(batch, jugador, enemigos, nivelActual);
            HUD.dibujarCorazones(batch, jugador.getVidas());
            batch.end();
            HUD.render();
        }
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // Si HUD es accesible desde Main, actualiza su viewport:
        if (HUD != null && HUD.getStage() != null) {
            HUD.getStage().getViewport().update(width, height, true);
        }
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
        transicion.dispose();
        menu.dispose();
        seleccionPersonaje.dispose();
    }
}
