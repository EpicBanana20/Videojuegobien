package Utilz;
public class Constantes {
    public static class Direccion{
        public static final int LEFT=0;
        public static final int UP=1;
        public static final int RIGHT=2;
        public static final int DOWN=3;
    }
    public static class Mapas{
        
    }
    public static class  ConstanteJugador {
        public static final int INACTIVO=0;
        public static final int CORRER=1;
        public static final int SALTAR=2;
        public static final int CAYENDO=3;
        public static final int DAÑO=4;
        public static final int MUERTE=5;

    
    public static int GetNoSprite(int player_action){
        switch (player_action) {
            case INACTIVO:
                return 4;
            case CORRER:
                return 7;
            case SALTAR:
                return 4;
            case CAYENDO:
                return 1;
            case DAÑO:
                return 2;
            case MUERTE:
                return 2;
            default:
                 return 1;
        }
    }
  }
}
