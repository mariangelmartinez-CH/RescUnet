package com.juego.progra;

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

public class AvisosNiveles {
    private Group grupoAvisos;
    private ImageButton btnContinuar;
    private Stage stage;
    private int nivel;

    public AvisosNiveles(Stage stage , int nivel) {
        this.stage=stage;
        this.nivel = nivel;

        grupoAvisos=new Group();
        int anchoMenu=700;
        int altoMenu=650;
        Texture textAviso= new Texture("aviso"+nivel+".png");
        Image aviso = new Image(textAviso);
        aviso.setSize(anchoMenu, altoMenu);
        aviso.setScaling(Scaling.stretch);

        grupoAvisos.addActor(aviso);
        grupoAvisos.setPosition((1550 - anchoMenu) / 2f, (780 - altoMenu) / 2f);

        ImageButton btnContinuar = crearBoton("btnContinuar.png","btnContinuar_Ab.png");
        btnContinuar.setPosition(95, 275);
        btnContinuar.setSize(150, 150);
        grupoAvisos.addActor(btnContinuar);

        btnContinuar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                grupoAvisos.setVisible(false);
            }
        });

        stage.addActor(grupoAvisos);
        grupoAvisos.setVisible(false);

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
        grupoAvisos.setVisible(true);
    }
    public void ocultar() {
        grupoAvisos.setVisible(false);
    }
}
