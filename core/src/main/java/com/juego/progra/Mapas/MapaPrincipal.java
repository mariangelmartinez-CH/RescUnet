package com.juego.progra.Mapas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.juego.progra.Main;
import com.juego.progra.Personajes.Jugador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MapaPrincipal implements Caminable {
    private HUD hud;
    private Jugador jugador;
    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    private static final float ANCHO_IMAGEN = 1697;
    private static final float ALTO_IMAGEN = 927;


    private static final float[][] TRAMOS = {
        {196, 520,  75, 360},   //bajada desde la mariposa hasta el arco
        {231, 501, 742,  55},   //mariposa conejo
        {936, 226,  73, 293},   //conejo  serpiente
        {969, 214, 428,  48}    //serpiente  casa
    };


    private static final float[][] PLAZOLETAS = {
        {233, 521, 88},   //mariposa
        {972, 534, 82},   //conejo
        {972, 229, 82}    //serpiente
    };

    private static final float[] CASA = {1390, 190, 170, 80};

    private static final float[][] FLECHAS_IMG = {
        {1150, 238},
        {1300, 238}
    };

    private static final float FLECHA_ANCHO = 70f;
    private static final float FLECHA_ALTO = 70f * 1024f / 1536f;
    private static final float DURACION_FLECHA = 0.5f;

    private Texture fondo;
    private Texture arco;
    private Texture bosque;
    private Texture[] medallas = new Texture[3];

    //cuantos niveles lleva ganados 0 a 3 Decide cuantas medallas se encienden
    private int nivelesGanados = 0;

    //los animalitos de adorno que andan por el cesped
    private static final int CUANTOS_ANIMALES = 7;
    private Texture[] hojasAnimales;
    private ArrayList<AnimalJardin> animales = new ArrayList<>();

    //Boton
    private Texture btnMenu;
    private Texture btnMenuAb;
    private Rectangle areaBotonMenu;
    private boolean btnVisible = false;
    private boolean botonPresionado = false;
    private float tiempoBotonPresionado = 0f;
    private static final float duracion = 0.20f;
    private Sound sonidoBoton;

    private Texture flecha;
    private boolean mostrarFlecha = true;
    private float tiempoFlecha = 0f;
    private float[][] flechasMundo;

    public MapaPrincipal(Main juego) {

        fondo  = new Texture("mapaPrincipal.png");
        arco   = new Texture("mapaArco.png");
        bosque = new Texture("mapaBosque.png");

        medallas[0] = new Texture("medallaMariposa.png");
        medallas[1] = new Texture("medallaConejo.png");
        medallas[2] = new Texture("medallaSerpiente.png");

        marcarLaGrama();


        hojasAnimales = new Texture[]{
            new Texture("jardinConejoManchado.png"),
            new Texture("jardinZorro.png"),
            new Texture("jardinConejoGris.png")
        };
        for (int i = 0; i < CUANTOS_ANIMALES; i++) {
            animales.add(new AnimalJardin(hojasAnimales[i % hojasAnimales.length], this));
        }

        //bton
        btnMenu= new Texture("btnMenu.png");
        btnMenuAb= new Texture("btnMenu_Ab.png");
        areaBotonMenu = new Rectangle(50,   600, 100, 100);
        sonidoBoton = Gdx.audio.newSound(Gdx.files.internal("boton.ogg"));

        flecha = new Texture("flecha.png");
        flechasMundo = new float[FLECHAS_IMG.length][2];
        for (int i = 0; i < FLECHAS_IMG.length; i++) {
            float imgX = FLECHAS_IMG[i][0];
            float imgY = FLECHAS_IMG[i][1];
            flechasMundo[i][0] = imgX * ANCHO_MUNDO / ANCHO_IMAGEN - FLECHA_ANCHO / 2f;
            flechasMundo[i][1] = ALTO_MUNDO - imgY * ALTO_MUNDO / ALTO_IMAGEN - FLECHA_ALTO / 2f;
        }

    }
    public void dibujarBotonMenu(SpriteBatch batch) {
        if (!btnVisible) {
            return;
        }

        Texture texturaActual;

        if (botonPresionado) {
            texturaActual = btnMenuAb;
        } else {
            texturaActual = btnMenu;
        }

        batch.draw(
            texturaActual,
            areaBotonMenu.x,
            areaBotonMenu.y,
            areaBotonMenu.width,
            areaBotonMenu.height
        );
    }
    public void mostrarBotonMenu(boolean mostrar) {
        btnVisible = mostrar;
    }
    public boolean botonMenuPresionado() {
        if (!btnVisible) {
            return false;
        }

        if (!Gdx.input.justTouched()) {
            return false;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        float mundoX =
            mouseX * ANCHO_MUNDO / Gdx.graphics.getWidth();

        float mundoY =
            (Gdx.graphics.getHeight() - mouseY)
                * ALTO_MUNDO / Gdx.graphics.getHeight();

        if (areaBotonMenu.contains(mundoX, mundoY)) {

            sonidoBoton.play();
            botonPresionado = true;
            tiempoBotonPresionado = 0f;

            return true;
        }

        return false;
    }

    public boolean mouseSobreBotonMenu() {

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        float mundoX =
            mouseX * ANCHO_MUNDO / Gdx.graphics.getWidth();

        float mundoY =
            (Gdx.graphics.getHeight() - mouseY)
                * ALTO_MUNDO / Gdx.graphics.getHeight();

        return areaBotonMenu.contains(mundoX, mundoY);
    }
    public boolean actualizarBotonMenu(float delta) {

        if (!botonPresionado) {
            return false;
        }

        tiempoBotonPresionado += delta;

        if (tiempoBotonPresionado >= duracion) {

            botonPresionado = false;
            tiempoBotonPresionado = 0f;

            return true;
        }

        return false;
    }
    public void actualizarAnimales(float delta) {
        for (int i = 0; i < animales.size(); i++) {
            animales.get(i).actualizar(this, delta);
        }
    }


    public void dibujarAnimales(SpriteBatch batch) {
        Collections.sort(animales, new Comparator<AnimalJardin>() {
            @Override
            public int compare(AnimalJardin a, AnimalJardin b) {
                return Float.compare(b.getY(), a.getY());
            }
        });
        for (int i = 0; i < animales.size(); i++) {
            animales.get(i).dibujar(batch);
        }
    }


    private boolean estaAbierto(int i) {
        return i <= nivelesGanados;
    }

    //EL PRADO: por donde pueden andar los animalitos. Va metido hacia dentro
    //respecto a la pantalla para que no se salgan por los bordes ni se metan
    //del todo entre los arboles, donde no se verian.
    private static final float PRADO_IZQ = 90;
    private static final float PRADO_DER = 1320;
    private static final float PRADO_ABAJO = 70;
    private static final float PRADO_ARRIBA = 700;

    public float getPradoIzq()    { return PRADO_IZQ; }
    public float getPradoDer()    { return PRADO_DER; }
    public float getPradoAbajo()  { return PRADO_ABAJO; }
    public float getPradoArriba() { return PRADO_ARRIBA; }


    private boolean esCamino(float mundoX, float mundoY) {
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


    private static final int CELDA_GRAMA = 8;
    private static final int GRAMA_COLUMNAS = ANCHO_MUNDO / CELDA_GRAMA;
    private static final int GRAMA_FILAS = ALTO_MUNDO / CELDA_GRAMA;


    private static final float FRACCION_GRAMA = 0.80f;

    private boolean[][] grama = new boolean[GRAMA_COLUMNAS][GRAMA_FILAS];


    private boolean esColorDeGrama(int rgba) {
        int r = (rgba >>> 24) & 0xFF;
        int g = (rgba >>> 16) & 0xFF;
        int b = (rgba >>> 8) & 0xFF;

        if (b > 56) return false;
        if (g < 104 || g > 168) return false;
        if (r < 52 || r > 128) return false;
        return (g - r) >= 22;
    }

    private void marcarLaGrama() {
        Pixmap dibujo = new Pixmap(Gdx.files.internal("mapaPrincipal.png"));
        int anchoImagen = dibujo.getWidth();
        int altoImagen = dibujo.getHeight();

        for (int c = 0; c < GRAMA_COLUMNAS; c++) {
            for (int f = 0; f < GRAMA_FILAS; f++) {
                int buenos = 0;
                int total = 0;

                for (int sx = 0; sx < CELDA_GRAMA; sx += 2) {
                    for (int sy = 0; sy < CELDA_GRAMA; sy += 2) {
                        int mundoX = c * CELDA_GRAMA + sx;
                        int mundoY = f * CELDA_GRAMA + sy;

                        int px = mundoX * anchoImagen / ANCHO_MUNDO;
                        int py = (ALTO_MUNDO - mundoY) * altoImagen / ALTO_MUNDO;
                        if (px < 0) px = 0;
                        if (px >= anchoImagen) px = anchoImagen - 1;
                        if (py < 0) py = 0;
                        if (py >= altoImagen) py = altoImagen - 1;

                        total++;
                        if (esColorDeGrama(dibujo.getPixel(px, py))) buenos++;
                    }
                }
                grama[c][f] = buenos >= total * FRACCION_GRAMA;
            }
        }
        dibujo.dispose();
    }

    //true si esa pisada cae entera en grama dentro del prado, sin tocar el camino, y con TODAS las celdas que pisa marcadas como grama.
    public boolean cespedLibre(Rectangle r) {
        if (r.x < PRADO_IZQ || r.x + r.width > PRADO_DER
         || r.y < PRADO_ABAJO || r.y + r.height > PRADO_ARRIBA) {
            return false;
        }

        if (esCamino(r.x, r.y) || esCamino(r.x + r.width, r.y)
         || esCamino(r.x, r.y + r.height) || esCamino(r.x + r.width, r.y + r.height)) {
            return false;
        }

        int c = (int) ((r.x + r.width / 2f) / CELDA_GRAMA);
        int f = (int) ((r.y + r.height / 2f) / CELDA_GRAMA);
        if (c < 0 || f < 0 || c >= GRAMA_COLUMNAS || f >= GRAMA_FILAS) return false;
        return grama[c][f];
    }

    private boolean pisoValido(float mundoX, float mundoY) {
        float px = mundoX * ANCHO_IMAGEN / ANCHO_MUNDO;
        float py = (ALTO_MUNDO - mundoY) * ALTO_IMAGEN / ALTO_MUNDO;

        for (int i = 0; i < PLAZOLETAS.length; i++) {
            if (!estaAbierto(i)) continue;
            float[] p = PLAZOLETAS[i];
            float dx = px - p[0];
            float dy = py - p[1];
            if (dx * dx + dy * dy <= p[2] * p[2]) return true;
        }

        for (int i = 0; i < TRAMOS.length; i++) {
            if (!estaAbierto(i)) continue;
            float[] t = TRAMOS[i];
            if (px >= t[0] && px <= t[0] + t[2]
             && py >= t[1] && py <= t[1] + t[3]) return true;
        }

        return false;
    }


    public int nivelDisponible(Rectangle r) {
        float pieX = r.x + r.width / 2f;
        float pieY = r.y + r.height / 2f - DESFASE_PIES;

        float px = pieX * ANCHO_IMAGEN / ANCHO_MUNDO;
        float py = (ALTO_MUNDO - pieY) * ALTO_IMAGEN / ALTO_MUNDO;

        for (int i = 0; i < PLAZOLETAS.length; i++) {
            if (!estaAbierto(i)) continue;
            float[] p = PLAZOLETAS[i];
            float dx = px - p[0];
            float dy = py - p[1];
            if (dx * dx + dy * dy <= p[2] * p[2]) return i + 1;
        }
        return 0;
    }


    private static final float DESFASE_PIES = 35;

    @Override
    public boolean CaminarCelda(Rectangle r) {
        float y = r.y - DESFASE_PIES;

        //las cuatro esquinas tienen que caer sobre el camino
        return pisoValido(r.x, y)
            && pisoValido(r.x + r.width, y)
            && pisoValido(r.x, y + r.height)
            && pisoValido(r.x + r.width, y + r.height);
    }

    //Todo lo que va DETRAS del jugador
    public void dibujarFondo(SpriteBatch batch) {
        batch.draw(fondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        //se enciende una medalla por cada nivel ganado, en orden
        for (int i = 0; i < nivelesGanados && i < 3; i++) {
            batch.draw(medallas[i], 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        }

        if (nivelesGanados >= 3 && mostrarFlecha) {
            for (float[] pos : flechasMundo) {
                batch.draw(flecha, pos[0], pos[1], FLECHA_ANCHO, FLECHA_ALTO);
            }
        }
    }

    public void actualizarFlecha(float delta) {
        tiempoFlecha += delta;
        if (tiempoFlecha >= DURACION_FLECHA) {
            tiempoFlecha -= DURACION_FLECHA;
            mostrarFlecha = !mostrarFlecha;
        }
    }

    public boolean tocandoCasa(Rectangle r) {
        if (nivelesGanados < 3) return false;

        float pieX = r.x + r.width / 2f;
        float pieY = r.y + r.height / 2f - DESFASE_PIES;

        float px = pieX * ANCHO_IMAGEN / ANCHO_MUNDO;
        float py = (ALTO_MUNDO - pieY) * ALTO_IMAGEN / ALTO_MUNDO;

        return px >= CASA[0] && px <= CASA[0] + CASA[2]
            && py >= CASA[1] && py <= CASA[1] + CASA[3];
    }

    //todo lo que va DELANTE del jugador el arco, para que pase por debajo
    public void dibujarFrente(SpriteBatch batch) {
        batch.draw(bosque, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        batch.draw(arco, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
    }


    public float getSpawnX() { return 143; }
    public float getSpawnY() { return 39; }


    public float getXPiedra(int nivel) {
        float px = PLAZOLETAS[nivel - 1][0];
        float mundoX = px * ANCHO_MUNDO / ANCHO_IMAGEN;
        return mundoX - 50;
    }

    public float getYPiedra(int nivel) {
        float py = PLAZOLETAS[nivel - 1][1];
        float mundoY = ALTO_MUNDO - py * ALTO_MUNDO / ALTO_IMAGEN;
        return mundoY - (50 - DESFASE_PIES);
    }

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
        if (hojasAnimales != null) {
            for (Texture t : hojasAnimales) {
                if (t != null) t.dispose();
            }
        }
        if (sonidoBoton != null) sonidoBoton.dispose();
        if (flecha != null) flecha.dispose();

    }
}
