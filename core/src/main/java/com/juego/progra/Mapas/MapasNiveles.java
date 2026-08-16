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

public class MapasNiveles implements Caminable {

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
    private Texture estanteV;
    private Texture estanteH;
    private Texture fondo;
    private Texture vallaInferior,vallaSuperior;
    private Texture fotosItem[][];
    private int faseItem = 0;

    public int getFaseItem() {
        return faseItem;
    }

    //LIMITES DEL MUNDO: cambian en cada nivel, se asignan en cargarNivel()
    private float LIMITE_IZQ, LIMITE_DER, LIMITE_ABAJO, LIMITE_ARRIBA;

    //ZONAS FIJAS QUE BLOQUEAN - cambian por nivel
    private ArrayList<Rectangle> zonasBloqueadas = new ArrayList<>();

    //ZONAS QUE SE PUEDEN PISAR AUNQUE QUEDEN FUERA DE LOS LIMITES, para las
    //salidas por donde la reja esta abierta y el camino continua
    private ArrayList<Rectangle> zonasPermitidas = new ArrayList<>();

    //ROMPER ARBUSTOS: el tamaño del arbusto cambia por nivel, se guarda al cargar
    public static final float TIEMPO_PARA_ROMPER = 3f;
    private Texture arbustoDanado;
    private float anchoArbustoNivel = 70, altoArbustoNivel = 70;

    //Constructor
    public MapasNiveles() {
        Listaobjetos = new ArrayList<>();
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
                tamArbusto = 90;
                anchoArbs=110;

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

            //Fuera de la arena solo se puede estar en las salidas del camino
            boolean enSalida = false;
            for (int i = 0; i < zonasPermitidas.size(); i++) {
                if (zonasPermitidas.get(i).contains(rectanguloPersonaje)) {
                    enSalida = true;
                    break;
                }
            }
            if (!enSalida) return false;
        }

        // LA CASA (uno o varios rectangulos, segun el nivel)
        for (int i = 0; i < zonasBloqueadas.size(); i++) {
            if (rectanguloPersonaje.overlaps(zonasBloqueadas.get(i))) {
                return false;
            }
        }

