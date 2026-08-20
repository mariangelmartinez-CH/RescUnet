package com.juego.progra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Menu {

    public enum Opcion { NINGUNA, JUGAR, SALIR, CREDITOS }

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;


    private static final float CRECIDA = 1.08f;

    private Texture fondo;
    private Texture[] botones;

    private final Rectangle[] dibujos = {
        new Rectangle(502, 239, 396, 159),  //JUGAR
        new Rectangle(519, 121, 363, 145),  //SALIR
        new Rectangle(532,  20, 337, 112)   //CREDITOS
    };

    private final Rectangle[] cajas = {
        new Rectangle(550, 274, 300, 94),   //JUGAR
        new Rectangle(550, 148, 300, 95),   //SALIR
        new Rectangle(595,  49, 210, 56)    //CREDITOS
    };

    private final Opcion[] opciones = { Opcion.JUGAR, Opcion.SALIR, Opcion.CREDITOS };
    private int senalado = -1;

    private static final float CARRIL_IZQ_MIN = 424, CARRIL_IZQ_MAX = 490;
    private static final float CARRIL_DER_MIN = 905, CARRIL_DER_MAX = 970;
    private static final float VUELO_ABAJO = 30, VUELO_ARRIBA = 390;

    private static final float SEPARACION_MINIMA = 60;

    private Texture[] hojasMariposas;
    private ArrayList<MariposaMenu> mariposas = new ArrayList<>();

    private Texture creditos;
    private boolean mostrandoCreditos = false;

    private static final float TAMANO_CREDITOS = 620;


    private static final float OSCURIDAD = 0.55f;

    private Texture pixel;

    private Sound sonidoBoton;

    private boolean clicEsteFrame = false;

    public boolean huboClic() {
        return clicEsteFrame;
    }

    public Menu() {
        fondo = new Texture("MenuPrincipalsinBotones.png");
        botones = new Texture[]{
            new Texture("botonprinc1.png"),   //JUGAR
            new Texture("botonprinc2.png"),   //SALIR
            new Texture("botonprinc3.png")    //CREDITOS
        };

        sonidoBoton = Gdx.audio.newSound(Gdx.files.internal("boton.ogg"));

        creditos = new Texture("creditos_.png");

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();


        hojasMariposas = new Texture[]{
            new Texture("mariposasMenu.png"),
            new Texture("mariposasMenu2.png"),
            new Texture("mariposasMenu3.png")
        };

        ArrayList<Float> alturas = new ArrayList<>();
        float centro = (VUELO_ABAJO + VUELO_ARRIBA) / 2f;
        alturas.add(centro);
        for (int i = 0; i < 2; i++) {
            alturas.add(alturaSeparada(alturas));
        }

        for (int i = 0; i < alturas.size(); i++) {
            float y = alturas.get(i);
            Texture hoja = hojasMariposas[i];
            int fila = MathUtils.random(2);
            agregarMariposa(CARRIL_IZQ_MIN, CARRIL_IZQ_MAX, y, hoja, fila);
            agregarMariposa(CARRIL_DER_MIN, CARRIL_DER_MAX, y, hoja, fila);
        }
    }

    private float alturaSeparada(ArrayList<Float> alturas) {
        float y = VUELO_ABAJO;
        for (int intento = 0; intento < 30; intento++) {
            y = MathUtils.random(VUELO_ABAJO, VUELO_ARRIBA);
            boolean lejos = true;
            for (float otra : alturas) {
                if (Math.abs(y - otra) < SEPARACION_MINIMA) { lejos = false; break; }
            }
            if (lejos) break;
        }
        return y;
    }

    private void agregarMariposa(float min, float max, float y, Texture hoja, int fila) {
        mariposas.add(new MariposaMenu(hoja, fila, min, max, y));
    }


    public Opcion actualizar() {
        clicEsteFrame = false;

        float ratonX = Gdx.input.getX() * (float) ANCHO_MUNDO / Gdx.graphics.getWidth();
        float ratonY = ALTO_MUNDO
                     - (Gdx.input.getY() * (float) ALTO_MUNDO / Gdx.graphics.getHeight());

        float delta = Gdx.graphics.getDeltaTime();
        for (int i = 0; i < mariposas.size(); i++) {
            mariposas.get(i).actualizar(delta);
        }

        if (mostrandoCreditos) {
            senalado = -1;
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                mostrandoCreditos = false;
            }
            return Opcion.NINGUNA;
        }

        senalado = -1;
        for (int i = 0; i < cajas.length; i++) {
            if (cajas[i].contains(ratonX, ratonY)) {
                senalado = i;
                break;
            }
        }

        if (senalado >= 0 && Gdx.input.justTouched()) {
            sonidoBoton.play();
            clicEsteFrame = true;
            if (opciones[senalado] == Opcion.CREDITOS) {
                mostrandoCreditos = true;
                return Opcion.NINGUNA;
            }
            return opciones[senalado];
        }
        return Opcion.NINGUNA;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(fondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        for (int i = 0; i < mariposas.size(); i++) {
            mariposas.get(i).dibujar(batch);
        }

        for (int i = 0; i < botones.length; i++) {
            if (i != senalado) {
                dibujarBoton(batch, i, 1f);
            }
        }
        if (senalado >= 0) {
            dibujarBoton(batch, senalado, CRECIDA);
        }

        if (mostrandoCreditos) {
            batch.setColor(0f, 0f, 0f, OSCURIDAD);
            batch.draw(pixel, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
            batch.setColor(Color.WHITE);

            batch.draw(creditos,
                       ANCHO_MUNDO / 2f - TAMANO_CREDITOS / 2f,
                       ALTO_MUNDO / 2f - TAMANO_CREDITOS / 2f,
                       TAMANO_CREDITOS, TAMANO_CREDITOS);
        }
    }

    private void dibujarBoton(SpriteBatch batch, int i, float escala) {
        Rectangle d = dibujos[i];
        float ancho = d.width * escala;
        float alto = d.height * escala;
        float x = d.x + d.width / 2f - ancho / 2f;
        float y = d.y + d.height / 2f - alto / 2f;
        batch.draw(botones[i], x, y, ancho, alto);
    }

    public void dispose() {
        if (fondo != null) fondo.dispose();
        if (botones != null) {
            for (Texture t : botones) {
                if (t != null) t.dispose();
            }
        }
        if (hojasMariposas != null) {
            for (Texture t : hojasMariposas) {
                if (t != null) t.dispose();
            }
        }
        if (creditos != null) creditos.dispose();
        if (pixel != null) pixel.dispose();
        if (sonidoBoton != null) sonidoBoton.dispose();
    }
}
