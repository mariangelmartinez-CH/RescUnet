package com.juego.progra.Personajes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.juego.progra.Mapas.MapasNiveles;
import com.badlogic.gdx.math.Rectangle;

public abstract class Personaje {
    protected float x,y;
    protected Texture imagen;
    protected float cajaTamañito = 10;

    //Constructor
    public Personaje(float x, float y, String rutaImagen) {
        this.x = x;
        this.y = y;
        this.imagen = new Texture(rutaImagen);
    }

    public abstract void actualizar(MapasNiveles mapa, float delta);

    public static final float TAMANO_NORMAL = 105;

    public void dibujar (SpriteBatch batch) {
        dibujar(batch, TAMANO_NORMAL);
    }

    //Lo mismo pero de otro tamaño, para el mapa principal, donde el personaje
    //se ve mas grande. Se corre la mitad de lo que crece hacia la izquierda
    //para que el CENTRO siga cayendo en el mismo sitio; el borde de abajo se
    //queda donde estaba, asi que los pies casi no se mueven.
    public void dibujar (SpriteBatch batch, float tamano) {
        float corrimiento = (tamano - TAMANO_NORMAL) / 2f;
        batch.draw(imagen, x - corrimiento, y, tamano, tamano);
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


