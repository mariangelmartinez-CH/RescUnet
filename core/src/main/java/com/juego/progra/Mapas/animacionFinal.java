package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    private Texture textFondo;
    private ImageButton botonFinal;
    private float stateTime;
    private Texture[] rector; // Arreglo de texturas
    private int pantallaAct = 1;

    //C
    public animacionFinal(Main main) {
        this.main = main;
        this.stage = new Stage(new FitViewport(ANCHO_MUNDO, ALTO_MUNDO));
        this.batch = new SpriteBatch();

        historiaFinal();
        botoneFinal();

    }

    public void historiaFinal() {
        textFondo=new Texture("fondoFinal.png");

        // Rector
        rector = new Texture[4];
        for (int i = 0; i < 4; i++) {
            rector[i] = new Texture("rector" + (i + 1) + ".png");
        }
    }

    public void botoneFinal() {
        ImageButton.ImageButtonStyle estilo = new ImageButton.ImageButtonStyle();
        estilo.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonFinal.png")));
        estilo.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonFinal_Ab.png")));

        botonFinal = new ImageButton(estilo);
        botonFinal.setPosition(1080, 1);
        botonFinal.setSize(380, 200);

        botonFinal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.menuGameOver();
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
        batch.draw(rector[fase], 42, 40,205,210);
    }

    public void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();

        Texture fondo;
        fondo= textFondo;
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

    public void actualizar(float delta) {
        stateTime += delta;
        stage.act(delta);
    }
    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        textFondo.dispose();
        if (rector != null) {
            for (Texture t : rector) {
                if (t != null) t.dispose();
            }
        }
        stage.dispose();
        batch.dispose();
    }
}
