package com.juego.progra.Mapas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    //Los animalitos de adorno que andan por el cesped
    private static final int CUANTOS_ANIMALES = 7;
    private Texture[] hojasAnimales;
    private ArrayList<AnimalJardin> animales = new ArrayList<>();

    public MapaPrincipal() {
        fondo  = new Texture("mapaPrincipal.png");
        arco   = new Texture("mapaArco.png");
        bosque = new Texture("mapaBosque.png");

        medallas[0] = new Texture("medallaMariposa.png");
        medallas[1] = new Texture("medallaConejo.png");
        medallas[2] = new Texture("medallaSerpiente.png");

        //Hay que saber donde esta la grama ANTES de soltar los animales, que
        //buscan sitio nada mas nacer.
        marcarLaGrama();

        //Los bichitos del jardin. Las hojas se cargan UNA vez y se reparten
        //entre todos: si cada animal cargara la suya se gastaria memoria de
        //balde, porque varios usan el mismo dibujo.
        hojasAnimales = new Texture[]{
            new Texture("jardinConejoManchado.png"),
            new Texture("jardinZorro.png"),
            new Texture("jardinConejoGris.png")
        };
        for (int i = 0; i < CUANTOS_ANIMALES; i++) {
            animales.add(new AnimalJardin(hojasAnimales[i % hojasAnimales.length], this));
        }
    }

    public void actualizarAnimales(float delta) {
        for (int i = 0; i < animales.size(); i++) {
            animales.get(i).actualizar(this, delta);
        }
    }

    //Se pintan de arriba hacia abajo, para que el que esta mas al frente tape
    //al que esta mas atras, igual que se hace dentro de los niveles.
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

    //Pasa una coordenada del mundo (1408x768, con la Y hacia arriba) a un
    //pixel del dibujo (mas grande y con la Y hacia abajo) y dice si es camino
    //El camino esta cerrado por partes: al principio solo se llega hasta la
    //piedra de la mariposa, y cada nivel ganado abre el tramo siguiente.
    //  0 ganados -> tramo 0 (arco->mariposa) y la piedra de la mariposa
    //  1 ganado  -> ademas el tramo mariposa->conejo y la piedra del conejo
    //  2 ganados -> ademas el tramo conejo->serpiente y su piedra
    //  3 ganados -> ademas el ultimo tramo, el que llega a la casa
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

    //Igual que pisoValido pero SIN mirar lo que esta desbloqueado: dice si ese
    //punto es camino de piedra, abierto o no. Los animales lo usan al reves,
    //para no subirse nunca al camino aunque el jugador todavia no pueda pasar.
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

    //LA GRAMA LISA. El mapa trae arboles, bambu, matas de flores y piedras
    //pintados encima del cesped, y los animalitos no tienen que pisarlos. Todo
    //eso va DENTRO de mapaPrincipal.png y no son figuras que se puedan
    //describir con cuatro rectangulos, asi que en vez de medirlas a mano se
    //mira la imagen: al cargar se recorre una vez y se apunta que trocitos son
    //grama y cuales no. Se guarda por celdas y no pixel a pixel para que
    //preguntarlo despues sea instantaneo.
    private static final int CELDA_GRAMA = 8;
    private static final int GRAMA_COLUMNAS = ANCHO_MUNDO / CELDA_GRAMA;
    private static final int GRAMA_FILAS = ALTO_MUNDO / CELDA_GRAMA;

    //Que parte de la celda tiene que ser grama para darla por buena. No se
    //pide el 100% porque el cesped tiene florecitas sueltas de un par de
    //pixeles, y por esas si se puede caminar.
    private static final float FRACCION_GRAMA = 0.80f;

    private boolean[][] grama = new boolean[GRAMA_COLUMNAS][GRAMA_FILAS];

    //El cesped es verde muy saturado (el azul casi no existe) y CLARO. Los
    //arboles son ese mismo verde pero oscuro, el bambu tira a amarillo (el
    //rojo sube hasta acercarse al verde), y las flores, las piedras, el camino
    //y la casa tienen el azul o el rojo altos. Con eso se separan todos.
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

    //True si esa pisada cae entera en grama: dentro del prado, sin tocar el
    //camino, y con TODAS las celdas que pisa marcadas como grama.
    public boolean cespedLibre(Rectangle r) {
        if (r.x < PRADO_IZQ || r.x + r.width > PRADO_DER
         || r.y < PRADO_ABAJO || r.y + r.height > PRADO_ARRIBA) {
            return false;
        }

        //El camino ya sale del color, pero se comprueba aparte para que la
        //regla de no subirse nunca a la piedra no dependa de acertar el color.
        if (esCamino(r.x, r.y) || esCamino(r.x + r.width, r.y)
         || esCamino(r.x, r.y + r.height) || esCamino(r.x + r.width, r.y + r.height)) {
            return false;
        }

        //Solo se mira la celda del CENTRO de la pisada, no todas las que toca.
        //Exigiendo todas quedaba pisable apenas el 14% de la pantalla: el
        //cesped esta lleno de matitas sueltas de pocos pixeles, y pedir que un
        //bloque entero estuviera limpio partia el prado en islas diminutas.
        //Mirando solo el centro queda el 33%, y los estorbos de verdad (los
        //arboles, el bambu, las matas de flores) siguen bloqueando igual
        //porque ocupan muchas celdas seguidas.
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

    //Sobre que piedra esta parado: 1 mariposa, 2 conejo, 3 serpiente, y 0 si
    //no esta sobre ninguna. Solo cuentan las que ya estan abiertas, asi que
    //no se puede entrar a un nivel al que todavia no se llega caminando.
    //Se mira el CENTRO de los pies, no la caja entera, para que valga con que
    //el personaje este parado encima y no tenga que caber justo en el circulo.
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

    //Donde hay que poner al jugador para que quede DE PIE sobre una piedra.
    //Se usa al volver de ganar un nivel, para que aparezca en la piedra que
    //acaba de pasar y siga desde ahi hacia la siguiente.
    //La cuenta deshace lo de siempre: la caja va en el centro del sprite
    //(+50) y los pies quedan DESFASE_PIES mas abajo.
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
    }
}
