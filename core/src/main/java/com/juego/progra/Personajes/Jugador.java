package com.juego.progra.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.juego.progra.Mapas.MapasNiveles;


public class Jugador extends Personaje{
    private int vidas=5;
    private float tiempoSinPerderVidas = 0;
    public Jugador(float x, float y) {
        super(x, y, "jugador1.png");
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tiempoSinPerderVidas > 0) tiempoSinPerderVidas -= delta;

        // MOVIMIENTO EN X
        float newX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newX += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  newX -= 200 * delta;
        if (mapa.CaminarCelda(caja(newX, y))) x = newX;

        // MOVIMIENTO EN Y
        float newY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))   newY += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) newY -= 200 * delta;
        if (mapa.CaminarCelda(caja(x, newY))) y = newY;

        mapa.obtenerObjeto(caja(x, y));
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
}
