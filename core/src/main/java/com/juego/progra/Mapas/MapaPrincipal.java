package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

//El mapa de seleccion de niveles. El jugador solo camina por el camino de
//piedra. El camino NO se lee de la imagen (el borde de los adoquines es
//irregular y la colision quedaba dentada): se describe con 4 tramos rectos
//y 4 circulos, que dan bordes lisos.
public class MapaPrincipal implements Caminable {

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    //Las medidas estan en pixeles del dibujo original (1672x941), no del mundo
    private static final float ANCHO_IMAGEN = 1672;
    private static final float ALTO_IMAGEN = 941;

    //Mitad del ancho del camino. Subirlo ensancha el paso, bajarlo lo angosta.
    private static final float MEDIO_CAMINO = 42;

    //Los dos tramos verticales son rectangulos: x, y, ancho, alto
    private static final float[][] RECTANGULOS = {
        {235, 300,  87, 390},   //bajada desde la casa hasta la mariposa
        {1388, 180, 77, 530}    //subida desde la serpiente hasta la bandera
    };

    //Los dos tramos diagonales no pueden ser rectangulos alineados a los ejes,
    //asi que se miden por distancia a la linea: x1, y1, x2, y2
    private static final float[][] TRAMOS = {
        {308, 684,  836, 188},   //mariposa -> conejo
        {836, 188, 1388, 708}    //conejo -> serpiente
    };

    //Plazoletas redondas: x, y, radio
    private static final float[][] CIRCULOS = {
        {308, 684,  95},   //mariposa
        {836, 188,  90},   //conejo
        {1388, 708, 98},   //serpiente
        {1444, 188, 52}    //bandera
    };

    //Los 4 dibujos del mapa, segun cuantos niveles lleva ganados el jugador
    private Texture[] fondos = new Texture[4];
    private int nivelesGanados = 0;

    public MapaPrincipal() {
        for (int i = 0; i < 4; i++) {
            fondos[i] = new Texture("mapa_p" + (i + 1) + ".png");
        }
    }

    //Distancia de un punto al tramo recto (no a la recta infinita: si el punto
    //queda pasado de un extremo, mide hasta ese extremo)
    private float distanciaATramo(float px, float py, float[] t) {
        float dx = t[2] - t[0];
        float dy = t[3] - t[1];
        float largo = dx * dx + dy * dy;

        float avance = ((px - t[0]) * dx + (py - t[1]) * dy) / largo;
        if (avance < 0) avance = 0;
        if (avance > 1) avance = 1;

        float cercaX = t[0] + avance * dx;
        float cercaY = t[1] + avance * dy;

        return (float) Math.sqrt((px - cercaX) * (px - cercaX) + (py - cercaY) * (py - cercaY));
    }

    //Pasa una coordenada del mundo (1408x768, con la Y hacia arriba) a un
    //pixel del dibujo (mas grande y con la Y hacia abajo) y dice si es camino
    private boolean pisoValido(float mundoX, float mundoY) {
        float px = mundoX * ANCHO_IMAGEN / ANCHO_MUNDO;
        float py = (ALTO_MUNDO - mundoY) * ALTO_IMAGEN / ALTO_MUNDO;

        for (float[] c : CIRCULOS) {
            float dx = px - c[0];
            float dy = py - c[1];
            if (dx * dx + dy * dy <= c[2] * c[2]) return true;
        }

        for (float[] r : RECTANGULOS) {
            if (px >= r[0] && px <= r[0] + r[2]
             && py >= r[1] && py <= r[1] + r[3]) return true;
        }

        for (float[] t : TRAMOS) {
            if (distanciaATramo(px, py, t) <= MEDIO_CAMINO) return true;
        }

        return false;
    }

    @Override
    public boolean CaminarCelda(Rectangle r) {
        //Las cuatro esquinas de la caja tienen que caer sobre el camino
        return pisoValido(r.x, r.y)
            && pisoValido(r.x + r.width, r.y)
            && pisoValido(r.x, r.y + r.height)
            && pisoValido(r.x + r.width, r.y + r.height);
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(fondos[nivelesGanados], 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
    }

    public int getNivelesGanados() {
        return nivelesGanados;
    }

    public void setNivelesGanados(int nivelesGanados) {
        //Se queda entre 0 y 3 para no salirse del arreglo de dibujos
        this.nivelesGanados = Math.max(0, Math.min(3, nivelesGanados));
    }

    public void dispose() {
        for (Texture t : fondos) {
            if (t != null) t.dispose();
        }
    }
}
