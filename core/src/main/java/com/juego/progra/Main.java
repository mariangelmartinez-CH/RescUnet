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

    //La animacion de entrar a un nivel: puño arriba y hojas que se cierran
    private Transicion transicion;

    //En el mapa principal el personaje se ve mas grande que dentro de un
    //nivel. Vale tanto para el sprite normal como para el del puño.
    private static final float TAMANO_EN_EL_MAPA = 125;

    //CUANTO SE TIENEN QUE ACERCAR jugador y enemigo para que haga daño.
    //Los dos sprites van centrados igual dentro de su cuadro de 105, asi que
    //restar sus x (o sus y) da directamente la distancia entre sus centros.
    //Medido sobre los dibujos: el cuerpo del jugador ocupa 41 de ancho y 84 de
    //alto, y el del zorro unos 50 de ancho mirando arriba o abajo. O sea que
    //rozarse es estar a unos 45 en X. Se deja por debajo de eso a proposito,
    //para que haya que solaparse de verdad y no valga con pasar cerca.
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
        HUD = new HUD();
        mapa.setHud(HUD);
        //El jugador lo crea cambiarNivel(), en el punto de aparicion del nivel

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

    //Cambia al jugador de sitio. Se suelta el anterior porque cada Jugador
    //carga sus 12 texturas en el constructor, y si no se irian acumulando en
    //cada cambio de nivel.
    private void nuevoJugador(float x, float y) {
        if (jugador != null) {
            jugador.dispose();
        }
        jugador = new Jugador(x, y);
    }

    //Entra al mapa de seleccion de niveles y pone al jugador en el arco
    private void irAlMapaPrincipal() {
        enMapaPrincipal = true;
        nuevoJugador(mapaPrincipal.getSpawnX(), mapaPrincipal.getSpawnY());
    }

    //Se acaba de ganar el nivel: se enciende su medalla y el jugador aparece
    //de pie sobre esa misma piedra, listo para seguir hacia la siguiente.
    private void volverAlMapaGanando() {
        //Con esta comparacion no se pierde progreso al repetir un nivel ya ganado
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

        //El punto de aparicion lo decide el mapa: en los niveles 1 y 2 es el
        //camino de abajo, donde la reja esta abierta. El Jugador se guarda ese
        //punto en su constructor, asi que tambien es donde reaparece cada vez
        //que pierde una vida.
        nuevoJugador(mapa.getSpawnX(), mapa.getSpawnY());

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
            // OJO: estas posiciones se recalcularon sobre el nivel2.txt nuevo.
            // Tres de las anteriores caian DENTRO de la caja de un arbusto, y
            // ahi el zorro no se puede mover para ningun lado: CaminarCelda le
            // dice que no a las cuatro direcciones, asi que se queda pegado
            // rebotando en el sitio. Al lado va el hueco libre que tiene cada
            // uno, medido con la misma cuenta que usa CaminarCelda.
            enemigos.add(new Enemigo(345, 590, Enemigo.Tipo.HORIZONTAL, 2));  // arriba izq, 594px de pasillo
            enemigos.add(new Enemigo(1000, 470, Enemigo.Tipo.HORIZONTAL, 2)); // arriba der, 241px por encima de los arbustos
            enemigos.add(new Enemigo(300, 175, Enemigo.Tipo.ALEATORIO, 2));   // abajo izq, 231x137
            enemigos.add(new Enemigo(1150, 250, Enemigo.Tipo.ALEATORIO, 2));  // abajo der, 56x389 (el pasillo pegado a la reja)
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
        // ---------------------------------

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();

        //La transicion manda sobre todo lo demas: mientras corre, ni el
        //jugador ni los enemigos se mueven. Se adelanta una sola vez por
        //frame, antes de decidir que se dibuja.
        boolean enTransicion = transicion.estaActiva();
        if (enTransicion && transicion.actualizar(delta)) {
            //Justo aqui la pantalla esta tapada por las hojas, asi que es
            //cuando se cambia de sitio y no se ve el corte
            if (transicion.getNivelDestino() > 0) {
                cambiarNivel(transicion.getNivelDestino());
            } else {
                volverAlMapaGanando();
            }
        }

        // --- LO QUE SE MUEVE ---
        //Los animalitos del jardin siguen a lo suyo aunque el jugador este en
        //medio de una transicion: son ambiente, y verlos congelados se notaria.
        if (enMapaPrincipal) {
            mapaPrincipal.actualizarAnimales(delta);
        }

        if (!enTransicion) {
            if (enMapaPrincipal) {
                //En el mapa solo hay movimiento: ni enemigos, ni items, ni vidas
                jugador.moverEn(mapaPrincipal, delta);

                //ESPACIO parado encima de una piedra ya desbloqueada: entra
                //a ese nivel. Si no esta sobre ninguna, no pasa nada.
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

                //Subio del todo por el camino de arriba (que solo esta abierto
                //si ya recogio los 27 objetos): nivel ganado, se vuelve al mapa.
                if (mapa.saliendoPorArriba(jugador.caja(jugador.getX(), jugador.getY()))) {
                    transicion.empezarVuelta();
                    Gdx.app.log("JUEGO", "Nivel " + nivelActual + " ganado");
                }
            }
        }

        // --- LO QUE SE DIBUJA ---
        //Mientras dura el efecto, la escena no se pinta en la pantalla sino en
        //una textura aparte, para poder pixelarla entera antes de enseñarla.
        if (transicion.pixelando()) {
            transicion.empezarCaptura();
            dibujarEscena();
            transicion.terminarCapturaYDibujar(batch);
        } else {
            dibujarEscena();
        }
    }

    //Todo el juego tal cual, sin efectos. Va aparte porque hay que poder
    //pedirlo dos veces: una para la pantalla y otra para la textura del efecto.
    private void dibujarEscena() {
        if (enMapaPrincipal) {
            batch.begin();
            mapaPrincipal.dibujarFondo(batch);
            //Los animales van entre el fondo y el jugador: asi el arco y los
            //arboles de dibujarFrente los tapan cuando pasan por detras
            mapaPrincipal.dibujarAnimales(batch);
            //Durante la pose se cambia el sprite normal por el del puño arriba
            if (transicion.mostrandoPose()) {
                transicion.dibujarPuno(batch, jugador, TAMANO_EN_EL_MAPA);
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
    public void dispose() {
        batch.dispose();
        jugador.dispose();
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
        mapa.dipose();
        mapaPrincipal.dispose();
        transicion.dispose();
    }
}
