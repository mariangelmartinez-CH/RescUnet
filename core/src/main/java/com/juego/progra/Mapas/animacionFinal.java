package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.juego.progra.Main;

public class animacionFinal {
    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    private Main main;
    private Stage stage;
    private SpriteBatch batch;

    private Texture llaves;
    private Texture central;
    private Texture textFondo;
    private ImageButton botonSiguiente;
    private ImageButton botonFinal;
    private float stateTime;
    private Texture[] rector;
    private int pantallaAct = 1;

    public animacionFinal(Main main) {
        this.main = main;
        this.stage = new Stage(new FitViewport(ANCHO_MUNDO, ALTO_MUNDO));
        this.batch = new SpriteBatch();

        historiaFinal();
        botonesFinal();
        setActivo(false);
    }

    public void historiaFinal() {
        llaves = new Texture("llavesabriendo.png");
        central = new Texture("central.png");
        textFondo = new Texture("fondoFinal.png");

        rector = new Texture[4];
        for (int i = 0; i < 4; i++) {
            rector[i] = new Texture("rector" + (i + 1) + ".png");
        }
    }

    public void botonesFinal() {
        ImageButton.ImageButtonStyle estiloSiguiente = new ImageButton.ImageButtonStyle();
        estiloSiguiente.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1.png")));
        estiloSiguiente.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1_Ab.png")));

        botonSiguiente = new ImageButton(estiloSiguiente);
        botonSiguiente.setPosition(1080, 1);
        botonSiguiente.setSize(380, 200);
        botonSiguiente.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pantallaAct == 1) {
                    pantallaAct = 2;
                } else if (pantallaAct == 2) {
                    pantallaAct = 3;
                }
            }
        });
        stage.addActor(botonSiguiente);

        ImageButton.ImageButtonStyle estiloFinal = new ImageButton.ImageButtonStyle();
        estiloFinal.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonFinal.png")));
        estiloFinal.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonFinal_Ab.png")));

        botonFinal = new ImageButton(estiloFinal);
        botonFinal.setPosition(1080, 1);
        botonFinal.setSize(380, 200);
        botonFinal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pantallaAct != 3) return;
                main.empezarSalidaFinal();
            }
        });
        stage.addActor(botonFinal);
    }

    private void dibujarRector(SpriteBatch batch, float tiempo) {
        float ciclo = tiempo % 1.60f;
        int fase;
        if (ciclo < 0.4f) {
            fase = 0;
        } else if (ciclo < 0.8f) {
            fase = 1;
        } else if (ciclo < 1.2f) {
            fase = 2;
        } else {
            fase = 3;
        }
        batch.draw(rector[fase], 42, 40, 205, 210);
    }

    public void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();

        if (pantallaAct == 1) {
            batch.draw(llaves, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        } else if (pantallaAct == 2) {
            batch.draw(central, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        } else {
            batch.draw(textFondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
            dibujarRector(batch, stateTime);
        }

        batch.end();

        botonSiguiente.setVisible(pantallaAct == 1 || pantallaAct == 2);
        botonFinal.setVisible(pantallaAct == 3);

        stage.draw();
    }

    public void actualizar(float delta) {
        stateTime += delta;
        stage.act(delta);
    }

    public void reiniciar() {
        pantallaAct = 1;
        stateTime = 0;
    }

    public void setActivo(boolean activo) {
        Touchable t = activo ? Touchable.enabled : Touchable.disabled;
        botonSiguiente.setTouchable(t);
        botonFinal.setTouchable(t);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (llaves != null) llaves.dispose();
        if (central != null) central.dispose();
        if (textFondo != null) textFondo.dispose();
        if (rector != null) {
            for (Texture t : rector) {
                if (t != null) t.dispose();
            }
        }
        stage.dispose();
        batch.dispose();
    }
}
