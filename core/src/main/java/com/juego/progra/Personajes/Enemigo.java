package com.juego.progra.Personajes;
import com.juego.progra.Mapas.MapasNiveles;

public class Enemigo extends Personaje {

      public Enemigo(float x, float y) {
          super(x, y, "personaje.png.png");//POR AHORA USAMOS LA MISMA IMAGEN DEL JUGADOR PARA LLENAR EL CONSTRUCTOR LUEGO LE PONEOS UNA DIFERENTE

      }

      private float cambio=0;
      private int direccion =0; //ESTO SE MANEJARA CON EL CASE

      @Override
      public void actualizar(MapasNiveles mapa, float delta) {
          /*cambio+=delta;

          if (cambio >= 2) { //CAMBIA CADA DOS SEGUNDOS DE DIRECCION
              direccion = MathUtils.random(0,3);
              cambio=0;
          }
          switch (direccion){
              case 0: x += 100 * delta;
              break;
              case 1: x -= 100 * delta;
              break;
              case 2: y += 100 * delta;
              break;
              case 3: y -= 100 * delta;
              break;

          }
          x = Math.max(0, Math.min(x, 800 - 50));
          y = Math.max(0, Math.min(y, 480 - 50));*/
      }
  }
