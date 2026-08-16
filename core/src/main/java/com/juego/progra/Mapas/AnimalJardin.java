package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

//Un conejo o un zorro de los que andan sueltos por el cesped del mapa
//principal. No son enemigos ni se puede interactuar con ellos: estan de
//adorno, para que el jardin se vea vivo. Nunca se suben al camino de piedra,
//asi que no estorban al jugador.
//
//Las hojas vienen en 3 columnas (los cuadros de la animacion) por 4 filas
//(las direcciones), en este orden: abajo, izquierda, derecha, arriba.
public class AnimalJardin {

    private static final int ABAJO = 0, IZQUIERDA = 1, DERECHA = 2, ARRIBA = 3;
    private static final int CUADROS = 3;

    //Lo que mide el lado mas largo del dibujo. Bastante mas chico que el
    //jugador, para que se vean como bichitos del fondo y no como personajes.
    private static final float TAMANO = 62;

    //La pisada: el trocito de suelo que de verdad ocupa. Va en la parte de
    //abajo del dibujo, que es donde tiene las patas.
    private static final float PISADA_ANCHO = 16;
    private static final float PISADA_ALTO = 8;

    //Van lentos a proposito: es un jardin tranquilo, no una carrera.
    private static final float VELOCIDAD_MINIMA = 12;
    private static final float VELOCIDAD_MAXIMA = 24;

    private TextureRegion[][] cuadros = new TextureRegion[4][CUADROS];

    private float x, y;
    private int direccion = ABAJO;
    private float velocidad;

    private float tiempoAnimacion = 0;
    private float tiempoParaDecidir = 0;
    private boolean caminando = false;

    public AnimalJardin(Texture hoja, MapaPrincipal mapa) {
        TextureRegion[][] celdas = TextureRegion.split(hoja,
                hoja.getWidth() / CUADROS, hoja.getHeight() / 4);

        //celdas viene como [fila][columna]: la fila es la direccion
        for (int fila = 0; fila < 4; fila++) {
            for (int col = 0; col < CUADROS; col++) {
                cuadros[fila][col] = celdas[fila][col];
            }
        }

        this.velocidad = MathUtils.random(VELOCIDAD_MINIMA, VELOCIDAD_MAXIMA);
        this.direccion = MathUtils.random(0, 3);
        buscarSitio(mapa);
        decidirQueHacer();
    }

    //Lo suelta en un punto de cesped al azar. Si despues de varios intentos no
    //encuentra ninguno bueno, se queda donde cayo el ultimo: no vale la pena
    //buscar para siempre por un bicho de adorno.
    private void buscarSitio(MapaPrincipal mapa) {
        for (int intento = 0; intento < 200; intento++) {
            x = MathUtils.random(mapa.getPradoIzq(), mapa.getPradoDer() - PISADA_ANCHO);
            y = MathUtils.random(mapa.getPradoAbajo(), mapa.getPradoArriba() - PISADA_ALTO);
            if (mapa.cespedLibre(pisada(x, y))) return;
        }
    }

    private Rectangle pisada(float px, float py) {
        return new Rectangle(px, py, PISADA_ANCHO, PISADA_ALTO);
    }

    //Cada cierto rato decide si ponerse a andar o quedarse quieto un momento.
    //Los ratos quietos son lo que hace que se vea calmado.
    private void decidirQueHacer() {
        caminando = MathUtils.randomBoolean(0.55f);
        if (caminando) {
            direccion = MathUtils.random(0, 3);
            tiempoParaDecidir = MathUtils.random(1.5f, 4f);
        } else {
            tiempoParaDecidir = MathUtils.random(1f, 3.5f);
        }
    }

    public void actualizar(MapaPrincipal mapa, float delta) {
        tiempoParaDecidir -= delta;
        if (tiempoParaDecidir <= 0) {
            decidirQueHacer();
        }

        if (!caminando) return;

        float paso = velocidad * delta;
        float nuevaX = x, nuevaY = y;
        switch (direccion) {
            case DERECHA:   nuevaX += paso; break;
            case IZQUIERDA: nuevaX -= paso; break;
            case ARRIBA:    nuevaY += paso; break;
            default:        nuevaY -= paso; break;
        }

        if (mapa.cespedLibre(pisada(nuevaX, nuevaY))) {
            x = nuevaX;
            y = nuevaY;
            tiempoAnimacion += delta;
        } else {
            //Se topo con el camino o con el borde del prado: se da la vuelta y
            //se queda quieto un momento, como olfateando
            caminando = false;
            direccion = MathUtils.random(0, 3);
            tiempoParaDecidir = MathUtils.random(0.6f, 1.6f);
        }
    }

    public void dibujar(SpriteBatch batch) {
        //Quieto se queda en el primer cuadro; andando pasa los tres
        int cuadro = 0;
        if (caminando) {
            cuadro = ((int) (tiempoAnimacion / 0.22f)) % CUADROS;
        }
        TextureRegion region = cuadros[direccion][cuadro];

        //Se escala por el lado mas largo para que no se deforme, igual que los
        //villanos, y se centra sobre la pisada.
        float w = region.getRegionWidth();
        float h = region.getRegionHeight();
        float escala = TAMANO / Math.max(w, h);
        float ancho = w * escala;
        float alto = h * escala;

        float dibujoX = x + PISADA_ANCHO / 2f - ancho / 2f;
        batch.draw(region, dibujoX, y, ancho, alto);
    }

    //Para ordenarlos por profundidad: cuanto mas abajo, mas al frente
    public float getY() {
        return y;
    }
}
