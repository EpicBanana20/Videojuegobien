package Utilz;

import Juegos.Juego;

public class Constantes {
    public static class Ambiente {
        public static final int BIG_CLOUD_WIDTH_DEF=448;
        public static final int BIG_CLOUD_HEIGHT_DEF=101;
        public static final int SMALL_CLOUD_WIDTH_DEF=74;
        public static final int SMALL_CLOUD_HEIGHT_DEF=24;

        public static final int BIG_CLOUD_WIDTH=(int) (BIG_CLOUD_WIDTH_DEF*Juego.SCALE);
        public static final int BIG_CLOUD_HEIGHT=(int) (BIG_CLOUD_HEIGHT_DEF*Juego.SCALE);
        public static final int SMALL_CLOUD_WIDTH=(int) (SMALL_CLOUD_WIDTH_DEF*Juego.SCALE);
        public static final int SMALL_CLOUD_HEIGHT=(int) (SMALL_CLOUD_HEIGHT_DEF*Juego.SCALE);
    }

    public static class Direccion{
        public static final int LEFT=0;
        public static final int UP=1;
        public static final int RIGHT=2;
        public static final int DOWN=3;
    }
    public static class  ConstanteJugador {
        public static final int CORRER=1;
        public static final int INACTIVO=0;
        public static final int SALTAR=2;
        public static final int CAYENDO=3;
        public static final int AGACHAR=4;
        public static final int GOLPEAR=5;
        public static final int ATACAR1=6;
        public static final int ATACAR_BRINCAR1=7;
        public static final int ATACAR_BRINCAR2=8;
    
    public static int GetNoSprite(int player_action){
        switch (player_action) {
            case CORRER: return 6;
            case INACTIVO: return 4;
            case GOLPEAR: return 5;
            case SALTAR: 
            case ATACAR1:
            case ATACAR_BRINCAR2:
            case ATACAR_BRINCAR1: return 3;
            case AGACHAR: return 2;
            case CAYENDO:         
            default:
                 return 1;
        }
    }
  }
}
