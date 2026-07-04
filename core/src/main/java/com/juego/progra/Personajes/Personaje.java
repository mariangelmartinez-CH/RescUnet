package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.juego.progra.Mapas.MapasNiveles;
import com.badlogic.gdx.math.Rectangle;

public abstract class Personaje {
    protected float x,y;
    protected Texture imagen;
    protected float cajaTamañito = 15;

    //Constructor
    public Personaje(float x, float y, String rutaImagen) {
        this.x = x;
        this.y = y;
        this.imagen = new Texture(rutaImagen);
    }

    public abstract void actualizar(MapasNiveles mapa, float delta);

    public void dibujar (SpriteBatch batch) {
        batch.draw(imagen, x, y, 125, 125); // Lo dibuja en su x, y con tamaño 50x50
    }
    public void dispose() {
        if (imagen != null) {
            imagen.dispose();
        }
    }

    public Rectangle caja(float px, float py) {
        float offset = (100 - cajaTamañito) / 2f;
        return new Rectangle(px + offset, py + offset, cajaTamañito, cajaTamañito);
    }
    public void setCajaTamañito(float cajaTamañito) {
        this.cajaTamañito = cajaTamañito;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}


