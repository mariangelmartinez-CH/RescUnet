package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.juego.progra.Mapas.MapasNiveles;

public class Enemigo extends Personaje {

    public enum Tipo { HORIZONTAL, ALEATORIO }

    private Tipo tipo;
    private float velocidad = 100;
    private int direccionX = 1;
    private float cambio = 0;
    private int direccion = 0;
    private float tiempoAnimacion = 0;
    private Texture[] derecha = new Texture[3];
    private Texture[] izquierda = new Texture[3];
    private Texture[] arriba = new Texture[3];
    private Texture[] abajo = new Texture[3];


    public Enemigo(float x, float y, Tipo tipo) {
        super(x, y, "villano_down1.png");
        this.tipo = tipo;
        this.cajaTamañito = 15;

        abajo[0] = imagen;
        for (int i = 1; i < 3; i++) {
            abajo[i] = new Texture("villano_down" + (i + 1) + ".png");
        }
        for (int i = 0; i < 3; i++) {
            derecha[i] = new Texture("villano_izq" + (i + 1) + ".png");
            izquierda[i] = new Texture("villano_der" + (i + 1) + ".png");
            arriba[i] = new Texture("villano_up" + (i + 1) + ".png");

        }
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tipo == Tipo.HORIZONTAL) {
            actualizarHorizontal(mapa, delta);
        } else {
            actualizarAleatorio(mapa, delta);
        }

        tiempoAnimacion += delta;
        int frame = (int) ((tiempoAnimacion % 0.6f) / 0.2f);
        imagen = direccionActual()[frame];
    }

    private Texture[] direccionActual() {
        if (tipo == Tipo.HORIZONTAL) {
            return direccionX == 1 ? derecha : izquierda;
        }
        switch (direccion) {
            case 0: return derecha;
            case 1: return izquierda;
            case 2: return arriba;
            default: return abajo;
        }
    }

    private void actualizarHorizontal(MapasNiveles mapa, float delta) {
        float newX = x + direccionX * velocidad * delta;
        if (mapa.CaminarCelda(caja(newX, y))) {
            x = newX;
        } else {
            direccionX *= -1;
        }
    }

    private void actualizarAleatorio(MapasNiveles mapa, float delta) {
        cambio += delta;
        if (cambio >= 2) {
            direccion = MathUtils.random(0, 3);
            cambio = 0;
        }

        float newX = x, newY = y;
        switch (direccion) {
            case 0: newX += velocidad * delta; break;
            case 1: newX -= velocidad * delta; break;
            case 2: newY += velocidad * delta; break;
            case 3: newY -= velocidad * delta; break;
        }

        if (mapa.CaminarCelda(caja(newX, newY))) {
            x = newX;
            y = newY;
        } else {
            cambio = 2;
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < 3; i++) {
            derecha[i].dispose();
            izquierda[i].dispose();
            arriba[i].dispose();
            abajo[i].dispose();
        }
    }
}
