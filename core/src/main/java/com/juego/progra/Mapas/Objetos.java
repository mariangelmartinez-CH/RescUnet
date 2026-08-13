package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Rectangle;

public class Objetos {
    public Texture imagen;
    public Texture[] aniMariposas;
    public Rectangle cajaObstaculo;

    //Los estantes tienen mucho margen transparente alrededor del dibujo, asi
    //que su caja de choque es mas chica que la de dibujo. En los demas objetos
    //las dos son la misma.
    public Rectangle cajaColision;
    public String tipo;
    public int colorItem; //0 Azul,  1 Amarillo, 2 Rosa
    public boolean recogible;
    public boolean destructible;
    public boolean indestructible;
    public float tiempoDañado = 0;

    //Constructor para Objetos Estáticos (Arbustos y estantes)
    public Objetos(Texture imagen, int x, int y, int ancho, int largo, String tipo) {
        this.imagen=imagen;
        this.cajaObstaculo = new Rectangle(x,y,ancho,largo);
        this.tipo = tipo;
        this.recogible = false;

        if(tipo.equals("arbusto")){
            this.destructible = true;
            this.indestructible=false;
        }else if (tipo.equals("arbusto_flores") || tipo.equals("estanteV") || tipo.equals("estanteH")){
            this.destructible = false;
            this.indestructible=true;
        }

        //Proporciones medidas sobre los PNG: el mueble no llena su recuadro
        if(tipo.equals("estanteV")){
            this.cajaColision = new Rectangle(x + ancho*0.29f, y + largo*0.08f,
                                              ancho*0.39f, largo*0.86f);
        }else if(tipo.equals("estanteH")){
            this.cajaColision = new Rectangle(x + ancho*0.10f, y + largo*0.21f,
                                              ancho*0.84f, largo*0.64f);
        }else{
            this.cajaColision = this.cajaObstaculo;
        }
    }

    //Constructor para Items
    public Objetos(Texture[] animacion, int x, int y, int ancho, int largo, String tipo, int color) {
        this.aniMariposas=animacion;
        this.cajaObstaculo = new Rectangle(x,y,ancho,largo);
        this.tipo = tipo;
        this.colorItem = color;

        this.destructible =  false;
        this.recogible = true;
        this.cajaColision = this.cajaObstaculo;
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
