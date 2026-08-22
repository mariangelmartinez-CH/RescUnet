package com.juego.progra;

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

public class informacion {

    private static final int ANCHO_MUNDO = 1408;
    private static final int ALTO_MUNDO = 768;

    private Main main;
    private Stage stage;
    private SpriteBatch batch;

    private Texture p1;
    private Texture p2;
    private Texture p3;
    private ImageButton botonPrincipio;
    private ImageButton.ImageButtonStyle estiloSiguiente;
    private ImageButton.ImageButtonStyle estiloJugar;
    private float stateTime;
    private Texture[] rector; // Arreglo de texturas
    private int pantallaAct = 1;

    //C
    public informacion(Main main) {
        this.main = main;
        this.stage = new Stage(new FitViewport(ANCHO_MUNDO, ALTO_MUNDO));
        this.batch = new SpriteBatch();

        historiaPrincipio();
        botonesPrincipio();
        setActivo(false);

    }

    public void setActivo(boolean activo) {
        botonPrincipio.setTouchable(activo ? Touchable.enabled : Touchable.disabled);
    }

    public void historiaPrincipio() {
        p1 = new Texture("fondoP1.png");
        p2 = new Texture("fondoP2.png");
        p3 = new Texture("fondoP3.png");

        // Rector
        rector = new Texture[4];
        for (int i = 0; i < 4; i++) {
            rector[i] = new Texture("rector" + (i + 1) + ".png");
        }
    }

    public void botonesPrincipio() {
        estiloSiguiente = new ImageButton.ImageButtonStyle();
        estiloSiguiente.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1.png")));
        estiloSiguiente.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1_Ab.png")));

        estiloJugar = new ImageButton.ImageButtonStyle();
        estiloJugar.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonP2.png")));
        estiloJugar.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonP2_Ab.png")));

        botonPrincipio = new ImageButton(estiloSiguiente);
        botonPrincipio.setPosition(1080, 1);
        botonPrincipio.setSize(380, 200);

        botonPrincipio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pantallaAct == 1) {
                    pantallaAct = 2;
                } else if (pantallaAct == 2) {
                    pantallaAct = 3;
                    botonPrincipio.setStyle(estiloJugar);
                } else if (pantallaAct == 3) {
                    main.empezarSalidaHistoria();
                }
            }
        });
        stage.addActor(botonPrincipio);
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
        batch.draw(rector[fase], 42, 40,205,210);
    }

    public void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();

        Texture fondo;

        if (pantallaAct == 1) {
            fondo = p1;
        } else if(pantallaAct==2) {
            fondo = p2;
        }else{
            fondo =p3;
        }

        batch.draw(
            fondo,
            0,
            0,
            1408,
            768
        );

        dibujarRector(batch, stateTime);

        batch.end();

        stage.draw();
    }
    public void reiniciar() {
        pantallaAct = 1;
        if (botonPrincipio != null) {
            botonPrincipio.setStyle(estiloSiguiente);
        }
    }
    public void actualizar(float delta) {
        stateTime += delta;
        stage.act(delta);
    }
    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (p1 != null) p1.dispose();
        if (p2 != null) p2.dispose();
        if (p3 != null) p3.dispose();
        if (rector != null) {
            for (Texture t : rector) {
                if (t != null) t.dispose();
            }
        }
        stage.dispose();
        batch.dispose();
    }
}
