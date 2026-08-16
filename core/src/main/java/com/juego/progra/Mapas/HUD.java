package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class HUD {
    private MenuConf menu;
    // Corazones
    private Texture corazonLleno;
    private Texture corazonVacio;

    //Botones
    private ImageButton btnPausa;
    private Texture texturePausa;
    private Texture texturePausaAb;

    //Texturas de los Objetos por nivel
    private Texture[] grisNivel1, colorNivel1;
    private Texture[] grisNivel2, colorNivel2;
    private Texture[] grisNivel3, colorNivel3;

    private final int[] posX_M = {553, 663, 762};
    private final int[] posY_M = {33, 35, 23};
    private final int[] size_M = {75, 75, 93};

    private final int[] posX_C = {553, 663, 770};
    private final int[] posY_C = {30, 30, 30};
    private final int[] size_C = {75, 78, 78};

    private final int[] posX_F = {563, 673, 780};
    private final int[] posY_F = {45, 45, 45};
    private final int[] size_F = {56, 56, 56};


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
        stage = new Stage(new FitViewport(1550, 780));
        menu = new MenuConf(stage);

        texturaPaneles = new Texture[]{
            new Texture("panelPrimero.png"),
            new Texture("panelSegundo.png"),
            new Texture("panelTercero.png")
        };

        // Botones
        texturePausa = new Texture("btnPausa.png");
        texturePausaAb = new Texture("btnPausaAb.png");

        ImageButton.ImageButtonStyle estiloBtn = new ImageButton.ImageButtonStyle();
        estiloBtn.imageUp = new TextureRegionDrawable(new TextureRegion(texturePausa));
        estiloBtn.imageDown = new TextureRegionDrawable(new TextureRegion(texturePausaAb));

        btnPausa = new ImageButton(estiloBtn);
        btnPausa.setPosition(20, 630);
        btnPausa.setSize(150, 150);

        btnPausa.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.cambiarVisibilidad();
            }
        });
        stage.addActor(btnPausa);

        tablaPanel = new Table();
        tablaPanel.setBackground(new TextureRegionDrawable(new TextureRegion(texturaPaneles[0])));
        tablaPanel.setSize(450, 170);
        tablaPanel.setPosition(950, 2);
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

        //Flores
        grisNivel3 = new Texture[]{
          new Texture("blancaGris.png"),
          new Texture("moradaGris.png"),
          new Texture("amarillaGris.png"),
        };
        colorNivel3= new Texture[]{
          new Texture("blanca1.png"),
          new Texture ("morada1.png"),
          new Texture("amarilla1.png"),
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
        int[] posXActual = {0, 0, 0};
        int[] posYActual= {0, 0, 0};
        int[] sizeActual= {0, 0, 0};

        if(nivel==1){
            posXActual = posX_M;
            posYActual =posY_M;
            sizeActual=size_M;

        }else if(nivel==2){
            posXActual = posX_C;
            posYActual =posY_C;
            sizeActual=size_C;

        }else if (nivel==3){
            posXActual = posX_F;
            posYActual =posY_F;
            sizeActual=size_F;
        }


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
        } else if (nivel==3){
            for (int i = 0; i < 3; i++) {
                ItemsGris[i]= grisNivel3[i];
                ItemsColor [i]= colorNivel3[i];
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
        if (texturePausa != null) texturePausa.dispose();
        if (texturePausaAb != null) texturePausaAb.dispose();
        for(Texture t : texturaPaneles) t.dispose();
        for(Texture t : grisNivel1) t.dispose();
        for(Texture t : colorNivel1) t.dispose();
        for(Texture t : grisNivel2) t.dispose();
        for(Texture t : colorNivel2) t.dispose();

    }

    public Stage getStage() {
        return stage;
    }
}
