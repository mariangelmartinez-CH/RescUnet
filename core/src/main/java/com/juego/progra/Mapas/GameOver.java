package com.juego.progra.Mapas;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.juego.progra.Main;

public class GameOver {
    private Group grupoGameOver;
    private Texture texGameOver;
    private ImageButton btnReinicar;

    private Stage stage;
    private Main juegoP;
    private HUD hudRef;
    private Sound sonido;

    public GameOver(Stage stage, Main juegoP,HUD hudRef, Sound sonidoBoton) {
        this.stage=stage;
        this.juegoP=juegoP;
        this.hudRef=hudRef;
        this.sonido=sonidoBoton;
        grupoGameOver= new Group();

        int anchoMenu=700;
        int altoMenu=650;

        texGameOver= new Texture("gameOver.png");
        Image pantallaGameOver=new Image(texGameOver);
        pantallaGameOver.setSize(anchoMenu, altoMenu);
        pantallaGameOver.setScaling(Scaling.stretch);

        grupoGameOver.addActor(pantallaGameOver);
        grupoGameOver.setPosition((1550 - anchoMenu) / 2f, (780 - altoMenu) / 2f);

        btnReinicar = crearBoton("botonReintentar.png","botonReintentar_Ab.png");
        btnReinicar.setPosition(185, 70);
        btnReinicar.setSize(300, 290);
        grupoGameOver.addActor(btnReinicar);

        btnReinicar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (sonidoBoton != null) {
                    sonidoBoton.play();
                }
                ocultar();
                juegoP.menuGameOver();
            }
        });
        stage.addActor(grupoGameOver);
        grupoGameOver.setVisible(false);

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

    public void mostrar() {
        grupoGameOver.setVisible(true);
    }

    public void ocultar() {
        grupoGameOver.setVisible(false);
    }

    public boolean isVisible() {
        return grupoGameOver.isVisible();
    }

    public void dispose() {
        if (texGameOver != null) {
            texGameOver.dispose();
        }

        if (btnReinicar != null) {
            ImageButton.ImageButtonStyle estilo = btnReinicar.getStyle();
            if (estilo.imageUp != null && estilo.imageUp instanceof TextureRegionDrawable) {
                ((TextureRegionDrawable) estilo.imageUp).getRegion().getTexture().dispose();
            }
            if (estilo.imageDown != null && estilo.imageDown instanceof TextureRegionDrawable) {
                ((TextureRegionDrawable) estilo.imageDown).getRegion().getTexture().dispose();
            }
        }
    }


}
