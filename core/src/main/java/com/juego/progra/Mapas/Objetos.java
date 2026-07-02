package com.juego.progra.Mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Rectangle;

public class Objetos {
    public Texture imagen;
    public Rectangle cajaObstaculo;
    public String tipo;
    public boolean recogible;
    public boolean destructible;

    public Objetos(Texture imagen, int x, int y, int ancho, int largo, String tipo) {
        this.imagen=imagen;
        this.cajaObstaculo = new Rectangle(x,y,ancho,largo);
        this.tipo = tipo;

        this.destructible = tipo.equals("arbusto") || tipo.equals("arbustoFlores");
        this.recogible = tipo.equals("mariposa");

    }
    public void dibujar(SpriteBatch batch) {
        batch.draw(imagen, cajaObstaculo.x, cajaObstaculo.y, cajaObstaculo.width, cajaObstaculo.height);
    }
}
