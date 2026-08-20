package com.juego.progra.Mapas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juego.progra.Personajes.Enemigo;
import com.juego.progra.Personajes.Jugador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapasNiveles implements Caminable {


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
    private int faseItem = 0;
    private boolean nivelCompl = false;
    private Texture arbusto;
    private Texture arbustoFlores;
    private Texture estanteV;
    private Texture estanteH;
    private Texture fondo;
    private Texture vallaInferior,vallaSuperior,vallaSuperior_Ab;
    private Texture fotosItem[][];
    private Texture fotosLlave[];

    public int getFaseItem() {
        return faseItem;
    }


    private float LIMITE_IZQ, LIMITE_DER, LIMITE_ABAJO, LIMITE_ARRIBA;


    private ArrayList<Rectangle> zonasBloqueadas = new ArrayList<>();


    private ArrayList<Rectangle> zonasPermitidas = new ArrayList<>();


    private Rectangle salidaArriba;


    private float spawnX, spawnY;

    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
    public boolean estaCompletado() { return nivelCompl; }


    public void completarDePrueba() { nivelCompl = true; }

    private static final float MARGEN_SALIDA = 15;

    public boolean saliendoPorArriba(Rectangle caja) {
        if (!nivelCompl || salidaArriba == null) return false;
        float topeDelCamino = salidaArriba.y + salidaArriba.height;
        return caja.y + caja.height >= topeDelCamino - MARGEN_SALIDA;
    }


    private ArrayList<float[]> zonasOvaladas = new ArrayList<>();

    public static final float TIEMPO_PARA_ROMPER = 3f;

    public static final float ALCANCE_GOLPE = 28f;


    public static final float GROSOR_MIRA = 4f;
    private Texture arbustoDanado;
    private float anchoArbustoNivel = 70, altoArbustoNivel = 70;

    private Sound sonidoRomper;

    //Constructor
    public MapasNiveles() {
        Listaobjetos = new ArrayList<>();
        sonidoRomper = Gdx.audio.newSound(Gdx.files.internal("romper_arbusto.wav"));
    }

    public void cargarArchivo (String archivo,int nivel){

        if (!Gdx.files.internal(archivo).exists()){
            System.out.println("Archivo de Mapa no encontrado");
            return;
        }else{

            //Arbustos
            int anchoArbs=80;
            int tamArbusto=80;
            //Items
            int anchItem=60;
            int altItem = 60;

            if(nivel==2) {
                tamArbusto = 80;
                anchoArbs=100;

                anchItem= 75;
                altItem=75;

            }if(nivel==3){
                anchItem=70;
                altItem= 70;
            }

            //Se guarda para que disparar la linea use el tamaño real del nivel
            anchoArbustoNivel = anchoArbs;
            altoArbustoNivel = tamArbusto;

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
                        objTemp = new Objetos(arbusto,x,y,anchoArbs,tamArbusto,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "arbusto_flores":
                        objTemp = new Objetos(arbustoFlores,x,y,anchoArbs,tamArbusto,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "estanteV":
                        objTemp = new Objetos(estanteV,x,y,160,180,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "estanteH":
                        objTemp = new Objetos(estanteH,x,y,160,140,tipoObj);
                        Listaobjetos.add(objTemp);
                        break;
                    case "mariposa":
                    case "conejo":
                    case "flor":
                        int color = Integer.parseInt(datos[3].trim());
                        objTemp = new Objetos(fotosItem[color],x,y,anchItem,altItem,tipoObj,color);
                        Listaobjetos.add(objTemp);
                        break;
                    case "llave":
                        int colorL = Integer.parseInt(datos[3].trim());
                        objTemp = new Objetos(fotosLlave,x,y,70,90,tipoObj,colorL);
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

            boolean enSalida = false;
            for (int i = 0; i < zonasPermitidas.size(); i++) {
                if (zonasPermitidas.get(i).contains(rectanguloPersonaje)) {
                    enSalida = true;
                    break;
                }
            }


            if (!enSalida && nivelCompl && salidaArriba != null
                    && salidaArriba.contains(rectanguloPersonaje)) {
                enSalida = true;
            }

            if (!enSalida) return false;
        }


        for (int i = 0; i < zonasBloqueadas.size(); i++) {
            if (rectanguloPersonaje.overlaps(zonasBloqueadas.get(i))) {
                return false;
            }
        }


        for (int i = 0; i < zonasOvaladas.size(); i++) {
            if (tocaOvalo(rectanguloPersonaje, zonasOvaladas.get(i))) {
                return false;
            }
        }


        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if ((obj.destructible || obj.indestructible) && rectanguloPersonaje.overlaps(obj.cajaColision)){
                return false;
            }
        }
        return true;
    }

    private static final float DESFASE_PIES = 35;


    private boolean tocaOvalo(Rectangle r, float[] ovalo) {
        float centroX = ovalo[0], centroY = ovalo[1];
        float radioX  = ovalo[2], radioY  = ovalo[3];

        float pieY = r.y - DESFASE_PIES;

        float puntoX = Math.max(r.x, Math.min(centroX, r.x + r.width));
        float puntoY = Math.max(pieY, Math.min(centroY, pieY + r.height));

        float dx = (puntoX - centroX) / radioX;
        float dy = (puntoY - centroY) / radioY;
        return dx * dx + dy * dy <= 1;
    }

    public void CambioFase(){
        boolean existeColor = false;
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if((obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor") || obj.tipo.equals("llave")) && obj.colorItem == faseItem){                existeColor=true;
                break;
            }
        }
        if(!existeColor) {
            if(faseItem<2){
                faseItem++;
                System.out.println("Fase "+faseItem+" recolectada");
            }else if(faseItem==2){
                faseItem=3;
            } else if (faseItem==3) {
                nivelCompl=true;
                System.out.println("LLlave recolectada. Nivel Completado");
            }
        }

    }
    private HUD hud;

    public void setHud(HUD hud) {
        this.hud = hud;
    }

    public void obtenerObjeto (Rectangle rectanguloPersonaje){
        for (int i = Listaobjetos.size() - 1; i >= 0; i--) {
            Objetos obj = Listaobjetos.get(i);
            if (obj.recogible && obj.colorItem ==faseItem && rectanguloPersonaje.overlaps(obj.cajaObstaculo)){
                int indice = obj.colorItem;
                Listaobjetos.remove(i);
                hud.CambiarItem(indice);
                CambioFase();
            }
        }
    }


    //metodo para no repetir codigo en dibuj
    public void dibujarObjetos (SpriteBatch batch, Objetos obj){
        if (obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor") || obj.tipo.equals("llave")) {
            if (obj.colorItem == faseItem) {

                float tiempoAnimacion = tiempo;
                if (nivel == 3) {
                    tiempoAnimacion = tiempo * 0.5f; // Más lento en el nivel 3
                }
                obj.dibujarM(batch, tiempoAnimacion);
            }
        } else {
            obj.dibujar(batch);
        }
    }
    public void dibujar (SpriteBatch batch, Jugador jugador, List<Enemigo> enemigos, int nivel,float delta){
        batch.draw(fondo,0,0,1408,768);
        Texture vallaDibujar = nivelCompl ? vallaSuperior_Ab : vallaSuperior;
        batch.draw(vallaDibujar, 0, 0, 1408, 768);
        tiempo += delta;
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
            }else if (cosa.obj instanceof Texture && (nivel == 1 || nivel == 2)){
                batch.draw((Texture) cosa.obj,465,257,490,260);
            }
        }
        batch.draw(vallaInferior,0,0,1408,768);

    }

    //cambia el sprite del arbusto cuando ya lleva la mitad del daño
    public void actualizarDanoArbusto(Objetos obj) {
        if (obj.tiempoDañado >= TIEMPO_PARA_ROMPER * 0.5f) {
            obj.imagen = arbustoDanado;
        }
    }

    public void romperArbusto(Objetos obj) {
        Listaobjetos.remove(obj);
    }

    public Objetos obtenerArbustoEnDireccion(float centroX, float centroY, float dx, float dy) {
        Objetos elegido = null;
        float menorDistancia = Float.MAX_VALUE;

        for (Objetos obj : Listaobjetos) {
            if (!obj.destructible) continue;


            Rectangle caja = obj.cajaColision;
            float distancia;

            if (dx != 0) {

                if (centroY + GROSOR_MIRA < caja.y) continue;
                if (centroY - GROSOR_MIRA > caja.y + caja.height) continue;
                distancia = (dx > 0) ? (caja.x - centroX)
                                     : (centroX - (caja.x + caja.width));
            } else {
                if (centroX + GROSOR_MIRA < caja.x) continue;
                if (centroX - GROSOR_MIRA > caja.x + caja.width) continue;
                distancia = (dy > 0) ? (caja.y - centroY)
                                     : (centroY - (caja.y + caja.height));
            }


            if (distancia < -2 || distancia > ALCANCE_GOLPE) continue;

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                elegido = obj;
            }
        }
        return elegido;
    }

    //golpea el arbusto que tenga delante si hay alguno al alcance
    public void dispararLinea(float centroX, float centroY, String direccion) {
        float dx = 0, dy = 0;
        switch (direccion) {
            case "derecha": dx = 1; break;
            case "izquierda": dx = -1; break;
            case "arriba": dy = 1; break;
            case "abajo": dy = -1; break;
        }

        Objetos arbusto = obtenerArbustoEnDireccion(centroX, centroY, dx, dy);
        if (arbusto == null) return;

        sonidoRomper.play();
        arbusto.tiempoDañado += TIEMPO_PARA_ROMPER / 2f;
        actualizarDanoArbusto(arbusto);
        if (arbusto.tiempoDañado >= TIEMPO_PARA_ROMPER) {
            romperArbusto(arbusto);
        }
    }


    private void configurarLimites(int nivel){
        zonasBloqueadas.clear();
        zonasPermitidas.clear();
        zonasOvaladas.clear();
        salidaArriba = null;



        if (nivel == 1) {

            LIMITE_IZQ = 114;
            LIMITE_DER = 1292;
            LIMITE_ABAJO = 119;
            LIMITE_ARRIBA = 613;


            zonasPermitidas.add(new Rectangle(643, 45, 92, 100));  //entrada de abajo (siempre abierta)
            salidaArriba = new Rectangle(643, 560, 92, 190);       //salida de arriba (solo al completar)

            spawnX = 641;
            spawnY = 25;
        } else if (nivel == 3) {

            LIMITE_IZQ = 166;
            LIMITE_DER = 1219;

            LIMITE_ABAJO = 164;

            LIMITE_ARRIBA = 692;


            zonasPermitidas.add(new Rectangle(650, 45, 100, 130)); //entrada de abajo (siempre abierta)
            salidaArriba = new Rectangle(650, 645, 100, 110);      //salida de arriba (solo al completar)

            spawnX = 650;  //la caja queda en x 698, el centro del camino
            spawnY = 42;
        } else {

            LIMITE_IZQ = 176;
            LIMITE_DER = 1281;
            LIMITE_ABAJO = 125;
            LIMITE_ARRIBA = 680;


            zonasPermitidas.add(new Rectangle(650, 45, 100, 100)); //entrada de abajo (siempre abierta)
            salidaArriba = new Rectangle(650, 645, 100, 110);      //salida de arriba (solo al completar)

            spawnX = 650;
            spawnY = 42;

            zonasOvaladas.add(new float[]{708, 400, 86, 54});
            zonasOvaladas.add(new float[]{817, 323, 45, 35});
        }
    }

    private int nivel;
    public void cargarNivel (int nivel){
        Listaobjetos.clear();
        this.nivel=nivel;

        if(fondo != null) fondo.dispose();
        if(vallaSuperior != null) vallaSuperior.dispose();
        if(vallaInferior != null) vallaInferior.dispose();
        if(vallaSuperior_Ab != null) vallaSuperior_Ab.dispose();

        fondo = new Texture("fondo" + nivel + ".png");
        vallaSuperior = new Texture("vallaSuperior" + nivel + ".png");
        vallaInferior = new Texture("vallaInferior" + nivel + ".png");
        vallaSuperior_Ab = new Texture("vallaSuperior_Ab"+nivel+".png");
        nivelCompl = false;
        arbusto = new Texture("arbusto"+nivel+".png");
        arbustoFlores = new Texture("arbustoFlores"+nivel+".png");

        //El sprite de "ya golpeado" tambien cambia por nivel: en el 3 es un tronco
        if(arbustoDanado != null) arbustoDanado.dispose();
        arbustoDanado = new Texture("arbustoRoto"+nivel+".png");
        estanteH = new Texture ("estanteHorizontal.png");
        estanteV = new Texture("estanteVertical.png");

        faseItem = 0;


        configurarLimites(nivel);

        //Llaves
        fotosLlave= new Texture[3];
        for (int i = 0; i < 3; i++) {
            fotosLlave[i]= new Texture("llave"+(i+1)+".png");
        }
        //Items
        fotosItem = new Texture[3][3];
        if (nivel == 1) {
            String[] colores = {"azul", "amarillo", "rosa"};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    fotosItem[i][j] = new Texture(colores[i] + (j + 1) + ".PNG");
                }
            }
        } else if (nivel == 2) {
            String[] conejos = {"negro", "marron", "beige"};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    fotosItem[i][j] = new Texture(conejos[i] + (j + 1) + ".PNG");
                }
            }
        }else if (nivel == 3) {
            String[] flores = {"blanca", "morada", "amarilla"};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    fotosItem[i][j] = new Texture(flores[i] + (j + 1) + ".PNG");
                }
            }
        }
        cargarArchivo("nivel"+nivel+".txt",nivel);
    }
    public void dipose() {
        if(arbusto != null) arbusto.dispose();
        if(arbustoFlores != null) arbustoFlores.dispose();
        if(vallaSuperior != null) vallaSuperior.dispose();
        if(vallaInferior != null) vallaInferior.dispose();
        if(fondo != null) fondo.dispose();
        if(estanteV != null) estanteV.dispose();
        if(estanteH != null) estanteH.dispose();
        if(arbustoDanado != null) arbustoDanado.dispose();
        if(vallaSuperior_Ab != null) vallaSuperior_Ab.dispose();

        if(fotosItem != null) {
            for (int i = 0; i < fotosItem.length; i++) {
                for (int j = 0; j < fotosItem[i].length; j++) {
                    if(fotosItem[i][j] != null) fotosItem[i][j].dispose();
                }
            }
        }
        if(sonidoRomper != null) sonidoRomper.dispose();
    }
}
