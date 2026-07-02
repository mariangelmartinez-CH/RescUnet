package com.juego.progra.Mapas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;

public class MapasNiveles {
    private ArrayList<Objetos> Listaobjetos;
    private Texture arbusto;
    private Texture arbustoFlores;
    private Texture fondo;
    private Texture valla,invernadero;

    //Constructor
    public MapasNiveles() {
        fondo = new Texture("Fondo.png");
        valla = new Texture("Valla.png");
        invernadero = new Texture("Invernadero.png");
        Listaobjetos = new ArrayList<>();
        arbusto = new Texture("Arbusto.png");
        arbustoFlores = new Texture("ArbustoFlores.png");

        cargarArchivo("nivelUno.txt");
    }

    public void cargarArchivo (String archivo){

        if (!Gdx.files.internal(archivo).exists()){
            System.out.println("Archivo de Mapa no encontrado");
            return;
        }else{
            String contenidoArchivo = Gdx.files.internal(archivo).readString();
            String[] lineas = contenidoArchivo.split("\n");
            for(String linea : lineas){
                if(linea.trim().isEmpty() || linea.trim().startsWith("#")) continue; // Agregamos .trim() aquí también

                String[] datos = linea.split(",");
                String tipoObj = datos[0].trim();

                int x = Integer.parseInt(datos[1].trim());
                int y = Integer.parseInt(datos[2].trim());

                Texture imagen= null;
                switch (tipoObj.toLowerCase()){
                    case "arbusto":
                        imagen = arbusto;
                        break;
                    case "arbusto_flores":
                        imagen = arbustoFlores;
                        break;
                }
                if(imagen!=null){
                    Objetos ObjTemp = new Objetos(imagen,x,y,70,70,tipoObj);
                    Listaobjetos.add(ObjTemp);
                }

            }

        }
    }

    public boolean CaminarCelda(Rectangle rectanguloPersonaje){
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj =  Listaobjetos.get(i);
            if (obj.destructible && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
                return false;
            }
        }
        return true;
    }

    public void obtenerObjeto (Rectangle rectanguloPersonaje){
        for (int i = Listaobjetos.size() - 1; i >= 0; i--) {
            Objetos obj = Listaobjetos.get(i);
            if (obj.recogible && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
                Listaobjetos.remove(i);
                break;
            }
        }
    }
    public void dibujar (SpriteBatch batch){
        batch.draw(fondo,0,0,1408,768);
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            obj.dibujar(batch);
        }
        batch.draw(valla,0,0,1408,768);
        batch.draw(invernadero,454,257,510,260);
    }

    public void dipose() {
        arbusto.dispose();
        arbustoFlores.dispose();
    }
}
