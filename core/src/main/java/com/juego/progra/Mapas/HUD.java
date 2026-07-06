package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUD {
    //Corazones
    private Texture corazonLleno;
    private Texture corazonVacio;
    //Mariposas
    private Texture[] mariposasGris;
    private Texture[] mariposasColor;
    //Panel
    private Stage stage;
    private Table tablaPanel;
    private Image[] mariposas;
    private Group grupoMariposas; // Nuestro nuevo contenedor de control absoluto

    public HUD() {
        corazonLleno = new Texture("CorazonLleno.png");
        corazonVacio = new Texture("CorazonVacio.png");
        stage = new Stage(new ScreenViewport());

        Table tablaPanel = new Table();
        tablaPanel.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("panelPrimero.png"))));
        tablaPanel.setSize(450, 150);
        tablaPanel.setPosition(475, 2);
        stage.addActor(tablaPanel);

        grupoMariposas = new Group();
        grupoMariposas.setPosition(475, 2);
        stage.addActor(grupoMariposas);

        mariposasGris = new Texture[]{
            new Texture("azulGris.png"),
            new Texture("amarilloGris.png"),
            new Texture("rosaGris.png")
        };
        mariposasColor = new Texture[]{
            new Texture("azul1.PNG"),
            new Texture("amarillo1.PNG"),
            new Texture("rosa1.PNG")
        };

        mariposas = new Image[3];
        int[] posX = {65, 182, 278};
        int[] posY = {17, 18, 3};
        int[] size = {75, 75, 95};

        for (int i = 0; i < 3; i++) {
            mariposas[i] = new Image(mariposasGris[i]);
            mariposas[i].setBounds(posX[i], posY[i], size[i], size[i]);
            grupoMariposas.addActor(mariposas[i]);
        }
    }

    //Para dibujar los corazones
    public void dibujarCorazones (SpriteBatch batch, int vidasAct){
        int totalVidas = 5;

        for (int i = 0; i < totalVidas; i++) {
            if(i < vidasAct){
                batch.draw(corazonLleno, 1050+(i*60),690,60,60);
            }else{
                batch.draw(corazonVacio, 1050+(i*60),690,60,60);
            }
        }
    }

    //Este metodo se llama es en MapaNiveles oBTENERoBJETO(), no en el main
    private int[] recogidas = {0, 0, 0};
    private int[] total = {6, 8, 13};

    public void CambiarMariposa(int indice) {
        if (indice >= 0 && indice < 3) {
            recogidas[indice]++; // Sumamos una más a este color

            // Solo pintamos si el contador actual llegó al total de ese color
            if (recogidas[indice] >= total[indice]) {
                mariposas[indice].setDrawable(new TextureRegionDrawable(mariposasColor[indice]));
            }
        }
    }

    public void render(){
        stage.act();
        stage.draw();
    }
}
