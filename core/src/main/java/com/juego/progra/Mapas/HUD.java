package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUD {
    // Corazones
    private Texture corazonLleno;
    private Texture corazonVacio;

    // Texturas pre-cargadas por nivel (Evita fugas de memoria)
    private Texture[] grisNivel1, colorNivel1;
    private Texture[] grisNivel2, colorNivel2;

    private final int[] posX_M = {65, 182, 278};
    private final int[] posY_M = {17, 18, 3};
    private final int[] size_M = {75, 75, 95};

    private final int[] posX_C = {65, 183, 288};
    private final int[] posY_C = {11, 10, 11};
    private final int[] size_C = {75, 78, 78};


    private Texture[] ItemsGris = new Texture[3];
    private Texture[] ItemsColor = new Texture[3];

    // Panel
    private Stage stage;
    private Table tablaPanel;
    private Image[] items;
    private Group grupoItems;
    private Texture[] texturaPaneles;

    public HUD() {
        corazonLleno = new Texture("CorazonLleno.png");
        corazonVacio = new Texture("CorazonVacio.png");
        stage = new Stage(new ScreenViewport());

        texturaPaneles = new Texture[]{
            new Texture("panelPrimero.png"),
            new Texture("panelSegundo.png"),
            new Texture("panelTercero.png")
        };

        tablaPanel = new Table();
        tablaPanel.setBackground(new TextureRegionDrawable(new TextureRegion(texturaPaneles[0])));
        tablaPanel.setSize(450, 150);
        tablaPanel.setPosition(475, 2);
        stage.addActor(tablaPanel);

        grupoItems = new Group();
        grupoItems.setPosition(475, 2);
        stage.addActor(grupoItems);

        //Mariposas
        grisNivel1 = new Texture[]{
            new Texture("azulGris.png"),
            new Texture("amarilloGris.png"),
            new Texture("rosaGris.png")
        };
        colorNivel1 = new Texture[]{
            new Texture("azul1.PNG"),
            new Texture("amarillo1.PNG"),
            new Texture("rosa1.PNG")
        };

        //Conejos
        grisNivel2 = new Texture[]{
            new Texture("negroGris.png"),
            new Texture("marronGris.png"),
            new Texture("beigeGris.png")
        };
        colorNivel2 = new Texture[]{
            new Texture("negro1.PNG"),
            new Texture("marron1.PNG"),
            new Texture("beige1.PNG")
        };

        // Por defecto arrancamos con el Nivel 1
        for (int i = 0; i < 3; i++) {
            ItemsGris[i] = grisNivel1[i];
            ItemsColor[i] = colorNivel1[i];
        }

        items = new Image[3];

        for (int i = 0; i < 3; i++) {
            items[i] = new Image(ItemsGris[i]);
            items[i].setBounds(posX_M[i], posY_M[i], size_M[i], size_M[i]);
            grupoItems.addActor(items[i]);
        }
    }

    // Para dibujar los corazones
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

    private int[] recogidas = {0, 0, 0};
    private int[] total = {6, 8, 13};

    public void CambiarItem(int indice) {
        if (indice >= 0 && indice < 3) {
            recogidas[indice]++;

            if (recogidas[indice] >= total[indice]) {
                items[indice].setDrawable(new TextureRegionDrawable(ItemsColor[indice]));

                if (indice + 1 < texturaPaneles.length) {
                    tablaPanel.setBackground(new TextureRegionDrawable(new TextureRegion(texturaPaneles[indice + 1])));
                }
            }
        }
    }

    public void actualizarHud(int nivel){
        recogidas = new int[]{0, 0, 0};
        tablaPanel.setBackground(new TextureRegionDrawable(new TextureRegion(texturaPaneles[0])));

        // Elegimos qué configuración usar
        int[] posYActual = (nivel == 1) ? posY_M : posY_C;
        int[] sizeActual = (nivel == 1) ? size_M : size_C;
        int[] posXActual = (nivel == 1) ? posX_M : posX_C;

        if (nivel == 1) {
            for (int i = 0; i < 3; i++) {
                ItemsGris[i] = grisNivel1[i];
                ItemsColor[i] = colorNivel1[i];
            }
        } else if (nivel == 2) {
            for (int i = 0; i < 3; i++) {
                ItemsGris[i] = grisNivel2[i];
                ItemsColor[i] = colorNivel2[i];
            }
        }

        for (int i = 0; i < 3; i++) {
            items[i].setDrawable(new TextureRegionDrawable(ItemsGris[i]));
            items[i].setBounds(posXActual[i], posYActual[i], sizeActual[i], sizeActual[i]);
        }
    }

    public void render(){
        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        corazonLleno.dispose();
        corazonVacio.dispose();
        for(Texture t : texturaPaneles) t.dispose();
        for(Texture t : grisNivel1) t.dispose();
        for(Texture t : colorNivel1) t.dispose();
        for(Texture t : grisNivel2) t.dispose();
        for(Texture t : colorNivel2) t.dispose();
    }
}
