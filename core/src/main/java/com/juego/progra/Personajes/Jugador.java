package com.juego.progra.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.juego.progra.Mapas.Caminable;
import com.juego.progra.Mapas.MapasNiveles;

public class Jugador extends Personaje {
    private static final float INVENCIBILIDAD = 1.2f;

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
        this.cajaTamañito = 4; //mas chica que la de los enemigos, para pasar por los huecos angostos
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
        moverEn(mapa, delta);

        mapa.obtenerObjeto(caja(x, y));

        //ESPACIO: rompe el arbusto que este justo en la direccion que mira.
        //Se manda el CENTRO de la caja, no la esquina del sprite.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Rectangle miCaja = caja(x, y);
            mapa.dispararLinea(miCaja.x + miCaja.width / 2f,
                               miCaja.y + miCaja.height / 2f,
                               direccionMirando);
        }
    }

    //Solo el movimiento y la animacion. Sirve igual en un nivel que en el
    //mapa principal, porque los dos saben responder CaminarCelda.
    public void moverEn(Caminable mapa, float delta) {
        if (tiempoSinPerderVidas > 0) tiempoSinPerderVidas -= delta;

        boolean moviendo = false;
        boolean esHorizontal = false;

        // MOVIMIENTO EN X
        float newX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += 125 * delta;
            direccionQuieta = derechaQuieto; direccionPie1 = derechaPie1; direccionPie2 = derechaPie2;
            moviendo = true; esHorizontal = true;
            direccionMirando = "derecha";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= 125 * delta;
            direccionQuieta = izquierdaQuieto; direccionPie1 = izquierdaPie1; direccionPie2 = izquierdaPie2;
            moviendo = true; esHorizontal = true;
            direccionMirando = "izquierda";
        }
        if (mapa.CaminarCelda(caja(newX, y))) x = newX;

        // MOVIMIENTO EN Y
        float newY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += 125 * delta;
            direccionQuieta = arribaQuieto; direccionPie1 = arribaPie1; direccionPie2 = arribaPie2;
            moviendo = true; esHorizontal = false;
            direccionMirando = "arriba";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= 125 * delta;
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
    }

    //Al recibir el golpe alterna rojo - normal - rojo y despues vuelve a la
    //normalidad. Se pinta el mismo sprite tintado, no hace falta otra imagen.
    @Override
    public void dibujar(SpriteBatch batch) {
        boolean rojo = false;
        if (tiempoSinPerderVidas > 0) {
            int tramo = (int) (tiempoSinPerderVidas / (INVENCIBILIDAD / 5f));
            rojo = (tramo % 2 == 0); //alterna rojo y normal en 5 tramos
        }

        if (rojo) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
            super.dibujar(batch);
            batch.setColor(Color.WHITE); //se devuelve el color o se tiñe todo lo demas
        } else {
            super.dibujar(batch);
        }
    }

    public void perderVida(){
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = INVENCIBILIDAD;
        }
    }

    public void recibirGolpe() {
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = INVENCIBILIDAD;
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
