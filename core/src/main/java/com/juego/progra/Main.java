package com.juego.progra;
import com.juego.progra.Mapas.MapasNiveles;
import com.juego.progra.Personajes.Enemigo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Jugador;

public class Main extends Game {
    private SpriteBatch batch;
    private MapasNiveles mapa;
    private Jugador jugador;
    private Enemigo enemigo;

    @Override
    public void create(){
        batch = new SpriteBatch();
        mapa = new MapasNiveles();
        jugador = new Jugador(384, 458);
        enemigo = new Enemigo(400, 300);
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // La pantalla tiene Y invertido (el 0 arriba, el fondo abajo)
                // Por eso, para que coincida con tu juego, usamos (Gdx.graphics.getHeight() - screenY)
                int yCorregida = Gdx.graphics.getHeight() - screenY;

                System.out.println("arbusto, " + screenX + ", " + yCorregida);
                return true;
            }
        });
    }

    public void render(){
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float delta = Gdx.graphics.getDeltaTime();//-> MOVIMIENTO FLUIDO

       /* jugador.actualizar(mapa, delta);//-> LLAMA AL METODO ACTUALIZAR Y SE LE PASA EL MAPA PARA COLISIONES DESPUES Y DELTA PARA EL MOVIMIENTO FLUIDO
        enemigo.actualizar(mapa, delta);
        // COLISION JUGADOR-ENEMIGO
        if (Math.abs(jugador.getX() - enemigo.getX()) < 50 &&
            Math.abs(jugador.getY() - enemigo.getY()) < 50) {
            jugador.recibirGolpe();
        }*/

        batch.begin();// ABRE EL PINCEL , A PARTIR DE AQUI SE PUEDE DIBUJAR
        mapa.dibujar(batch);    // DIBUJA EL FONDO
        //jugador.dibujar(batch); // DIBUJA EL JUGADOR ENCIMA
        //enemigo.dibujar(batch);
        batch.end(); //CIERRA EL PINCEL, ENVIA TODO_ A LA PANTALLA

    }
    @Override // SE EJECUTA UNA SOLA VEZ AL CERRAR EL JUEGO LIBERA MEMORIA DE TODO_
    public void dispose() {
        batch.dispose();
        jugador.dispose();
    }
}
