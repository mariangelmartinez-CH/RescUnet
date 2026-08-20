package com.juego.progra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.juego.progra.Personajes.Jugador;
import com.juego.progra.Personajes.Personaje;


public class Transicion {

    private enum Fase { NADA, POSE, CERRANDO, ABRIENDO }
    private static final float DURACION_POSE = 0.8f;
    private static final float DURACION_CORTINA = 0.6f;
    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;
    private static final int BLOQUE_MAXIMO = 96;
    private static final float INICIO_APAGADO = 0.62f;

    private static final float PASO_MAXIMO = 1f / 30f;
    private static final float FRAC_ALTO_JUGADOR = 92f / 105f;  //lo que mide el muñeco de lado
    private static final float FRAC_PIES_JUGADOR = 11.5f / 105f;//a que altura tiene los pies
    private static final float FRAC_ALTO_PUNO_NINO = 0.8196f;
    private static final float FRAC_PIES_PUNO_NINO = 0.1120f;
    private static final float FRAC_CENTROX_PUNO_NINO = 0.48f;

    private static final float FRAC_ALTO_PUNO_NINA = 0.76f;
    private static final float FRAC_PIES_PUNO_NINA = 0.132f;
    private static final float FRAC_CENTROX_PUNO_NINA = 0.508f;

    private Fase fase = Fase.NADA;
    private float tiempo = 0;
    private int nivelDestino = 0;
    private boolean conPose = false;
    private FrameBuffer fboEscena;
    private FrameBuffer fboChico;

    private Texture punoNiño, punoNiña;

    public Transicion() {
        fboEscena = new FrameBuffer(Pixmap.Format.RGBA8888, ANCHO_MUNDO, ALTO_MUNDO, false);
        fboChico  = new FrameBuffer(Pixmap.Format.RGBA8888, ANCHO_MUNDO, ALTO_MUNDO, false);
        punoNiño = new Texture("punio1.png");
        punoNiña = new Texture("punio2.png");
    }

    public void empezar(int nivel) {
        if (fase != Fase.NADA) return;   //ya hay una corriendo
        nivelDestino = nivel;
        conPose = true;
        fase = Fase.POSE;
        tiempo = 0;
    }

    public void empezarVuelta() {
        empezarPantalla(0);
    }
    public void empezarPantalla(int destino) {
        if (fase != Fase.NADA) return;
        nivelDestino = destino;
        conPose = false;
        fase = Fase.CERRANDO;
        tiempo = 0;
    }

    public boolean estaActiva() { return fase != Fase.NADA; }


    public boolean mostrandoPose() {
        return conPose && (fase == Fase.POSE || fase == Fase.CERRANDO);
    }


    public boolean pixelando() {
        return fase == Fase.CERRANDO || fase == Fase.ABRIENDO;
    }

    public int getNivelDestino() { return nivelDestino; }

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


    private float brillo() {
        float avance = avanceActual();
        if (avance <= INICIO_APAGADO) return 1f;
        return 1f - (avance - INICIO_APAGADO) / (1f - INICIO_APAGADO);
    }


    public void empezarCaptura() {
        fboEscena.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    public void terminarCapturaYDibujar(SpriteBatch batch) {
        fboEscena.end();

        int lado = ladoBloque();
        int anchoChico = Math.max(1, ANCHO_MUNDO / lado);
        int altoChico  = Math.max(1, ALTO_MUNDO / lado);

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

    public void dibujarPuno(SpriteBatch batch, Jugador jugador, float tamanoJugador, int personaje) {
        if (!mostrandoPose()) return;

        Texture puno = (personaje == 1) ? punoNiña : punoNiño;
        float fracAlto = (personaje == 1) ? FRAC_ALTO_PUNO_NINA : FRAC_ALTO_PUNO_NINO;
        float fracPies = (personaje == 1) ? FRAC_PIES_PUNO_NINA : FRAC_PIES_PUNO_NINO;
        float fracCentroX = (personaje == 1) ? FRAC_CENTROX_PUNO_NINA : FRAC_CENTROX_PUNO_NINO;

        float altoMuneco = FRAC_ALTO_JUGADOR * tamanoJugador;
        float pies = jugador.getY() + FRAC_PIES_JUGADOR * tamanoJugador;
        float centroX = jugador.getX() + Personaje.TAMANO_NORMAL / 2f;

        float lado = altoMuneco / fracAlto;
        float x = centroX - fracCentroX * lado;
        float y = pies - fracPies * lado;

        batch.draw(puno, x, y, lado, lado);
    }

    public void dispose() {
        if (fboEscena != null) fboEscena.dispose();
        if (fboChico != null) fboChico.dispose();
        if (punoNiño != null) punoNiño.dispose();
        if (punoNiña != null) punoNiña.dispose();
    }
}
