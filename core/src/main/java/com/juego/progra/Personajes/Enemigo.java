package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.juego.progra.Mapas.MapasNiveles;

import java.util.ArrayList;

public class Enemigo extends Personaje {

    public enum Tipo { HORIZONTAL, ALEATORIO }

    private Tipo tipo;
    private float velocidad = 100;
    private int direccionX = 1;
    private float cambio = 0;
    private int direccion = 0;
    private float tiempoAnimacion = 0;

    //Los sprites se guardan como TextureRegion para poder usar tanto imagenes
    //sueltas (nivel 1) como una sola hoja recortada (nivel 2)
    private TextureRegion[] derecha = new TextureRegion[3];
    private TextureRegion[] izquierda = new TextureRegion[3];
    private TextureRegion[] arriba = new TextureRegion[3];
    private TextureRegion[] abajo = new TextureRegion[3];
    private TextureRegion regionActual;

    //Las texturas propias hay que liberarlas al cerrar
    private ArrayList<Texture> texturasPropias = new ArrayList<>();

    public Enemigo(float x, float y, Tipo tipo) {
        this(x, y, tipo, 1);
    }

    public Enemigo(float x, float y, Tipo tipo, int nivel) {
        super(x, y, nivel == 2 ? "villano2.png" : "villano_down1.png");
        this.tipo = tipo;
        this.cajaTamañito = 15;
        texturasPropias.add(imagen);

        if (nivel == 2) {
            cargarHoja();
        } else {
            cargarImagenesSueltas();
        }
        regionActual = abajo[0];
    }

    //NIVEL 2: una sola imagen partida en 4 columnas (abajo, arriba, derecha,
    //izquierda) por 3 filas (los frames de la animacion)
    private void cargarHoja() {
        TextureRegion[][] celdas = TextureRegion.split(imagen,
                imagen.getWidth() / 4, imagen.getHeight() / 3);

        for (int f = 0; f < 3; f++) {
            abajo[f]     = celdas[f][0];
            arriba[f]    = celdas[f][1];
            derecha[f]   = celdas[f][2];
            izquierda[f] = celdas[f][3];
        }
    }

    //NIVEL 1: la mariposa negra, que son 12 archivos separados
    private void cargarImagenesSueltas() {
        abajo[0] = new TextureRegion(imagen);
        for (int i = 1; i < 3; i++) {
            abajo[i] = nuevaRegion("villano_down" + (i + 1) + ".png");
        }
        for (int i = 0; i < 3; i++) {
            derecha[i]   = nuevaRegion("villano_izq" + (i + 1) + ".png");
            izquierda[i] = nuevaRegion("villano_der" + (i + 1) + ".png");
            arriba[i]    = nuevaRegion("villano_up" + (i + 1) + ".png");
        }
    }

    private TextureRegion nuevaRegion(String ruta) {
        Texture t = new Texture(ruta);
        texturasPropias.add(t);
        return new TextureRegion(t);
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
        regionActual = direccionActual()[frame];
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        //Se respeta la proporcion del sprite para que el zorro no salga estirado
        float ancho = 105;
        float alto = ancho * regionActual.getRegionHeight() / regionActual.getRegionWidth();
        batch.draw(regionActual, x, y, ancho, alto);
    }

    private TextureRegion[] direccionActual() {
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

        float paso = velocidad * delta;
        if (moverEnDireccion(mapa, direccion, paso)) return;

        //Si choco, busca una direccion que SI este libre y se queda con ella.
        //Antes solo re-sorteaba, y si estaba encerrado sorteaba cada frame,
        //lo que se veia como si girara en el sitio sin avanzar.
        int inicio = MathUtils.random(0, 3);
        for (int i = 0; i < 4; i++) {
            int d = (inicio + i) % 4;
            if (moverEnDireccion(mapa, d, paso)) {
                direccion = d;
                cambio = 0;
                return;
            }
        }
        //Ninguna sirve: se queda quieto mirando a donde estaba, sin girar
    }

    //Mueve un paso en la direccion dada. Devuelve false si ahi hay algo.
    private boolean moverEnDireccion(MapasNiveles mapa, int d, float paso) {
        float newX = x, newY = y;
        switch (d) {
            case 0: newX += paso; break;
            case 1: newX -= paso; break;
            case 2: newY += paso; break;
            case 3: newY -= paso; break;
        }
        if (!mapa.CaminarCelda(caja(newX, newY))) return false;
        x = newX;
        y = newY;
        return true;
    }

    @Override
    public void dispose() {
        for (Texture t : texturasPropias) {
            t.dispose();
        }
        texturasPropias.clear();
    }
}
