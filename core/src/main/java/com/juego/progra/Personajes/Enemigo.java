package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.juego.progra.Mapas.MapasNiveles;

import java.util.ArrayList;

public class Enemigo extends Personaje {

    public enum Tipo { HORIZONTAL, VERTICAL, ALEATORIO }

    private Tipo tipo;
    private float velocidad = 100;
    private int direccionX = 1;
    private int direccionY = 1;   // 1 = sube, -1 = baja (para VERTICAL)
    private float cambio = 0;
    private int direccion = 0;
    private float tiempoAnimacion = 0;


    private TextureRegion[] derecha, izquierda, arriba, abajo;
    private TextureRegion regionActual;


    private float largoSprite = 105;


    private ArrayList<Texture> texturasPropias = new ArrayList<>();

    public Enemigo(float x, float y, Tipo tipo) {
        this(x, y, tipo, 1);
    }

    public Enemigo(float x, float y, Tipo tipo, int nivel) {
        super(x, y, hojaDelNivel(nivel));
        this.tipo = tipo;
        this.cajaTamañito = 15;
        texturasPropias.add(imagen);

        if (nivel == 2) {
            cargarHojaZorro();
        } else if (nivel == 3) {
            this.largoSprite = 150;
            this.velocidad = 200;
            this.cajaTamañito = 4;
            cargarHojaSerpiente();
        } else {
            cargarImagenesSueltas();
        }
        regionActual = abajo[0];
    }

    private static String hojaDelNivel(int nivel) {
        if (nivel == 2) return "villano2.png";
        if (nivel == 3) return "villano3_down1.png";
        return "villano_down1.png";
    }


    private void cargarHojaZorro() {
        TextureRegion[][] celdas = TextureRegion.split(imagen,
                imagen.getWidth() / 4, imagen.getHeight() / 3);

        abajo     = new TextureRegion[3];
        arriba    = new TextureRegion[3];
        derecha   = new TextureRegion[3];
        izquierda = new TextureRegion[3];

        for (int f = 0; f < 3; f++) {
            abajo[f]     = celdas[f][0];
            arriba[f]    = celdas[f][1];
            derecha[f]   = celdas[f][2];
            izquierda[f] = celdas[f][3];
        }
    }


    private void cargarHojaSerpiente() {
        abajo     = new TextureRegion[4];
        arriba    = new TextureRegion[4];
        derecha   = new TextureRegion[4];
        izquierda = new TextureRegion[4];

        abajo[0] = new TextureRegion(imagen);
        for (int i = 1; i < 4; i++) {
            abajo[i] = nuevaRegion("villano3_down" + (i + 1) + ".png");
        }
        for (int i = 0; i < 4; i++) {
            derecha[i]   = nuevaRegion("villano3_der" + (i + 1) + ".png");
            izquierda[i] = nuevaRegion("villano3_izq" + (i + 1) + ".png");
            arriba[i]    = nuevaRegion("villano3_up" + (i + 1) + ".png");
        }
    }


    private void cargarImagenesSueltas() {
        abajo     = new TextureRegion[3];
        arriba    = new TextureRegion[3];
        derecha   = new TextureRegion[3];
        izquierda = new TextureRegion[3];

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


    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public void actualizar(MapasNiveles mapa, float delta) {
        if (tipo == Tipo.HORIZONTAL) {
            actualizarHorizontal(mapa, delta);
        } else if (tipo == Tipo.VERTICAL) {
            actualizarVertical(mapa, delta);
        } else {
            actualizarAleatorio(mapa, delta);
        }

        tiempoAnimacion += delta;

        TextureRegion[] cuadros = direccionActual();
        int frame = ((int) (tiempoAnimacion / 0.2f)) % cuadros.length;
        regionActual = cuadros[frame];
    }

    @Override
    public void dibujar(SpriteBatch batch) {

        float w = regionActual.getRegionWidth();
        float h = regionActual.getRegionHeight();
        float escala = largoSprite / Math.max(w, h);
        float ancho = w * escala;
        float alto = h * escala;


        float centroX = x + 52.5f;
        float centroY = y + 52.5f;
        batch.draw(regionActual, centroX - ancho / 2f, centroY - alto / 2f, ancho, alto);
    }

    private TextureRegion[] direccionActual() {
        if (tipo == Tipo.HORIZONTAL) {
            return direccionX == 1 ? derecha : izquierda;
        }
        if (tipo == Tipo.VERTICAL) {
            return direccionY == 1 ? arriba : abajo;
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


    private void actualizarVertical(MapasNiveles mapa, float delta) {
        float newY = y + direccionY * velocidad * delta;
        if (mapa.CaminarCelda(caja(x, newY))) {
            y = newY;
        } else {
            direccionY *= -1;
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


        int inicio = MathUtils.random(0, 3);
        for (int i = 0; i < 4; i++) {
            int d = (inicio + i) % 4;
            if (moverEnDireccion(mapa, d, paso)) {
                direccion = d;
                cambio = 0;
                return;
            }
        }

    }


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
