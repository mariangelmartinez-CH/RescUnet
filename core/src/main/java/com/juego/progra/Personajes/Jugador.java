package com.juego.progra.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.juego.progra.Mapas.Caminable;
import com.juego.progra.Mapas.MapasNiveles;

import java.util.ArrayList;

public class Jugador extends Personaje {
    private static final float INVENCIBILIDAD = 1.2f;

    private int vidas=5;
    private float tiempoSinPerderVidas = 0;


    private float spawnX, spawnY;


    private TextureRegion abajoQuieto, abajoPie1, abajoPie2;
    private TextureRegion arribaQuieto, arribaPie1, arribaPie2;
    private TextureRegion derechaQuieto, derechaPie1, derechaPie2;
    private TextureRegion izquierdaQuieto, izquierdaPie1, izquierdaPie2;

    private TextureRegion direccionQuieta, direccionPie1, direccionPie2;
    private TextureRegion regionActual;
    private float tiempoAnimacion = 0;
    private String direccionMirando = "abajo";


    private ArrayList<Texture> texturasPropias = new ArrayList<>();


    public Jugador(float x, float y) {
        this(x, y, 2);
    }


    public Jugador(float x, float y, int personaje) {
        super(x, y, personaje == 1 ? "jugador2.png" : "jugador1DOWN.png");
        this.cajaTamañito = 4; //mas chica que la de los enemigos, para pasar por los huecos angostos
        this.spawnX = x;
        this.spawnY = y;
        texturasPropias.add(imagen);

        if (personaje == 1) {
            cargarHojaNiña();
        } else {
            cargarImagenesNiño();
        }

        direccionQuieta = abajoQuieto;
        direccionPie1 = abajoPie1;
        direccionPie2 = abajoPie2;
        regionActual = direccionQuieta;
    }

    private void cargarHojaNiña() {
        TextureRegion[][] celdas = TextureRegion.split(imagen,
                imagen.getWidth() / 4, imagen.getHeight() / 3);

        abajoQuieto     = celdas[0][0]; abajoPie1     = celdas[1][0]; abajoPie2     = celdas[2][0];
        arribaQuieto    = celdas[0][1]; arribaPie1    = celdas[1][1]; arribaPie2    = celdas[2][1];
        derechaQuieto   = celdas[0][2]; derechaPie1   = celdas[1][2]; derechaPie2   = celdas[2][2];
        izquierdaQuieto = celdas[0][3]; izquierdaPie1 = celdas[1][3]; izquierdaPie2 = celdas[2][3];
    }


    private void cargarImagenesNiño() {
        abajoQuieto = new TextureRegion(imagen);
        abajoPie1 = nuevaRegion("jugador1_abajoca.png");
        abajoPie2 = nuevaRegion("jugador1_abajocad.png");

        arribaQuieto = nuevaRegion("jugador1UP.png");
        arribaPie1 = nuevaRegion("jugador1_arriba_ca.png");
        arribaPie2 = nuevaRegion("jugador1_arribacad.png");

        derechaQuieto = nuevaRegion("jugador1.png");
        derechaPie1 = nuevaRegion("jugador1_derechaca.png");
        derechaPie2 = nuevaRegion("jugador1_derechacad.png");

        izquierdaQuieto = nuevaRegion("jugador1IZQ.png");
        izquierdaPie1 = nuevaRegion("jugador1_izqca.png");
        izquierdaPie2 = nuevaRegion("jugador1_izquierdacad.png");
    }

    private TextureRegion nuevaRegion(String ruta) {
        Texture t = new Texture(ruta);
        texturasPropias.add(t);
        return new TextureRegion(t);
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        moverEn(mapa, delta);

        mapa.obtenerObjeto(caja(x, y));


        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Rectangle miCaja = caja(x, y);
            mapa.dispararLinea(miCaja.x + miCaja.width / 2f,
                               miCaja.y + miCaja.height / 2f,
                               direccionMirando);
        }
    }


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
                    case 0: regionActual = direccionPie1; break;
                    case 2: regionActual = direccionPie2; break;
                    default: regionActual = direccionQuieta; break;
                }
            } else {
                int frame = (int) ((tiempoAnimacion % 0.4f) / 0.2f);
                regionActual = (frame == 0) ? direccionPie1 : direccionPie2;
            }
        } else {
            tiempoAnimacion = 0;
            regionActual = direccionQuieta;
        }
    }


    @Override
    public void dibujar(SpriteBatch batch) {
        dibujar(batch, TAMANO_NORMAL);
    }


    @Override
    public void dibujar(SpriteBatch batch, float tamano) {
        boolean rojo = false;
        if (tiempoSinPerderVidas > 0) {
            int tramo = (int) (tiempoSinPerderVidas / (INVENCIBILIDAD / 5f));
            rojo = (tramo % 2 == 0); //alterna rojo y normal en 5 tramos
        }

        float corrimiento = (tamano - TAMANO_NORMAL) / 2f;
        if (rojo) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
            batch.draw(regionActual, x - corrimiento, y, tamano, tamano);
            batch.setColor(Color.WHITE); //se devuelve el color o se tiñe todo lo demas
        } else {
            batch.draw(regionActual, x - corrimiento, y, tamano, tamano);
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
            x = spawnX; y = spawnY;
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
        for (Texture t : texturasPropias) {
            t.dispose();
        }
        texturasPropias.clear();
    }
}
