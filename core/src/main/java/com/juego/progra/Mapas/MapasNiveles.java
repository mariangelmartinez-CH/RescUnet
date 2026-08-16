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
    private int faseItem = 0;
    private boolean nivelCompl = false;
    private Texture arbusto;
    private Texture arbustoFlores;
    private Texture estanteV;
    private Texture estanteH;
    private Texture fondo;
    private Texture vallaInferior,vallaSuperior,vallaSuperior_Ab;
    private Texture fotosItem[][];

    public int getFaseItem() {
        return faseItem;
    }

    //LIMITES DEL MUNDO: cambian en cada nivel, se asignan en cargarNivel()
    private float LIMITE_IZQ, LIMITE_DER, LIMITE_ABAJO, LIMITE_ARRIBA;

    //ZONAS FIJAS QUE BLOQUEAN - cambian por nivel
    private ArrayList<Rectangle> zonasBloqueadas = new ArrayList<>();

    //ENTRADA DE ABAJO: el trozo de camino que queda fuera de la arena y que SI
    //se puede pisar siempre, porque esa reja esta abierta desde el principio.
    //Es por donde entra el jugador y donde vuelve a aparecer al perder una vida.
    private ArrayList<Rectangle> zonasPermitidas = new ArrayList<>();

    //SALIDA DE ARRIBA: el camino que sigue por encima de la reja del fondo.
    //Empieza CERRADA. Solo se puede pasar cuando nivelCompl es true, que es el
    //mismo momento en el que se cambia el sprite de la valla por el de abierta.
    //Mientras siga cerrada, LIMITE_ARRIBA es el que no deja subir.
    private Rectangle salidaArriba;

    //Donde aparece el jugador al entrar al nivel y cada vez que pierde una vida:
    //en el camino de abajo, justo debajo de la reja abierta.
    private float spawnX, spawnY;

    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
    public boolean estaCompletado() { return nivelCompl; }

    //SOLO PARA PROBAR: da el nivel por terminado sin recoger nada, para ver
    //como cambia la reja y poder subir por el camino. Quitar al entregar.
    public void completarDePrueba() { nivelCompl = true; }

    //True cuando el jugador ya subio hasta el final del camino de arriba: esa
    //es la señal de que se acabo el nivel y toca volver al mapa principal.
    //No hace falta que toque el borde exacto (CaminarCelda lo frena unos pocos
    //pixeles antes), asi que basta con que llegue al ultimo tramo del camino.
    private static final float MARGEN_SALIDA = 15;

    public boolean saliendoPorArriba(Rectangle caja) {
        if (!nivelCompl || salidaArriba == null) return false;
        float topeDelCamino = salidaArriba.y + salidaArriba.height;
        return caja.y + caja.height >= topeDelCamino - MARGEN_SALIDA;
    }

    //ZONAS REDONDAS QUE BLOQUEAN: los charcos del nivel 2, que vienen pintados
    //en el fondo y no son objetos del .txt. Se guardan como ovalos
    //{centroX, centroY, radioX, radioY} y no como rectangulos porque con un
    //rectangulo las cuatro esquinas taparian tierra por la que si se camina.
    private ArrayList<float[]> zonasOvaladas = new ArrayList<>();

    //ROMPER ARBUSTOS: el tamaño del arbusto cambia por nivel, se guarda al cargar
    public static final float TIEMPO_PARA_ROMPER = 3f;

    //HASTA DONDE LLEGA EL GOLPE. Antes esto se calculaba como 5 veces el ancho
    //del arbusto, o sea 500px en el nivel 2 y 400 en el 1: se rompian matas a
    //media pantalla. El golpe es cuerpo a cuerpo, solo tiene que alcanzar la
    //mata que se tiene pegada. La celda con la que se busca ya mide 20, asi
    //que con 15 de alcance se revisan unos 25px por delante del jugador, que
    //es justo lo que hay desde su centro hasta la mata que lo esta frenando.
    public static final float ALCANCE_GOLPE = 15f;
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

            //Fuera de la arena solo se puede estar en los caminos, y hay que
            //entrar ENTERO en uno de ellos (contains, no overlaps): asi no se
            //puede uno salir del camino a mitad de camino.
            boolean enSalida = false;
            for (int i = 0; i < zonasPermitidas.size(); i++) {
                if (zonasPermitidas.get(i).contains(rectanguloPersonaje)) {
                    enSalida = true;
                    break;
                }
            }

            //El camino de arriba solo cuenta si ya se recogio todo el nivel
            if (!enSalida && nivelCompl && salidaArriba != null
                    && salidaArriba.contains(rectanguloPersonaje)) {
                enSalida = true;
            }

            if (!enSalida) return false;
        }

        // LA CASA (uno o varios rectangulos, segun el nivel)
        for (int i = 0; i < zonasBloqueadas.size(); i++) {
            if (rectanguloPersonaje.overlaps(zonasBloqueadas.get(i))) {
                return false;
            }
        }

        // LOS CHARCOS DEL NIVEL 2
        for (int i = 0; i < zonasOvaladas.size(); i++) {
            if (tocaOvalo(rectanguloPersonaje, zonasOvaladas.get(i))) {
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

    //La caja de colision va en el CENTRO del sprite, pero los pies quedan unas
    //35 unidades mas abajo (el dibujo del jugador termina a 13px del borde de
    //su textura, y la caja esta a 50). Los arbustos son cosas altas y ahi da
    //igual, pero el charco esta pintado EN EL SUELO: lo que no puede mojarse
    //son los pies. Por eso la caja se prueba corrida hacia abajo, que es donde
    //el personaje de verdad esta pisando. Es el mismo truco de MapaPrincipal.
    private static final float DESFASE_PIES = 35;

    //Dice si la caja del personaje toca el ovalo. Se busca el punto de la caja
    //mas cercano al centro del charco y se mide esa distancia EN RADIOS: si da
    //1 o menos, el punto cayo dentro. Dividir entre el radio de cada eje es lo
    //que permite que el ovalo sea mas ancho que alto.
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
            if((obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor")) && obj.colorItem == faseItem){
                existeColor=true;
                break;
            }
        }
        if(!existeColor && faseItem == 2) {
            nivelCompl=true;
            System.out.println("Nivel Completado");
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
        Texture vallaDibujar = nivelCompl ? vallaSuperior_Ab : vallaSuperior;
        batch.draw(vallaDibujar, 0, 0, 1408, 768);
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
        float alcanceMax = ALCANCE_GOLPE;

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
        zonasOvaladas.clear();
        salidaArriba = null;

        //Los dos caminos, el de entrada y el de salida, se midieron sobre el
        //hueco REAL que dejan los sprites de la reja (vallaSuperior_Ab y
        //vallaInferior), no sobre el fondo: el fondo del nivel 1 abajo es una
        //terraza ancha, pero la reja solo deja pasar por el centro.
        //Van en coordenadas de la CAJA del jugador, que queda unas 35 unidades
        //por encima de sus pies, por eso el alto no coincide con lo que se ve.

        if (nivel == 1) {
            //La arena de tierra va de x 114..1292, y 119..583 en el dibujo.
            //ARRIBA va mas alto que la arena porque la caja de colision queda
            //unos 30px por encima de los pies del sprite.
            LIMITE_IZQ = 114;
            LIMITE_DER = 1292;
            LIMITE_ABAJO = 119;
            LIMITE_ARRIBA = 613;

            //El hueco de la reja del nivel 1 va de x 625 a 742. Se deja en 92
            //de ancho, centrado, para que quede margen y no se pise el poste.
            zonasPermitidas.add(new Rectangle(643, 45, 92, 100));  //entrada de abajo (siempre abierta)
            salidaArriba = new Rectangle(643, 560, 92, 190);       //salida de arriba (solo al completar)

            spawnX = 641;  //la caja queda en x 689, el centro del camino
            //Mas abajo de lo que haria falta a proposito: el villano horizontal
            //de abajo patrulla en y=80, y Main da por golpe |dx|<50 y |dy|<50,
            //asi que con y=52 lo mataba nada mas aparecer. Con 25 la diferencia
            //es de 55 y ya no lo alcanza en el sitio donde reaparece.
            spawnY = 25;
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

            //Todavia sin caminos de entrada ni salida: este nivel no esta listo.
            spawnX = 480;
            spawnY = 200;
        } else {
            //Nivel 2: la tierra abierta (sin valla encima) va de x 176..1281, y 112..629.
            //ARRIBA va mas alto que la tierra porque la caja de colision queda
            //unos 40px por encima de los pies del sprite. IZQ y DER en cambio
            //van igual que el borde dibujado, porque de lado la caja SI esta
            //centrada en el sprite.
            LIMITE_IZQ = 176;
            LIMITE_DER = 1281;
            LIMITE_ABAJO = 125;
            LIMITE_ARRIBA = 680;

            //La casa ya no se dibuja en ningun nivel, asi que tampoco bloquea.

            //El hueco de la reja del nivel 2 va de x 640 a 755. Se deja en 100
            //de ancho, centrado, para no pisar el poste de ningun lado.
            zonasPermitidas.add(new Rectangle(650, 45, 100, 100)); //entrada de abajo (siempre abierta)
            salidaArriba = new Rectangle(650, 645, 100, 110);      //salida de arriba (solo al completar)

            spawnX = 650;  //la caja queda en x 698, el centro del camino
            spawnY = 42;   //los pies quedan en y 55, sobre el camino visible

            //LOS DOS CHARCOS DEL CENTRO. Van pintados dentro de fondo2.png, no
            //son objetos del .txt, por eso hay que bloquearlos aparte.
            //Las medidas se sacaron midiendo el dibujo y convirtiendolas al
            //mundo: el PNG mide 1380x752 pero se dibuja estirado a 1408x768, y
            //ademas su Y va al reves. Se tomo el BORDE DE PIEDRA, no solo el
            //agua, para que no se pueda pisar el anillo de piedras.
            zonasOvaladas.add(new float[]{708, 400, 86, 54}); //charco grande
            zonasOvaladas.add(new float[]{817, 323, 45, 35}); //charco pequeño
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
        if(vallaSuperior_Ab != null) vallaSuperior_Ab.dispose();

        if(fotosItem != null) {
            for (int i = 0; i < fotosItem.length; i++) {
                for (int j = 0; j < fotosItem[i].length; j++) {
                    if(fotosItem[i][j] != null) fotosItem[i][j].dispose();
                }
            }
        }
    }
}
