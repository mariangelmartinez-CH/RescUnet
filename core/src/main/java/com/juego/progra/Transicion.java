package com.juego.progra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.juego.progra.Personajes.Jugador;
import com.juego.progra.Personajes.Personaje;

//La entrada y la salida de un nivel, en tres pasos:
//  1. POSE     - el personaje levanta el puño encima de la piedra (solo al entrar)
//  2. CERRANDO - la imagen se va pixelando cada vez mas gordo
//  3. ABRIENDO - con la imagen hecha puros cuadrotes se cambia de sitio, y se
//                va despixelando hasta que se ve nitido lo nuevo
//Mientras la transicion corre nadie se mueve: Main deja de leer las teclas.
//
//NO se tapa con ningun color ni con ninguna imagen: se pixela la escena de
//verdad, con sus propios colores. El truco es de dos pasos:
//   1. se dibuja todo el juego en un FrameBuffer (una textura fuera de pantalla)
//   2. esa textura se encoge a un tamaño chiquito con filtro suave, asi cada
//      pixel chico sale del PROMEDIO de los que tenia alrededor
//   3. y esa miniatura se estira otra vez a pantalla completa con filtro
//      Nearest, que copia el pixel tal cual sin suavizar: de ahi salen los
//      cuadros grandes con el color real de esa zona
//Cuanto mas chica se hace la miniatura, mas gordo se ve el pixelado.
public class Transicion {

    private enum Fase { NADA, POSE, CERRANDO, ABRIENDO }

    private static final float DURACION_POSE = 0.8f;
    private static final float DURACION_CORTINA = 0.6f;

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    //Lo que llega a medir un bloque en el momento mas pixelado. Con 192 la
    //pantalla queda en unos 7x4 cuadros, que ya no se reconoce nada: por eso
    //ahi es donde se cambia de nivel sin que se note el corte.
    private static final int BLOQUE_MAXIMO = 96;

    //En el ultimo tramo, ademas de pixelar, la imagen se va apagando. Hace
    //falta porque por muy gordo que sea el bloque los colores siguen ahi, y el
    //cambio de nivel se notaria como un golpe de color. Los cuadros con sus
    //colores se ven durante casi toda la transicion; esto solo es el remate.
    private static final float INICIO_APAGADO = 0.62f;

    //Cargar un nivel crea un monton de texturas y ESE frame tarda muchisimo.
    //Si la animacion avanzara con ese delta enorme tal cual, se saltaria de
    //golpe toda la parte de abrir y no se veria. Por eso el tiempo avanza a
    //pasos cortos: como mucho lo que dura un frame a 30 por segundo.
    private static final float PASO_MAXIMO = 1f / 30f;

    //Van en FRACCIONES del cuadro, no en pixeles, para que el puño siga
    //cuadrando aunque en el mapa el personaje se dibuje mas grande.
    private static final float FRAC_ALTO_JUGADOR = 92f / 105f;  //lo que mide el muñeco de lado
    private static final float FRAC_PIES_JUGADOR = 11.5f / 105f;//a que altura tiene los pies
    private static final float FRAC_ALTO_PUNO = 0.8196f;  //409 de 500 px
    private static final float FRAC_PIES_PUNO = 0.1120f;  //margen transparente de abajo
    private static final float FRAC_CENTROX_PUNO = 0.48f; //el dibujo no esta centrado del todo

    private Fase fase = Fase.NADA;
    private float tiempo = 0;
    //A donde se va: 1, 2 o 3 es un nivel, y 0 es el mapa principal
    private int nivelDestino = 0;
    private boolean conPose = false;

    //Donde se dibuja el juego mientras dura el efecto, y donde se guarda la
    //version encogida. Los dos son del tamaño de la pantalla; del segundo solo
    //se usa la esquina de abajo a la izquierda, del tamaño que toque.
    private FrameBuffer fboEscena;
    private FrameBuffer fboChico;

    private Texture puno;

    public Transicion() {
        fboEscena = new FrameBuffer(Pixmap.Format.RGBA8888, ANCHO_MUNDO, ALTO_MUNDO, false);
        fboChico  = new FrameBuffer(Pixmap.Format.RGBA8888, ANCHO_MUNDO, ALTO_MUNDO, false);

        //Copia con nombre sin eñe del sprite del puño, porque el original
        //(puño1-removebg-preview.png) puede no cargar segun como se guarde
        //el archivo .java.
        puno = new Texture("punio1.png");
    }

    //Entrar a un nivel desde el mapa: con pose de puño primero
    public void empezar(int nivel) {
        if (fase != Fase.NADA) return;   //ya hay una corriendo
        nivelDestino = nivel;
        conPose = true;
        fase = Fase.POSE;
        tiempo = 0;
    }

    //Volver al mapa despues de ganar el nivel. Aqui NO hay pose: el puño es
    //para entrar. Destino 0 quiere decir "el mapa principal".
    public void empezarVuelta() {
        if (fase != Fase.NADA) return;
        nivelDestino = 0;
        conPose = false;
        fase = Fase.CERRANDO;
        tiempo = 0;
    }

    public boolean estaActiva() { return fase != Fase.NADA; }

    //Si toca dibujar el puño en vez del sprite normal del jugador
    public boolean mostrandoPose() {
        return conPose && (fase == Fase.POSE || fase == Fase.CERRANDO);
    }

