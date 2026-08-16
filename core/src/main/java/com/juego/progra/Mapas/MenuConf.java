package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuConf {
    private Table tabla;
    private Stage stage;


    public MenuConf(Stage stage) {
        tabla = new Table();
        tabla.setFillParent(true);
        this.stage=stage;


        tabla.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("fondoMenu.png"))));
        stage.addActor(tabla);
        tabla.setVisible(false);
    }
    public void cambiarVisibilidad() {
        tabla.setVisible(!tabla.isVisible());
    }

}
