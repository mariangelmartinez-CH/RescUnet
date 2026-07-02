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
        /*if (tiempoSinPerderVidas > 0) tiempoSinPerderVidas -= delta;

        // MOVIMIENTO EN X
        float newX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newX += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  newX -= 200 * delta;

        float bordeX = (newX >= x) ? newX + 50 : newX;
        int filaX = (int)((y - 134) / 36);
        int colX  = (int)((bordeX - 124) / 52);
        if (mapa.CaminarCelda(filaX, colX)) x = newX;

        // MOVIMIENTO EN Y SOLO LOS PIES (newY)
        float newY = y;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))   newY += 200 * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  newY -= 200 * delta;

        int filaY = (int)((newY - 134) / 36);
        int colY  = (int)(((x + 25) - 124) / 52);
        if (mapa.CaminarCelda(filaY, colY)) y = newY;
    }

    public void perderVida(){
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;// 2 SEGUNDOS SIN PERDER VIDA
        }
    }

    public void recibirGolpe() {
        if (tiempoSinPerderVidas <= 0) {
            vidas--;
            tiempoSinPerderVidas = 2;
            x = 384; y = 458; // POSICION VALIDA DENTRO DEL MAPA
            Gdx.app.log("JUEGO", "Vidas restantes: " + vidas);
        }
    }

    public int getVidas(){
        return vidas;
    }
    public boolean estaVivo(){
        return vidas > 0;
    }*/
    }
}
