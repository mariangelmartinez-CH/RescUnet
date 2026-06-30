package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.juego.progra.Mapas.MapasNiveles;

public abstract class Personaje {
    protected float x,y;
    protected Texture imagen;

    //Constructor
    public Personaje(float x, float y, String rutaImagen) {
        this.x = x;
        this.y = y;
        this.imagen = new Texture(rutaImagen);
    }

    public abstract void actualizar(MapasNiveles mapa, float delta);

    public void dibujar (SpriteBatch batch) {
        batch.draw(imagen, x, y, 250, 250); // Lo dibuja en su x, y con tamaño 50x50
    }
    public void dispose() {
        if (imagen != null) {
            imagen.dispose();
        }
    }
}

