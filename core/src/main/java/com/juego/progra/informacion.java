package com.juego.progra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;

public class informacion {

    private Main main;
    private Stage stage;
    private SpriteBatch batch;

    private Texture p1;
    private Texture p2;
    private ImageButton botonPrincipio;
    private float stateTime;
    private Texture[] rector; // Arreglo de texturas
    private int pantallaAct = 1;

    //C
    public informacion(Main main) {
        this.main = main;
        this.stage = new Stage();
        this.batch = new SpriteBatch();

        historiaPrincipio();
        botonesPrincipio();

    }

    public void historiaPrincipio() {
        // Fondos
        p1 = new Texture("fondoP1.png");
        p2 = new Texture("fondoP2.png");

        // Rector (tamaño 4, índices 0, 1, 2, 3)
        rector = new Texture[4];
        for (int i = 0; i < 4; i++) {
            rector[i] = new Texture("rector" + (i + 1) + ".png");
        }
    }

    public void botonesPrincipio() {
        ImageButton.ImageButtonStyle estilo = new ImageButton.ImageButtonStyle();
        estilo.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1.png")));
        estilo.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonP1_Ab.png")));

        botonPrincipio = new ImageButton(estilo);
        botonPrincipio.setPosition(1080, 1);
        botonPrincipio.setSize(380, 200);

        botonPrincipio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BOTON", "¡SE HIZO CLIC!");
                if (pantallaAct == 1) {
                    pantallaAct = 2;
                } else if (pantallaAct == 2) {
                    ImageButton.ImageButtonStyle nuevoEstilo = new ImageButton.ImageButtonStyle();
                    nuevoEstilo.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonP2.png")));
                    nuevoEstilo.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonP2.png")));
                    botonPrincipio.setStyle(nuevoEstilo);
                }
            }
        });
        stage.addActor(botonPrincipio);
        Gdx.app.log("INFO", "Boton agregado al Stage");
    }

    private void dibujarRector(SpriteBatch batch, float tiempo) {
        float ciclo = tiempo % 0.80f;
        int fase;
        if (ciclo < 0.2f) {
            fase = 0;
        } else if (ciclo < 0.4f) {
            fase = 1;
        } else if (ciclo < 0.6f) {
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
        } else {
            fondo = p2;
        }

        batch.draw(
            fondo,      // imagen
            0,       // posición X
            0,       // posición Y
            1408,    // ancho
            768      // alto
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
}
