package com.juego.progra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;


public class SeleccionPersonaje {

    public enum Opcion { NINGUNA, PERSONAJE1, PERSONAJE2 }

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;


    private static final float CRECIDA = 1.08f;

    private Texture fondo;
    private Texture[] personajes;


    private final Rectangle[] cajas = {
        new Rectangle(328, 184, 270, 339),
        new Rectangle(806, 184, 276, 339)
    };

    private final Rectangle[] dibujos = {
        new Rectangle(259, 167, 376, 364),  //PERSONAJE1
        new Rectangle(771, 174, 361, 350)   //PERSONAJE2
    };

    private final Opcion[] opciones = { Opcion.PERSONAJE1, Opcion.PERSONAJE2 };


    private int senalado = -1;


    private static final float[][] CARRILES = {
        { 150,  238},   //a la izquierda del marco de la niña
        { 648,  716},   //en el pasillo del centro, entre los dos marcos
        {1130, 1218}    //a la derecha del marco del niño
    };


    private static final float[] ALTURAS = { 470, 255 };

    private Texture[] hojasMariposas;
    private ArrayList<MariposaMenu> mariposas = new ArrayList<>();

    public SeleccionPersonaje() {
        fondo = new Texture("pantalla_seleccion.png");
        personajes = new Texture[]{
            new Texture("pantallaseleccion1.png"),  //niña, cuerpo completo
            new Texture("pantallaseleccion4.png")   //niño,  cuerpo completo
        };


        hojasMariposas = new Texture[]{
            new Texture("mariposasMenu.png"),
            new Texture("mariposasMenu2.png")
        };

        for (int a = 0; a < ALTURAS.length; a++) {
            Texture hoja = hojasMariposas[a];
            int fila = MathUtils.random(2);
            for (int c = 0; c < CARRILES.length; c++) {
                mariposas.add(new MariposaMenu(hoja, fila,
                        CARRILES[c][0], CARRILES[c][1], ALTURAS[a]));
            }
        }
    }

    public Opcion actualizar() {
        float ratonX = Gdx.input.getX() * (float) ANCHO_MUNDO / Gdx.graphics.getWidth();
        float ratonY = ALTO_MUNDO
                     - (Gdx.input.getY() * (float) ALTO_MUNDO / Gdx.graphics.getHeight());

        senalado = -1;
        for (int i = 0; i < cajas.length; i++) {
            if (cajas[i].contains(ratonX, ratonY)) {
                senalado = i;
                break;
            }
        }

        float delta = Gdx.graphics.getDeltaTime();
        for (int i = 0; i < mariposas.size(); i++) {
            mariposas.get(i).actualizar(delta);
        }

        if (senalado >= 0 && Gdx.input.justTouched()) {
            return opciones[senalado];
        }
        return Opcion.NINGUNA;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(fondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        for (int i = 0; i < mariposas.size(); i++) {
            mariposas.get(i).dibujar(batch);
        }

        for (int i = 0; i < personajes.length; i++) {
            if (i != senalado) {
                dibujarPersonaje(batch, i, 1f);
            }
        }
        if (senalado >= 0) {
            dibujarPersonaje(batch, senalado, CRECIDA);
        }
    }

    private void dibujarPersonaje(SpriteBatch batch, int i, float escala) {
        Rectangle d = dibujos[i];
        float ancho = d.width * escala;
        float alto = d.height * escala;
        float x = d.x + d.width / 2f - ancho / 2f;
        float y = d.y + d.height / 2f - alto / 2f;
        batch.draw(personajes[i], x, y, ancho, alto);
    }

    public void dispose() {
        if (fondo != null) fondo.dispose();
        if (personajes != null) {
            for (Texture t : personajes) {
                if (t != null) t.dispose();
            }
        }
        if (hojasMariposas != null) {
            for (Texture t : hojasMariposas) {
                if (t != null) t.dispose();
            }
        }
    }
}
