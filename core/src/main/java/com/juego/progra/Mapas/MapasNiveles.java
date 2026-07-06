package com.juego.progra.Mapas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Enemigo;
import com.juego.progra.Personajes.Jugador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapasNiveles {

    //Clase Interna Creada para poder meter todos los objetos del mapa en un solo arraylist
    //Sin importar si es jugador, enemigo, o objeto todos son dibujables
    private class Dibujable{
        public Object obj;
        public float cY;

        //Constructor

        public Dibujable(Object obj,float cY) {
            this.obj = obj;
            this.cY= cY;
        }
    }

    //Variables
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
    private static final float LIMITE_ARRIBA = 590;
    //AL FINAL SI LE HICE RECTANGULO AL INVERNADERO PERO UNO DENTRO DE OTRO
    private static final Rectangle invernaderoCaja = new Rectangle(610, 320, 189, 177);
    private static final Rectangle invernaderoCajaTecho = new Rectangle(667, 477, 75, 40); // 40 = 517 - 477
    //VARIABLES Y CONSTANTE PARA ROMPER ARBUSTOS
    public static final float TIEMPO_PARA_ROMPER = 3f;
    private Texture arbustoDano1, arbustoDano2;
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
        arbustoDano1 = new Texture("rompible_daño2.png");
        arbustoDano2 = new Texture("rompible_daño1.png");

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
                if(linea.trim().isEmpty() || linea.trim().startsWith("#"))
                    continue;

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
            if ((obj.destructible || obj.indestructible) && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
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
    public void dibujar (SpriteBatch batch, Jugador jugador, List<Enemigo> enemigos){
        batch.draw(fondo,0,0,1408,768);
        batch.draw(vallaSuperior,0,0,1408,768);
        batch.draw(invernaderoPiso,454,257,510,260);

        tiempo += Gdx.graphics.getDeltaTime();
        ArrayList<Dibujable> arrayDibujo = new ArrayList<>();

        for(Objetos obj : Listaobjetos ){
            arrayDibujo.add(new Dibujable(obj, obj.cajaObstaculo.y));
        }
        for (Enemigo ene : enemigos){
            arrayDibujo.add(new Dibujable(ene, ene.getY()));
        }
        arrayDibujo.add(new Dibujable(jugador, jugador.getY()));

        Collections.sort(arrayDibujo, (a,b) -> Float.compare(b.cY,a.cY));
        for(Dibujable cosa : arrayDibujo){
            if(cosa.obj instanceof Objetos){
                dibujarObjetos(batch,(Objetos)cosa.obj);
            }else if (cosa.obj instanceof Enemigo){
                Enemigo enem = (Enemigo) cosa.obj;
                enem.dibujar(batch);
            }else if (cosa.obj instanceof Jugador){
                Jugador juga = (Jugador) cosa.obj;
                juga.dibujar(batch);
            }
        }
        batch.draw(vallaInferior,0,0,1408,768);
        batch.draw(invernaderoTecho,454,257,510,260);
    }

    public Objetos obtenerArbustoRompible(Rectangle rectanguloPersonaje) {
        for (Objetos obj : Listaobjetos) {
            if (obj.destructible && rectanguloPersonaje.overlaps(obj.cajaObstaculo)) {
                return obj;
            }
        }
        return null;
    }

    public void actualizarDañoArbusto(Objetos obj) {
        if (obj.tiempoDañado >= TIEMPO_PARA_ROMPER * (2f/3f)) {
            obj.imagen = arbustoDano2;
        } else if (obj.tiempoDañado >= TIEMPO_PARA_ROMPER * (1f/3f)) {
            obj.imagen = arbustoDano1;
        }
    }



    public void romperArbusto(Objetos obj) {
        Listaobjetos.remove(obj);
    }

    public Objetos obtenerArbustoEnDireccion(Rectangle celda, float xJugador, float yJugador, float dx, float dy) {
        Objetos masCercano = null;
        float distanciaMinima = Float.MAX_VALUE;
        for (Objetos obj : Listaobjetos) {
            if (!obj.destructible || !celda.overlaps(obj.cajaObstaculo)) continue;

            float centroObjX = obj.cajaObstaculo.x + obj.cajaObstaculo.width / 2f;
            float centroObjY = obj.cajaObstaculo.y + obj.cajaObstaculo.height / 2f;

            float diffX = centroObjX - xJugador;
            float diffY = centroObjY - yJugador;

            if (dx != 0) {
                if (dx > 0 && diffX <= 0) continue;
                if (dx < 0 && diffX >= 0) continue;
                if (Math.abs(diffY) > 35) continue;
            } else {
                if (dy > 0 && diffY <= 0) continue;
                if (dy < 0 && diffY >= 0) continue;
                if (Math.abs(diffX) > 35) continue;
            }

            float distancia = diffX * diffX + diffY * diffY;
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercano = obj;
            }
        }
        return masCercano;
    }
        //ESTO NO FUNCIONA AUN :C
    public void dispararLinea(float centroX, float centroY, String direccion) {
        float dx = 0, dy = 0;
        switch (direccion) {
            case "derecha": dx = 1; break;
            case "izquierda": dx = -1; break;
            case "arriba": dy = 1; break;
            case "abajo": dy = -1; break;
        }

        float paso = 5f;
        float alcanceMax = 70 * 5;

        for (float recorrido = paso; recorrido <= alcanceMax; recorrido += paso) {
            float cx = centroX + dx * recorrido;
            float cy = centroY + dy * recorrido;

            Rectangle celdaDeteccion;
            if (dx != 0) {
                celdaDeteccion = new Rectangle(cx - 10, centroY - 10, 20, 20);
            } else {
                celdaDeteccion = new Rectangle(centroX - 10, cy - 10, 20, 20);
            }

            Objetos bloqueante = obtenerArbustoEnDireccion(celdaDeteccion, centroX, centroY, dx, dy);
            if (bloqueante != null) {
                bloqueante.tiempoDañado += TIEMPO_PARA_ROMPER / 3f;
                actualizarDañoArbusto(bloqueante);
                if (bloqueante.tiempoDañado >= TIEMPO_PARA_ROMPER) {
                    romperArbusto(bloqueante);
                }
                return;
            }

            if (!CaminarCelda(celdaDeteccion)) {
                return;
            }
        }

        for (int i = 1; i <= 5; i++) {
            float distancia = 70f * i;
            float px = centroX + dx * distancia;
            float py = centroY + dy * distancia;
            Rectangle celdaCrear = new Rectangle(px - 35, py - 35, 70, 70);

            if (CaminarCelda(celdaCrear)) {
                Listaobjetos.add(new Objetos(arbusto, (int) (px - 35), (int) (py - 35), 70, 70, "arbusto"));
            } else {
                break;
            }
        }
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
