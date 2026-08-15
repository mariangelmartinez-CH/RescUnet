package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

//El mapa de seleccion de niveles. El jugador solo camina por el camino de
//piedra, que va del arco de entrada (abajo a la izquierda) hasta la casa
//(arriba a la derecha), pasando por las tres plazoletas.
//
//El camino NO se lee de la imagen: se describe con 4 rectangulos y 3 circulos,
//que dan bordes rectos y limpios. Las medidas estan en pixeles del dibujo
//original (1697x927), no del mundo, y se convierten al vuelo.
public class MapaPrincipal implements Caminable {

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    private static final float ANCHO_IMAGEN = 1697;
    private static final float ALTO_IMAGEN = 927;

    //Los cuatro tramos rectos: x, y, ancho, alto (en pixeles del dibujo).
    //Van un poco mas angostos que el camino dibujado para que el jugador
    //no llegue a pisar el borde de piedra.
    private static final float[][] TRAMOS = {
        {196, 520,  75, 360},   //bajada desde la mariposa hasta el arco
        {231, 501, 742,  55},   //mariposa -> conejo
        {936, 226,  73, 293},   //conejo -> serpiente
        {969, 214, 428,  48}    //serpiente -> casa
    };

    //Las tres plazoletas: centro x, centro y, radio.
    //Los centros salen de medir el borde libre de cada circulo en el dibujo,
    //y el radio va unos pixeles por dentro para no pisar el borde de piedra.
    private static final float[][] PLAZOLETAS = {
        {233, 521, 88},   //mariposa
        {972, 534, 82},   //conejo
        {972, 229, 82}    //serpiente
    };

    private Texture fondo;
    private Texture arco;
    private Texture bosque;
    private Texture[] medallas = new Texture[3];

    //Cuantos niveles lleva ganados: 0 a 3. Decide cuantas medallas se encienden.
    private int nivelesGanados = 0;

    public MapaPrincipal() {
        fondo  = new Texture("mapaPrincipal.png");
        arco   = new Texture("mapaArco.png");
        bosque = new Texture("mapaBosque.png");

        medallas[0] = new Texture("medallaMariposa.png");
        medallas[1] = new Texture("medallaConejo.png");
        medallas[2] = new Texture("medallaSerpiente.png");
    }

    //Pasa una coordenada del mundo (1408x768, con la Y hacia arriba) a un
    //pixel del dibujo (mas grande y con la Y hacia abajo) y dice si es camino
    private boolean pisoValido(float mundoX, float mundoY) {
        float px = mundoX * ANCHO_IMAGEN / ANCHO_MUNDO;
        float py = (ALTO_MUNDO - mundoY) * ALTO_IMAGEN / ALTO_MUNDO;

        for (float[] p : PLAZOLETAS) {
            float dx = px - p[0];
            float dy = py - p[1];
            if (dx * dx + dy * dy <= p[2] * p[2]) return true;
        }

        for (float[] t : TRAMOS) {
            if (px >= t[0] && px <= t[0] + t[2]
             && py >= t[1] && py <= t[1] + t[3]) return true;
        }

        return false;
    }

    //La caja de choque va centrada en el sprite, pero los pies quedan unas 35
    //unidades mas abajo. Si se prueba la caja tal cual, en los tramos
    //horizontales el personaje puede bajar hasta pisar el cesped. Por eso se
    //prueba corrida hacia abajo, que es donde de verdad esta pisando.
    private static final float DESFASE_PIES = 35;

    @Override
    public boolean CaminarCelda(Rectangle r) {
        float y = r.y - DESFASE_PIES;

        //Las cuatro esquinas tienen que caer sobre el camino
        return pisoValido(r.x, y)
            && pisoValido(r.x + r.width, y)
            && pisoValido(r.x, y + r.height)
            && pisoValido(r.x + r.width, y + r.height);
    }

    //Todo lo que va DETRAS del jugador
    public void dibujarFondo(SpriteBatch batch) {
        batch.draw(fondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        //Se enciende una medalla por cada nivel ganado, en orden
        for (int i = 0; i < nivelesGanados && i < 3; i++) {
            batch.draw(medallas[i], 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        }
    }

    //Todo lo que va DELANTE del jugador: el arco, para que pase por debajo,
    //y el bosque del borde, para que se meta entre los arboles
    public void dibujarFrente(SpriteBatch batch) {
        batch.draw(bosque, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        batch.draw(arco, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
    }

    //Donde aparece el jugador: en el camino, justo debajo del arco
    public float getSpawnX() { return 143; }
    public float getSpawnY() { return 39; }

    public int getNivelesGanados() {
        return nivelesGanados;
    }

    public void setNivelesGanados(int nivelesGanados) {
        this.nivelesGanados = Math.max(0, Math.min(3, nivelesGanados));
    }

    public void dispose() {
        fondo.dispose();
        arco.dispose();
        bosque.dispose();
        for (Texture t : medallas) {
            if (t != null) t.dispose();
        }
    }
}
