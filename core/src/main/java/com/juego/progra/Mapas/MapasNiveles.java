package com.juego.progra.Mapas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class MapasNiveles {

    private int[][] mapaNivel1 = {
        //0 Camino Libre
        //1 borde
        //2 Arbusto rompible (Normal)
        //3 Arbusto irrompible (con flores)
        //4 Objeto recolectable (por los momentos no tiene)
        //5 invernadero
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {3,0,3,2,2,2,2,2,2,0,0,0,0,2,2,2,2,2,2,3,0,3},
        {3,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,3},
        {3,0,2,0,3,2,2,0,2,0,5,5,0,2,0,0,2,3,0,2,0,3},
        {3,0,0,0,2,0,0,0,0,5,5,5,5,0,0,0,0,2,0,0,0,3},
        {3,0,2,0,3,2,2,0,0,5,5,5,5,0,0,2,2,3,0,2,0,3},
        {3,0,2,0,0,0,0,0,2,0,0,0,0,2,0,0,0,0,0,2,0,3},
        {3,0,3,2,2,2,2,2,2,0,0,0,0,2,2,2,2,2,2,3,0,3},
        {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private Texture fondo;
    private Texture arbusto; //Rompible
    private Texture arbustoFlores; //Irrompible
    private Texture mariposa;
    private Texture invernadero;
    private Texture valla;

    //Constructor
    public MapasNiveles() {
        fondo = new Texture("Fondo.png");
        arbusto = new Texture("Arbusto.png");
        arbustoFlores = new Texture("ArbustoFlores.png");
        mariposa= new Texture("MariposaAzul.png");
        invernadero= new Texture("Invernadero.png");
        valla = new Texture("Valla.png");
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(fondo,0,0,1408,768);

        float inicioX=100;
        float inicioY=15;

        //Recorre la matriz
        for (int i = 0; i < mapaNivel1.length; i++) {
            for (int j = 0; j < mapaNivel1[i].length; j++) {
                //Para calcular los pixeles en la pantalla
                float xPixel = (j*54)+inicioX; //Columna, eje x
                float yPixel = (i*57)+inicioY;//Fila, eje y

                if(mapaNivel1[i][j]==2){
                    batch.draw(arbusto,xPixel,yPixel,70,70);
                }else if (mapaNivel1[i][j]==3) {
                    batch.draw(arbustoFlores, xPixel, yPixel-10, 80, 80);
                }
            }

        }
        batch.draw(invernadero, 8 * 54, 4 * 57, 550, 270);
        batch.draw(valla,0,0,1408, 768);
    }
    public boolean CaminarCelda (int cX, int cY){
        if(cX<0 || cX>=22 || cY<0 || cY>=12){
            return false;
        }
        int cActual = mapaNivel1[cX][cY];
        if(cActual==0 || cActual==3){
            return true;
        }else{
            return false;
        }
    }

    public boolean obtenerObjeto(int cX,int cY){
        int obj= mapaNivel1[cX][cY];
        if(obj==3){
            mapaNivel1[cX][cY]=0;
            return true;
        }else{
            return false;
        }
    }
    public void dispose(){
        fondo.dispose();
        arbusto.dispose();
        arbustoFlores.dispose();
        mariposa.dispose();
        invernadero.dispose();
    }
}