        //LO DE LOS ARBUSTOS QUE YA ESTABA
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if ((obj.destructible || obj.indestructible) && rectanguloPersonaje.overlaps(obj.cajaColision)){
                return false;
            }
        }
        return true;
    }

    public void CambioFase(){
        boolean existeColor = false;
        for (int i = 0; i < Listaobjetos.size(); i++) {
            Objetos obj = Listaobjetos.get(i);
            if((obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor")) && obj.colorItem == faseItem){
                existeColor=true;
                break;
            }
        }
        if(!existeColor && faseItem == 2) {
            System.out.println("Invernadero abierto");
        } else if(!existeColor && faseItem < 2){
            faseItem++;
            System.out.println("Clase de Mariposa recolectada");
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


    //Método hecho para no repetir codigo en el metodo dibujar
    public void dibujarObjetos (SpriteBatch batch, Objetos obj){
        if (obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor")) {
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
    public void dibujar (SpriteBatch batch, Jugador jugador, List<Enemigo> enemigos, int nivel){
        batch.draw(fondo,0,0,1408,768);
        batch.draw(vallaSuperior,0,0,1408,768);

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
            }else if (cosa.obj instanceof Texture && (nivel == 1 || nivel == 2)){
                batch.draw((Texture) cosa.obj,465,257,490,260);
            }
        }
        batch.draw(vallaInferior,0,0,1408,768);

    }

    //Cambia el sprite del arbusto cuando ya lleva la mitad del daño
    public void actualizarDanoArbusto(Objetos obj) {
        if (obj.tiempoDañado >= TIEMPO_PARA_ROMPER * 0.5f) {
            obj.imagen = arbustoDanado;
        }
    }

    public void romperArbusto(Objetos obj) {
        Listaobjetos.remove(obj);
    }

    //Devuelve el arbusto rompible que esta REALMENTE en la direccion apuntada.
    //dx/dy dicen hacia donde se disparo; se descarta cualquier arbusto que este
    //hacia el lado contrario o demasiado desalineado del eje.
    public Objetos obtenerArbustoEnDireccion(Rectangle celda, float centroX, float centroY, float dx, float dy) {
        Objetos masCercano = null;
        float distanciaMinima = Float.MAX_VALUE;
        for (Objetos obj : Listaobjetos) {
            if (!obj.destructible || !celda.overlaps(obj.cajaObstaculo)) continue;

            float centroObjX = obj.cajaObstaculo.x + obj.cajaObstaculo.width / 2f;
            float centroObjY = obj.cajaObstaculo.y + obj.cajaObstaculo.height / 2f;

            float diffX = centroObjX - centroX;
            float diffY = centroObjY - centroY;

            if (dx != 0) {
                if (dx > 0 && diffX <= 0) continue;
                if (dx < 0 && diffX >= 0) continue;
                if (Math.abs(diffY) > altoArbustoNivel / 2f) continue;
            } else {
                if (dy > 0 && diffY <= 0) continue;
                if (dy < 0 && diffY >= 0) continue;
                if (Math.abs(diffX) > anchoArbustoNivel / 2f) continue;
            }

            float distancia = diffX * diffX + diffY * diffY;
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercano = obj;
            }
        }
        return masCercano;
    }

    //Avanza de a 5px desde el centro del jugador hasta topar con algo.
    //Si es un arbusto rompible le suma daño; si es otra cosa, se detiene.
    public void dispararLinea(float centroX, float centroY, String direccion) {
        float dx = 0, dy = 0;
        switch (direccion) {
            case "derecha": dx = 1; break;
            case "izquierda": dx = -1; break;
            case "arriba": dy = 1; break;
            case "abajo": dy = -1; break;
        }

        float paso = 5f;
        float alcanceMax = 5 * (dx != 0 ? anchoArbustoNivel : altoArbustoNivel);

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
                bloqueante.tiempoDañado += TIEMPO_PARA_ROMPER / 2f;
                actualizarDanoArbusto(bloqueante);
                if (bloqueante.tiempoDañado >= TIEMPO_PARA_ROMPER) {
                    romperArbusto(bloqueante);
                }
                return;
            }

            if (!CaminarCelda(celdaDeteccion)) {
                return;
            }
        }
    }

    //Aqui van los numeros propios de cada nivel: hasta donde se puede caminar
    //y que rectangulos fijos bloquean (la casa). Se llama desde cargarNivel.
    private void configurarLimites(int nivel){
        zonasBloqueadas.clear();
        zonasPermitidas.clear();

        if (nivel == 1) {
            //La arena de tierra va de x 114..1292, y 119..583 en el dibujo.
            //ARRIBA va mas alto que la arena porque la caja de colision queda
            //unos 30px por encima de los pies del sprite.
            LIMITE_IZQ = 114;
            LIMITE_DER = 1292;
            LIMITE_ABAJO = 119;
            LIMITE_ARRIBA = 613;

            //La reja esta abierta arriba y abajo por el centro, y por ahi sigue
            //el camino de piedra. Esos dos pasillos salen de la arena, asi que
            //se permiten aparte de los limites.
            zonasPermitidas.add(new Rectangle(643, 560, 92, 140)); //salida de arriba
            zonasPermitidas.add(new Rectangle(643,  60, 92,  85)); //salida de abajo
        } else if (nivel == 3) {
            //Nivel 3: el piso de baldosas va de x 192..1209, y 194..648.
            //ARRIBA va mas alto que el piso porque la caja de colision queda
            //unos 40px por encima de los pies del sprite.
            LIMITE_IZQ = 192;
            LIMITE_DER = 1209;
            LIMITE_ABAJO = 200;
            LIMITE_ARRIBA = 678;

            //Este nivel no tiene casa, asi que no se agrega ninguna zona fija.
            //Lo que bloquea son los estantes, que ya vienen en nivel3.txt.
        } else {
            //Nivel 2: la tierra abierta (sin valla encima) va de x 147..1245, y 120..650.
            //ARRIBA va mas alto que la tierra porque la caja de colision queda
            //unos 40px por encima de los pies del sprite.
            LIMITE_IZQ = 147;
            LIMITE_DER = 1245;
            LIMITE_ABAJO = 125;
            LIMITE_ARRIBA = 680;

            //La casa ya no se dibuja en ningun nivel, asi que tampoco bloquea.
        }
    }

    private int nivel;
    public void cargarNivel (int nivel){
        Listaobjetos.clear();
        this.nivel=nivel;

        if(fondo != null) fondo.dispose();
        if(vallaSuperior != null) vallaSuperior.dispose();
        if(vallaInferior != null) vallaInferior.dispose();

        fondo = new Texture("fondo" + nivel + ".png");
        vallaSuperior = new Texture("vallaSuperior" + nivel + ".png");
        vallaInferior = new Texture("vallaInferior" + nivel + ".png");
        arbusto = new Texture("arbusto"+nivel+".png");
        arbustoFlores = new Texture("arbustoFlores"+nivel+".png");

        //El sprite de "ya golpeado" tambien cambia por nivel: en el 3 es un tronco
        if(arbustoDanado != null) arbustoDanado.dispose();
        arbustoDanado = new Texture("arbustoRoto"+nivel+".png");
        estanteH = new Texture ("estanteHorizontal.png");
        estanteV = new Texture("estanteVertical.png");

        faseItem = 0;

        configurarLimites(nivel);

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
            String[] conejos = {"blanca", "morada", "amarilla"};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    fotosItem[i][j] = new Texture(conejos[i] + (j + 1) + ".PNG");
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

        if(fotosItem != null) {
            for (int i = 0; i < fotosItem.length; i++) {
                for (int j = 0; j < fotosItem[i].length; j++) {
                    if(fotosItem[i][j] != null) fotosItem[i][j].dispose();
                }
            }
        }
    }
}