    //Si en este frame hay que pasar el juego por el efecto. Durante la pose
    //todavia no: ahi se ve todo nitido.
    public boolean pixelando() {
        return fase == Fase.CERRANDO || fase == Fase.ABRIENDO;
    }

    public int getNivelDestino() { return nivelDestino; }

    //Devuelve true UNA sola vez: justo cuando la imagen llego a su punto mas
    //pixelado. Ese es el momento de cambiar de sitio, porque no se reconoce nada.
    public boolean actualizar(float delta) {
        if (fase == Fase.NADA) return false;
        tiempo += Math.min(delta, PASO_MAXIMO);

        if (fase == Fase.POSE && tiempo >= DURACION_POSE) {
            fase = Fase.CERRANDO;
            tiempo = 0;
        } else if (fase == Fase.CERRANDO && tiempo >= DURACION_CORTINA) {
            fase = Fase.ABRIENDO;
            tiempo = 0;
            return true;
        } else if (fase == Fase.ABRIENDO && tiempo >= DURACION_CORTINA) {
            fase = Fase.NADA;
            tiempo = 0;
        }
        return false;
    }

    //Cuanto mide el bloque ahora mismo. Crece de forma exponencial y no a
    //partes iguales, porque el pixelado se nota "por veces": pasar de 2 a 4 se
    //ve tanto como pasar de 40 a 80.
    //0 = nitido, 1 = lo mas pixelado. Cerrando va de 0 a 1 y abriendo al reves.
    private float avanceActual() {
        float avance;
        if (fase == Fase.CERRANDO) {
            avance = tiempo / DURACION_CORTINA;
        } else {
            avance = 1f - tiempo / DURACION_CORTINA;
        }
        if (avance < 0) avance = 0;
        if (avance > 1) avance = 1;
        return avance;
    }

    private int ladoBloque() {
        int lado = (int) Math.pow(BLOQUE_MAXIMO, avanceActual());
        return lado < 1 ? 1 : lado;
    }

    //Cuanta luz le queda a la imagen: 1 normal, 0 negro del todo
    private float brillo() {
        float avance = avanceActual();
        if (avance <= INICIO_APAGADO) return 1f;
        return 1f - (avance - INICIO_APAGADO) / (1f - INICIO_APAGADO);
    }

    //A partir de aqui TODO lo que dibuje Main se va a la textura de fuera de
    //pantalla en vez de a la pantalla.
    public void empezarCaptura() {
        fboEscena.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    //Y aqui se cierra esa captura y se pinta ya pixelada en la pantalla.
    public void terminarCapturaYDibujar(SpriteBatch batch) {
        fboEscena.end();

        int lado = ladoBloque();
        int anchoChico = Math.max(1, ANCHO_MUNDO / lado);
        int altoChico  = Math.max(1, ALTO_MUNDO / lado);

        //PASO 1: encoger. Se usa Nearest y no Linear a proposito: Linear solo
        //mira 4 pixeles vecinos, asi que al reducir muchisimo no da el promedio
        //de la zona sino un color casi al azar. Nearest coge un pixel de verdad
        //de esa zona, que ademas es lo que le pega a un juego de pixel art.
        //Las texturas de un FrameBuffer salen del reves, por eso el ultimo
        //true, que es voltearla en vertical.
        Texture escena = fboEscena.getColorBufferTexture();
        escena.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        fboChico.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(escena, 0, 0, anchoChico, altoChico,
                   0, 0, ANCHO_MUNDO, ALTO_MUNDO, false, true);
        batch.end();
        fboChico.end();

        //PASO 2: estirar esa miniatura a pantalla completa. Nearest copia el
        //pixel tal cual, sin difuminar, y por eso se ven los cuadros con borde
        //recto en vez de un borron.
        Texture chico = fboChico.getColorBufferTexture();
        chico.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        float b = brillo();
        batch.begin();
        batch.setColor(b, b, b, 1f);
        batch.draw(chico, 0, 0, ANCHO_MUNDO, ALTO_MUNDO,
                   0, 0, anchoChico, altoChico, false, true);
        //Se devuelve el color o sale todo lo demas oscurecido
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    //El puño va donde esta el jugador, pero NO en un cuadro de 105: se agranda
    //hasta que el muñeco mida lo mismo que el del jugador, y se coloca por los
    //pies y por el centro del dibujo, no por la esquina de la imagen.
    public void dibujarPuno(SpriteBatch batch, Jugador jugador, float tamanoJugador) {
        if (!mostrandoPose()) return;

        //Que mide y donde tiene los pies el jugador dibujado a ese tamaño
        float altoMuneco = FRAC_ALTO_JUGADOR * tamanoJugador;
        float pies = jugador.getY() + FRAC_PIES_JUGADOR * tamanoJugador;
        //El centro no se mueve al cambiar el tamaño (ver Personaje.dibujar)
        float centroX = jugador.getX() + Personaje.TAMANO_NORMAL / 2f;

        //Y ahora el puño, para que su muñeco mida ese mismo alto
        float lado = altoMuneco / FRAC_ALTO_PUNO;
        float x = centroX - FRAC_CENTROX_PUNO * lado;
        float y = pies - FRAC_PIES_PUNO * lado;

        batch.draw(puno, x, y, lado, lado);
    }

    public void dispose() {
        if (fboEscena != null) fboEscena.dispose();
        if (fboChico != null) fboChico.dispose();
        if (puno != null) puno.dispose();
    }
}
