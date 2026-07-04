package com.juego.progra.Mapas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Jugador;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;

public class MapasNiveles {
    private ArrayList<Objetos> Listaobjetos;
    private float tiempo=0;
    private Texture arbusto;
    private Texture arbustoFlores;
    private Texture fondo;
    private Texture vallaInferior,vallaSuperior;
    private Texture invernaderoTecho, invernaderoPiso;
    private Texture fotosMariposa[][];
    private int faseMariposa = 0;

    //VARIBLES LIMITES QUE NECESITO PARA CAMINAR_CELDA
    private static final float LIMITE_IZQ = 125;
    private static final float LIMITE_DER = 1279;
    private static final float LIMITE_ABAJO = 140;
    private static final float LIMITE_ARRIBA = 615;
    //AL FINAL SI LE HICE RECTANGULO AL INVERNADERO PERO UNO DENTRO DE OTRO
    private static final Rectangle invernaderoCaja = new Rectangle(610, 320, 189, 177);
    private static final Rectangle invernaderoCajaTecho = new Rectangle(667, 477, 75, 40); // 40 = 517 - 477

    //Constructor
    public MapasNiveles() {
        fondo = new Texture("Fondo.png");
        vallaInferior = new Texture("VallaInferior.png");
        vallaSuperior = new Texture("VallaSuperior.png");
        invernaderoTecho = new Texture("InvernaderoTecho.png");
        invernaderoPiso = new Texture("InvernaderoPiso.png");
        Listaobjetos = new ArrayList<>();
        arbusto = new Texture("Arbusto.png");
        arbustoFlores = new Texture("ArbustoFlores.png");

        //Matriz Mariposas
        fotosMariposa = new Texture[3][3];
        String[] colores={"azul","amarillo","rosa"};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fotosMariposa[i][j] = new Texture(colores[i]+(j+1)+".PNG");
            }
        }

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

                Objetos objTemp = null;
                switch (tipoObj){
                    case "arbusto":
                        objTemp = new Objetos(arbusto,x,y,70,70,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "arbusto_flores":
                        objTemp = new Objetos(arbustoFlores,x,y,70,70,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "mariposa":
                        int color = Integer.parseInt(datos[3].trim());
                        objTemp = new Objetos(fotosMariposa[color],x,y,60,60,tipoObj,color);
                        Listaobjetos.add(objTemp);
                        break;
                }

            }

        }
    }

    public boolean CaminarCelda(Rectangle rectanguloPersonaje){
        // LIMITES DEL MUNDO, CALCLA LA ARENA DENTRO DE LA REJA
        if (rectanguloPersonaje.x < LIMITE_IZQ ||
            rectanguloPersonaje.x + rectanguloPersonaje.width > LIMITE_DER ||
            rectanguloPersonaje.y < LIMITE_ABAJO ||
            rectanguloPersonaje.y + rectanguloPersonaje.height > LIMITE_ARRIBA) {
            return false;
        }

        // INVERNADERO
        if (rectanguloPersonaje.overlaps(invernaderoCaja) || rectanguloPersonaje.overlaps(invernaderoCajaTecho)) {
            return false;
        }

        //LO DE LOS ARBUSTOS QUE YA ESTABA
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if (obj.destructible && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
                return false;
            }
        }
        return true;
    }
    public void CambioFase(){
        boolean existeColor = false;
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if(obj.tipo.equals("mariposa") && obj.colorMariposa==faseMariposa){
                existeColor=true;
                break;
            }
        }
        if(!existeColor && faseMariposa<2){
            faseMariposa++;
            System.out.println("Clase de Mariposa recolectada");
        }
    }
    public void obtenerObjeto (Rectangle rectanguloPersonaje){
        for (int i = Listaobjetos.size() - 1; i >= 0; i--) {
            Objetos obj = Listaobjetos.get(i);
            if (obj.recogible && obj.colorMariposa==faseMariposa && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
                Listaobjetos.remove(i);
                CambioFase();
            }
        }
    }

    //Método hecho para no repetir codigo en el metodo dibujar
    public void dibujarObjetos (SpriteBatch batch, Objetos obj){
        if (obj.tipo.equals("mariposa")) {
            if (obj.colorMariposa == faseMariposa) {
                obj.dibujarM(batch, tiempo);
            }
        } else {
            obj.dibujar(batch);
        }
    }
    public void dibujar (SpriteBatch batch, Jugador jugador){
        batch.draw(fondo,0,0,1408,768);
        batch.draw(vallaSuperior,0,0,1408,768);
        batch.draw(invernaderoPiso,454,257,510,260);
        tiempo += Gdx.graphics.getDeltaTime();
        for (Objetos obj : Listaobjetos) {
            if(obj.cajaObstaculo.y > jugador.getY()){
                dibujarObjetos(batch,obj);
            }
        }
        jugador.dibujar(batch);
        for (Objetos obj : Listaobjetos) {
            if (obj.cajaObstaculo.y <= jugador.getY()) {
                dibujarObjetos(batch, obj);
            }
        }
        batch.draw(vallaInferior,0,0,1408,768);
        batch.draw(invernaderoTecho,454,257,510,260);
    }

    public void dipose() {
        arbusto.dispose();
        arbustoFlores.dispose();
        vallaSuperior.dispose();
        vallaInferior.dispose();
        invernaderoTecho.dispose();
        invernaderoPiso.dispose();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fotosMariposa[i][j].dispose();
            }
        }
    }
}
