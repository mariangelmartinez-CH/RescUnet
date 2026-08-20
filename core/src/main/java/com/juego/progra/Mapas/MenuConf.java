package com.juego.progra.Mapas;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.juego.progra.Main;

public class MenuConf {
    private Group grupo;
    private Table tabla;
    private Stage stage;
    private Main juegoP;
    private HUD HudRef;
    private Image fondo;
    private Sound sonidoBoton;


    public MenuConf(Stage stage,Main juegoP, HUD HudRef, Sound sonidoBoton) {
        this.stage = stage;
        grupo = new Group();
        this.juegoP = juegoP;
        this.HudRef=HudRef;
        this.sonidoBoton = sonidoBoton;

        float anchoMenu = 700;
        float altoMenu = 650;

        Texture texturaFondo = new Texture("fondoMenu.png");
        fondo = new Image(texturaFondo);
        fondo.setSize(anchoMenu, altoMenu);
        fondo.setScaling(Scaling.stretch);

        grupo.addActor(fondo);
        grupo.setPosition((1550 - anchoMenu) / 2f, (780 - altoMenu) / 2f);


        ImageButton brnReiniciar = crearBoton("btnReiniciar.png", "btnReiniciar_Ab.png");
        brnReiniciar.setPosition(95, 275);
        brnReiniciar.setSize(150, 150);
        grupo.addActor(brnReiniciar);

        brnReiniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sonidoBoton.play();
                juegoP.empezarReinicioNivel();
                HudRef.resetearHUD();
                grupo.setVisible(false);
            }
        });

        //Boton volumen y que alterne entre estados
        final Texture texturaVolUp = new Texture("btnVol.png");
        final Texture texturaVolDown = new Texture("btnVol_Ab.png");

        final Texture texturaSilencioUp = new Texture("btnVolSilencio.png");
        final Texture texturaSilencioDown = new Texture("btnVolSilencio_Ab.png");

        ImageButton.ImageButtonStyle estiloVol = new ImageButton.ImageButtonStyle();
        estiloVol.imageUp = new TextureRegionDrawable(new TextureRegion(texturaVolUp));
        estiloVol.imageDown = new TextureRegionDrawable(new TextureRegion(texturaVolDown));

        ImageButton btnVol = new ImageButton(estiloVol);
        btnVol.setPosition(270, 275);
        btnVol.setSize(150, 150);
        grupo.addActor(btnVol);

        final int[] estadoVolumen = {0};

        btnVol.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sonidoBoton.play();

                estadoVolumen[0] = (estadoVolumen[0] == 0) ? 1 : 0;
                juegoP.alternarSilencioMusica();

                if (estadoVolumen[0] == 1) {
                    estiloVol.imageUp = new TextureRegionDrawable(new TextureRegion(texturaSilencioUp));
                    estiloVol.imageDown = new TextureRegionDrawable(new TextureRegion(texturaSilencioDown));
                } else {
                    estiloVol.imageUp = new TextureRegionDrawable(new TextureRegion(texturaVolUp));
                    estiloVol.imageDown = new TextureRegionDrawable(new TextureRegion(texturaVolDown));
                }
            }
        });

        ImageButton btnHome = crearBoton("btnHome.png", "btnHome_Ab.png");
        btnHome.setPosition(445, 275);
        btnHome.setSize(150, 150);
        grupo.addActor(btnHome);

        btnHome.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sonidoBoton.play();
                juegoP.empezarVueltaPausa();
                grupo.setVisible(false);
            }
        });

        ImageButton btnContinuar = crearBoton("btnContinuar.png","btnContinuar_Ab.png");
        btnContinuar.setPosition(170, 55);
        btnContinuar.setSize(350, 250);
        grupo.addActor(btnContinuar);

        btnContinuar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sonidoBoton.play();
                grupo.setVisible(false);
            }
        });

        stage.addActor(grupo);
        grupo.setVisible(false);
    }

    private ImageButton crearBoton(String imgUp, String imgDown) {
        ImageButton.ImageButtonStyle estilo = new ImageButton.ImageButtonStyle();
        estilo.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(imgUp)));
        estilo.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(imgDown)));

        ImageButton btn = new ImageButton(estilo);

        // Lógica para cambiar el cursor
        btn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                // Cambia el cursor a mano
                com.badlogic.gdx.Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                // Regresa el cursor a la flecha normal
                com.badlogic.gdx.Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
            }
        });

        return btn;
    }

        public void cambiarVisibilidad() {
            grupo.setVisible(!grupo.isVisible());
        }

        public boolean confVisible() {
            return grupo.isVisible();
        }
    }


