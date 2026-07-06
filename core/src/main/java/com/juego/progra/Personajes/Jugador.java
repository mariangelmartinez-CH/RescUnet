package com.juego.progra.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.juego.progra.Mapas.MapasNiveles;

public class Jugador extends Personaje {
    private int vidas=5;
    private float tiempoSinPerderVidas = 0;

    private Texture abajoQuieto, abajoPie1, abajoPie2;
    private Texture arribaQuieto, arribaPie1, arribaPie2;
    private Texture derechaQuieto, derechaPie1, derechaPie2;
    private Texture izquierdaQuieto, izquierdaPie1, izquierdaPie2;

    private Texture direccionQuieta, direccionPie1, direccionPie2;
    private float tiempoAnimacion = 0;
    private String direccionMirando = "abajo";

    public Jugador(float x, float y) {
        super(x, y, "jugador1DOWN.png");
        abajoQuieto = imagen;
        abajoPie1 = new Texture("jugador1_abajoca.png");
        abajoPie2 = new Texture("jugador1_abajocad.png");

        arribaQuieto = new Texture("jugador1UP.png");
        arribaPie1 = new Texture("jugador1_arriba_ca.png");
        arribaPie2 = new Texture("jugador1_arribacad.png");

        derechaQuieto = new Texture("jugador1.png");
        derechaPie1 = new Texture("jugador1_derechaca.png");
        derechaPie2 = new Texture("jugador1_derechacad.png");

        izquierdaQuieto = new Texture("jugador1IZQ.png");
        izquierdaPie1 = new Texture("jugador1_izqca.png");
        izquierdaPie2 = new Texture("jugador1_izquierdacad.png");

        direccionQuieta = abajoQuieto;
        direccionPie1 = abajoPie1;
        direccionPie2 = abajoPie2;
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tiempoSinPerderVidas > 0) tiempoSinPerderVidas -= delta;

        boolean moviendo = false;
        boolean esHorizontal = false;

        // MOVIMIENTO EN X
        float newX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX += 150 * delta;
            direccionQuieta = derechaQuieto; direccionPie1 = derechaPie1; direccionPie2 = derechaPie2;
            moviendo = true; esHorizontal = true;
            direccionMirando = "derecha";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX -= 150 * delta;
            direccionQuieta = izquierdaQuieto; direccionPie1 = izquierdaPie1; direccionPie2 = izquierdaPie2;
            moviendo = true; esHorizontal = true;
            direccionMirando = "izquierda";
        }
        if (mapa.CaminarCelda(caja(newX, y))) x = newX;

        // MOVIMIENTO EN Y
        float newY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newY += 150 * delta;
            direccionQuieta = arribaQuieto; direccionPie1 = arribaPie1; direccionPie2 = arribaPie2;
            moviendo = true; esHorizontal = false;
            direccionMirando = "arriba";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newY -= 150 * delta;
            direccionQuieta = abajoQuieto; direccionPie1 = abajoPie1; direccionPie2 = abajoPie2;
            moviendo = true; esHorizontal = false;
            direccionMirando = "abajo";
        }
        if (mapa.CaminarCelda(caja(x, newY))) y = newY;

        if (moviendo) {
            tiempoAnimacion += delta;
            if (esHorizontal) {
                int frame = (int) ((tiempoAnimacion % 0.6f) / 0.15f);
                switch (frame) {
                    case 0: imagen = direccionPie1; break;
                    case 2: imagen = direccionPie2; break;
                    default: imagen = direccionQuieta; break;
                }
            } else {
                int frame = (int) ((tiempoAnimacion % 0.4f) / 0.2f);
                imagen = (frame == 0) ? direccionPie1 : direccionPie2;
            }
        } else {
            tiempoAnimacion = 0;
            imagen = direccionQuieta;
        }

        mapa.obtenerObjeto(caja(x, y));

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Rectangle miCaja = caja(x, y);
            float centroX = miCaja.x + miCaja.width / 2f;
            float centroY = miCaja.y + miCaja.height / 2f;
            mapa.dispararLinea(centroX, centroY, direccionMirando);
        }
    }

    public void perderVida(){
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;
        }
    }

    public void recibirGolpe() {
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;
            x = 220; y = 300;
            Gdx.app.log("JUEGO", "Vidas restantes: " + vidas);
        }
    }

    public int getVidas(){
        return vidas;
    }
    public boolean estaVivo(){
        return vidas > 0;
    }

    @Override
    public void dispose() {
        abajoQuieto.dispose(); abajoPie1.dispose(); abajoPie2.dispose();
        arribaQuieto.dispose(); arribaPie1.dispose(); arribaPie2.dispose();
        derechaQuieto.dispose(); derechaPie1.dispose(); derechaPie2.dispose();
        izquierdaQuieto.dispose(); izquierdaPie1.dispose(); izquierdaPie2.dispose();
    }
}
