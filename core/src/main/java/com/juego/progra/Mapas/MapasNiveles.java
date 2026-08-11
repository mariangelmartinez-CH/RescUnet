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
    private Texture invernaderoTecho, invernaderoPisoC, invernaderoPisoA,invernaderoActual;
    private Texture fotosItem[][];
    private int faseItem = 0;

    public int getFaseItem() {
        return faseItem;
    }

    //LIMITES DEL MUNDO: cambian en cada nivel, se asignan en cargarNivel()
    private float LIMITE_IZQ, LIMITE_DER, LIMITE_ABAJO, LIMITE_ARRIBA;

    //ZONAS FIJAS QUE BLOQUEAN (la casa) - tambien cambian por nivel
    private ArrayList<Rectangle> zonasBloqueadas = new ArrayList<>();

    //Constructor
    public MapasNiveles() {
        invernaderoTecho = new Texture("InvernaderoTecho.png");
        invernaderoPisoC = new Texture("InvernaderoPiso_Cerrado.png");
        invernaderoPisoA = new Texture("InvernaderoPiso.png");
        invernaderoActual = invernaderoPisoC;

        Listaobjetos = new ArrayList<>();
    }

    public void cargarArchivo (String archivo,int nivel){

        if (!Gdx.files.internal(archivo).exists()){
            System.out.println("Archivo de Mapa no encontrado");
            return;
        }else{

            int anchoArbs=70;
            int tamArbusto=70;
            if(nivel==2) {
                tamArbusto = 90;
                anchoArbs=110;
            }

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
                    case "mariposa":
                    case "conejo":
                    case "flor":
                        int color = Integer.parseInt(datos[3].trim());
                        objTemp = new Objetos(fotosItem[color],x,y,60,60,tipoObj,color);
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

        // LA CASA (uno o varios rectangulos, segun el nivel)
        for (int i = 0; i < zonasBloqueadas.size(); i++) {
            if (rectanguloPersonaje.overlaps(zonasBloqueadas.get(i))) {
                return false;
            }
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
            if((obj.tipo.equals("mariposa") || obj.tipo.equals("conejo") || obj.tipo.equals("flor")) && obj.colorItem == faseItem){
                existeColor=true;
                break;
            }
        }
        if(!existeColor && faseItem == 2) {
            this.invernaderoActual = invernaderoPisoA;
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
                obj.dibujarM(batch, tiempo);
            }
        } else {
            obj.dibujar(batch);
        }
    }
    public void dibujar (SpriteBatch batch, Jugador jugador, List<Enemigo> enemigos){
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
        //La casa entra al mismo orden por Y usando la base de sus paredes (347),
        //que es donde apoya en el suelo por los costados. Quien este mas abajo
        //se dibuja delante, quien este mas arriba queda detras.
        arrayDibujo.add(new Dibujable(invernaderoActual, 347));

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
            }else if (cosa.obj instanceof Texture){
                batch.draw((Texture) cosa.obj,465,257,490,260);
            }
        }
        batch.draw(vallaInferior,0,0,1408,768);
        batch.draw(invernaderoTecho,465,257,490,260);

    }

    //Aqui van los numeros propios de cada nivel: hasta donde se puede caminar
    //y que rectangulos fijos bloquean (la casa). Se llama desde cargarNivel.
    private void configurarLimites(int nivel){
        zonasBloqueadas.clear();

        if (nivel == 1) {
            LIMITE_IZQ = 125;
            LIMITE_DER = 1279;
            LIMITE_ABAJO = 140;
            LIMITE_ARRIBA = 590;

            //Casa: medida sobre el dibujo real (465,257,490,260)
            //La casa se bloquea en tres partes: las dos paredes laterales frenan
            //al ras del suelo, y la del centro empieza mas arriba para que el
            //jugador pueda subir los escalones y quedar pegado a la puerta.
            zonasBloqueadas.add(new Rectangle(567, 347, 71, 143));  //pared izquierda
            zonasBloqueadas.add(new Rectangle(783, 347, 71, 143));  //pared derecha
            zonasBloqueadas.add(new Rectangle(638, 365, 145, 125)); //puerta (escalones libres)
        } else {
            //Nivel 2: la tierra abierta (sin valla encima) va de x 147..1245, y 120..650.
            //ARRIBA va mas alto que la tierra porque la caja de colision queda
            //unos 40px por encima de los pies del sprite.
            LIMITE_IZQ = 147;
            LIMITE_DER = 1245;
            LIMITE_ABAJO = 125;
            LIMITE_ARRIBA = 680;

            //La casa se dibuja igual que en el nivel 1, asi que bloquea igual
            //La casa se bloquea en tres partes: las dos paredes laterales frenan
            //al ras del suelo, y la del centro empieza mas arriba para que el
            //jugador pueda subir los escalones y quedar pegado a la puerta.
            zonasBloqueadas.add(new Rectangle(567, 347, 71, 143));  //pared izquierda
            zonasBloqueadas.add(new Rectangle(783, 347, 71, 143));  //pared derecha
            zonasBloqueadas.add(new Rectangle(638, 365, 145, 125)); //puerta (escalones libres)
        }
    }

    public void cargarNivel (int nivel){
        Listaobjetos.clear();

        if(fondo != null) fondo.dispose();
        if(vallaSuperior != null) vallaSuperior.dispose();
        if(vallaInferior != null) vallaInferior.dispose();

        fondo = new Texture("fondo" + nivel + ".png");
        vallaSuperior = new Texture("vallaSuperior" + nivel + ".png");
        vallaInferior = new Texture("vallaInferior" + nivel + ".png");
        arbusto = new Texture("arbusto"+nivel+".png");
        arbustoFlores = new Texture("arbustoFlores"+nivel+".png");

        faseItem = 0;
        invernaderoActual = invernaderoPisoC; // Invernadero siempre inicia cerrado

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
        }
        cargarArchivo("nivel"+nivel+".txt",nivel);
    }
    public void dipose() {
        if(arbusto != null) arbusto.dispose();
        if(arbustoFlores != null) arbustoFlores.dispose();
        if(vallaSuperior != null) vallaSuperior.dispose();
        if(vallaInferior != null) vallaInferior.dispose();
        if(invernaderoTecho != null) invernaderoTecho.dispose();
        if(invernaderoPisoC != null) invernaderoPisoC.dispose();
        if(invernaderoPisoA != null) invernaderoPisoA.dispose();
        if(fondo != null) fondo.dispose();

        if(fotosItem != null) {
            for (int i = 0; i < fotosItem.length; i++) {
                for (int j = 0; j < fotosItem[i].length; j++) {
                    if(fotosItem[i][j] != null) fotosItem[i][j].dispose();
                }
            }
        }
    }
}
