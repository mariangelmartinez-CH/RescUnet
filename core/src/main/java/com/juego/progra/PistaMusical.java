package com.juego.progra;

import com.badlogic.gdx.audio.Music;

public class PistaMusical {

    private static final float DURACION_FUNDIDO = 0.6f;

    private static final float FACTOR_AGACHADO = 0.35f;
    private static final float DURACION_RECUPERO_CLIC = 0.5f;

    private final Music musica;
    private final float volumenNormal;

    private float tiempoFundido = -1;
    private boolean fundidoBajando = false;
    private float tiempoAgache = -1;

    private boolean silenciada = false;

    public PistaMusical(Music musica, float volumenNormal) {
        this.musica = musica;
        this.musica.setLooping(true);
        this.musica.setVolume(volumenNormal);
        this.volumenNormal = volumenNormal;
    }

    public void setSilenciada(boolean silenciada) {
        this.silenciada = silenciada;
        musica.setVolume(silenciada ? 0f : volumenSinSilencio());
    }

    private float volumenSinSilencio() {
        if (tiempoFundido >= 0) {
            float t = Math.min(1f, tiempoFundido / DURACION_FUNDIDO);
            return fundidoBajando ? volumenNormal * (1f - t) : volumenNormal * t;
        }
        if (tiempoAgache >= 0) {
            float t = Math.min(1f, tiempoAgache / DURACION_RECUPERO_CLIC);
            float bajo = volumenNormal * FACTOR_AGACHADO;
            return bajo + (volumenNormal - bajo) * t;
        }
        return volumenNormal;
    }

    public void empezar() {
        tiempoFundido = -1;
        tiempoAgache = -1;
        musica.setVolume(silenciada ? 0f : volumenNormal);
        musica.play();
    }

    public void detener() {
        tiempoFundido = -1;
        tiempoAgache = -1;
        musica.stop();
    }

    public void fundidoSalida() {
        fundidoBajando = true;
        tiempoFundido = 0;
        tiempoAgache = -1;
    }

    public void fundidoEntrada() {
        fundidoBajando = false;
        tiempoFundido = 0;
        tiempoAgache = -1;
        musica.setVolume(0f);
        musica.play();
    }

    public void agachar() {
        if (tiempoFundido >= 0) return;
        tiempoAgache = 0;
        if (!silenciada) {
            musica.setVolume(volumenNormal * FACTOR_AGACHADO);
        }
    }

    public void actualizar(float delta) {
        if (tiempoFundido >= 0) {
            tiempoFundido += delta;
            float t = Math.min(1f, tiempoFundido / DURACION_FUNDIDO);
            if (!silenciada) {
                musica.setVolume(fundidoBajando
                        ? volumenNormal * (1f - t)
                        : volumenNormal * t);
            }
            if (t >= 1f) {
                tiempoFundido = -1;
                if (fundidoBajando) musica.stop();
            }
            return;
        }

        if (tiempoAgache >= 0) {
            tiempoAgache += delta;
            float t = Math.min(1f, tiempoAgache / DURACION_RECUPERO_CLIC);
            if (!silenciada) {
                float bajo = volumenNormal * FACTOR_AGACHADO;
                musica.setVolume(bajo + (volumenNormal - bajo) * t);
            }
            if (t >= 1f) tiempoAgache = -1;
        }
    }

    public boolean estaSonando() {
        return musica.isPlaying();
    }

    public void dispose() {
        musica.dispose();
    }
}
