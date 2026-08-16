package com.juego.progra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class MariposaMenu {

    private static final int CUADROS = 4;
    private static final int FILAS = 3;


    private static final float TAMANO = 42;


    private static final float DURACION_CUADRO = 0.07f;

    private static final float VELOCIDAD_MINIMA = 28;
    private static final float VELOCIDAD_MAXIMA = 50;

    private TextureRegion[] cuadros = new TextureRegion[CUADROS];
    private TextureRegion[] cuadrosVolteados = new TextureRegion[CUADROS];

    private final float minX, maxX;
    private float x, y;
    private int direccion; // 1 = va hacia la derecha, -1 = hacia la izquierda
    private final float velocidad;
    private float tiempoAnimacion;


    public MariposaMenu(Texture hoja, int fila, float minX, float maxX, float y) {
        TextureRegion[][] celdas = TextureRegion.split(hoja,
                hoja.getWidth() / CUADROS, hoja.getHeight() / FILAS);

        for (int c = 0; c < CUADROS; c++) {
            cuadros[c] = celdas[fila][c];
            cuadrosVolteados[c] = new TextureRegion(celdas[fila][c]);
            cuadrosVolteados[c].flip(true, false);
        }

        this.minX = minX;
        this.maxX = maxX;
        this.y = y;
        this.x = MathUtils.random(minX, maxX);
        this.velocidad = MathUtils.random(VELOCIDAD_MINIMA, VELOCIDAD_MAXIMA);
        this.direccion = MathUtils.randomBoolean() ? 1 : -1;
        this.tiempoAnimacion = MathUtils.random(0f, 1f);
    }

    public void actualizar(float delta) {
        x += direccion * velocidad * delta;


        if (x < minX) { x = minX; direccion = 1; }
        if (x > maxX) { x = maxX; direccion = -1; }

        tiempoAnimacion += delta;
    }

    public void dibujar(SpriteBatch batch) {
        int cuadro = ((int) (tiempoAnimacion / DURACION_CUADRO)) % CUADROS;
        TextureRegion region = (direccion == 1) ? cuadros[cuadro] : cuadrosVolteados[cuadro];
        batch.draw(region, x, y, TAMANO, TAMANO);
    }
}
