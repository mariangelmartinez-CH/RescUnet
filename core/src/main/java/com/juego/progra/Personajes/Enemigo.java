package com.juego.progra.Personajes;
import com.badlogic.gdx.math.MathUtils;
import com.juego.progra.Mapas.MapasNiveles;

public class Enemigo extends Personaje {
    public static final int HORIZONTAL = 0;
    public static final int ALEATORIO  = 1;

    private int tipo;
    private float tiempoCambio = 0;
    private int direccion;

    public Enemigo(float x, float y, int tipo) {
        super(x, y, "personaje.png.png");
        this.tipo = tipo;
        this.direccion = 0;
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tipo == ALEATORIO) {
            tiempoCambio += delta;
            if (tiempoCambio >= 2) {
                direccion = MathUtils.random(0, 3);
                tiempoCambio = 0;
            }
        }

        float newX = x;
        float newY = y;

        switch (direccion) {
            case 0: newX += 100 * delta; break;
            case 1: newX -= 100 * delta; break;
            case 2: newY += 100 * delta; break;
            case 3: newY -= 100 * delta; break;
        }

        // Colisión X
        float bordeX = (newX > x) ? newX + 50 : newX;
        int filaX = (int)((y - 134) / 36);
        int colX  = (int)((bordeX - 124) / 52);
        if (mapa.CaminarCelda(filaX, colX)) {
            x = newX;
        } else {
            if (tipo == HORIZONTAL) {
                direccion = (direccion == 0) ? 1 : 0; // rebota
            } else {
                direccion = MathUtils.random(0, 3);
                tiempoCambio = 0;
            }
        }

        // Colisión Y (solo el aleatorio se mueve en Y)
        if (tipo == ALEATORIO) {
            int filaY = (int)((newY - 134) / 36);
            int colY  = (int)(((x + 25) - 124) / 52);
            if (mapa.CaminarCelda(filaY, colY)) {
                y = newY;
            } else {
                direccion = MathUtils.random(0, 3);
                tiempoCambio = 0;
            }
        }
    }
}
