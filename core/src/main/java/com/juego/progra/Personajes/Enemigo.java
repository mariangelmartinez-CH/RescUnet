package com.juego.progra.Personajes;
import com.badlogic.gdx.math.MathUtils;
import com.juego.progra.Mapas.MapasNiveles;

public class Enemigo extends Personaje {

    public enum Tipo { HORIZONTAL, ALEATORIO }

    private Tipo tipo;
    private float velocidad = 100;
    private int direccionX = 1;   //1 ES DERECHA -1 IZQUIERDA(HORIZONTAL)
    private float cambio = 0;
    private int direccion = 0;    //  ( ALEATORIO)

    public Enemigo(float x, float y, Tipo tipo) {
        super(x, y, "personaje.png.png"); //POR AHORA USAMOS LA MISMA IMAGEN DEL JUGADOR
        this.tipo = tipo;
        this.cajaTamañito = 15;
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tipo == Tipo.HORIZONTAL) {
            actualizarHorizontal(mapa, delta);
        } else {
            actualizarAleatorio(mapa, delta);
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
}
