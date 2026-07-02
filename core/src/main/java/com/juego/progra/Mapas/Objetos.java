package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Rectangle;

public class Objetos {
    public Texture imagen;
    public Texture[] aniMariposas;
    public Rectangle cajaObstaculo;
    public String tipo;
    public int colorMariposa; //0 Azul,  1 Amarillo, 2 Rosa
    public boolean recogible;
    public boolean destructible;

    //Constructor para Objetos Estáticos (no se mueven) Arbustos
    public Objetos(Texture imagen, int x, int y, int ancho, int largo, String tipo) {
        this.imagen=imagen;
        this.cajaObstaculo = new Rectangle(x,y,ancho,largo);
        this.tipo = tipo;

        this.destructible = true;
        this.recogible = false;
    }

    //Constructor para Mariposas
    public Objetos(Texture[] animacion, int x, int y, int ancho, int largo, String tipo, int color) {
        this.aniMariposas=animacion;
        this.cajaObstaculo = new Rectangle(x,y,ancho,largo);
        this.tipo = tipo;
        this.colorMariposa = color;

        this.destructible =  false;
        this.recogible = true;

    }

    //Objetos Fijos
    public void dibujar(SpriteBatch batch) {
        batch.draw(imagen, cajaObstaculo.x, cajaObstaculo.y, cajaObstaculo.width, cajaObstaculo.height);
    }

    public void dibujarM(SpriteBatch batch,float tiempo) {
            float ciclo = tiempo % 0.75f;
            int fase;
            if(ciclo < 0.3f){
                fase=0;
            }else if (ciclo <0.4f){
                fase=1;
            }else{
                fase=2;
            }
            batch.draw(aniMariposas[fase],cajaObstaculo.x,cajaObstaculo.y,cajaObstaculo.width,cajaObstaculo.height);
    }
}
