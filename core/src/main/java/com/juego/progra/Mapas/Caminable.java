package com.juego.progra.Mapas;

import com.badlogic.gdx.math.Rectangle;

//lo implementan tanto los niveles como el mapa principal, para que el jugador pueda moverse en los dos sin repetir el codigo del movimiento.
public interface Caminable {
    boolean CaminarCelda(Rectangle rectanguloPersonaje);
}
