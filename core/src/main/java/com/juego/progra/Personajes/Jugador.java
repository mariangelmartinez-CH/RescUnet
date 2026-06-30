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
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  x -= 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    y += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  y -= 200 * delta;

        x = Math.max(0, Math.min(x, 800 - 50));
        y = Math.max(0, Math.min(y, 480 - 50));
    }

    public void perderVida(){
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;// 2 segundos de invencibilidad
        }
    }

    public void recibirGolpe() {
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;
            x = 100; // regresa a posición inicial
            y = 100;
        }
    }

    public int getVidas(){
        return vidas;
    }
    public boolean estaVivo(){
        return vidas > 0;
    }

}
