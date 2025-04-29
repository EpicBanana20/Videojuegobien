package Elementos.Administradores;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import Elementos.Decoraciones.Decoracion;
import Elementos.Decoraciones.DecoracionAnimada;
import Elementos.Decoraciones.ElementoRecolectable;
import Elementos.Decoraciones.EstacionQuimica;
import Juegos.Juego;
import Utilz.LoadSave;

public class AdministradorDecoraciones {
    // Lista para almacenar todas las decoraciones
    private ArrayList<Decoracion> decoraciones = new ArrayList<>();
    
    // Cache para almacenar sprites de decoraciones
    private HashMap<Integer, BufferedImage> spriteCache = new HashMap<>();
    private HashMap<Integer, BufferedImage[][]> animationCache = new HashMap<>();
    
    public AdministradorDecoraciones() {
        // Inicializar el administrador
    }
    
    // Método para agregar una decoración
    public void agregarDecoracion(Decoracion decoracion) {
        decoraciones.add(decoracion);
    }
    
    // Actualizar todas las decoraciones
    public void update() {
        for (Decoracion decoracion : decoraciones) {
            decoracion.update();
        }
    }
    
    // Renderizar todas las decoraciones
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Decoracion decoracion : decoraciones) {
            decoracion.render(g, xLvlOffset, yLvlOffset);
        }
    }
    
    // Método para crear decoración a partir de color en imagen de entorno
    public void crearDecoracion(int tipo, float x, float y, int anchoTiles, int altoTiles) {
        // Obtener dimensiones reales (escalar por TILES_SIZE)
        int ancho = anchoTiles * (int)Juegos.Juego.TILES_SIZE;
        int alto = altoTiles * (int)Juegos.Juego.TILES_SIZE;
        
        // Según el tipo, crear decoración estática o animada
        switch (tipo) {

            // Tipos 10-49: Decoraciones estáticas
                //MUNDO 1
            case 1: //ARBOL1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOL1.png");
                break;
            case 2: //ARBOL2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOL2.png");
                break;
            case 3: //ARBOL3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOL3.png");
                break;
            case 4: //TRONCO1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TRONCO1.png");
                break;
            case 5: //TRONCO2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TRONCO2.png");
                break;
            case 6: //TRONCO3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TRONCO3.png");
                break;
            case 7: //TRONCO4
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TRONCO4.png");
                break;
            case 8: //ARBOLSECO1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLSECO1.png");
                break;
            case 9: //ARBOLSECO2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLSECO2.png");
                break;
            case 10: //ARBOLSECO3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLSECO3.png");
                break;
            case 11: //PIEDRA1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/PIEDRA1.png");
                break;
            case 12: //PIEDRA2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/PIEDRA2.png");
                break;
            case 13: //LIANAS1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/LIANAS1.png");
                break;




                
                //MUNDO 2
            case 14: //CARTEL1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/CARTEL1.png");
                break;
            case 15: //CARTEL2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/CARTEL2.png");
                break;
            case 16: //ARBOLOP1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLOP1.png");
                break;
            case 17: //ARBOLOP2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLOP2.png");
                break;
            case 18: //ARBOLOP3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLOP3.png");
                break;
            case 19: //ARBOLOP4
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLOP4.png");
                break;
            case 20: //ARBOLOP5
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/ARBOLOP5.png");
                break;
           
           
           
           
           
           //MUNDO 3
            case 21: //TUBO1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TUBO1.png");
                break;
            case 22: //TUBO2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TUBO2.png");
                break;
            case 23: //TUBO3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TUBO3.png");
                break;
            case 24: //TUBO4
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TUBO4.png");
                break;
            case 25: //FONDOS1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS1.png");
                break;
            case 26: //FONDOS2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS2.png");
                break;
            case 27: //FONDOS3
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS3.png");
                break;
            case 28: //FONDOS4
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS4.png");
                break;
            case 29: //FONDOS5
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS5.png");
                break;
            case 30: //FONDOS6
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS6.png");
                break;
            case 31: //FONDOS7
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS7.png");
                break;
            case 32: //FONDOS8
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS8.png");
                break;
            case 33: //FONDOS9
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS9.png");
                break;
            case 34: //FONDOS10
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS10.png");
                break;
            case 35: //FONDOS11
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS11.png");
                break;
            case 36: //FONDOS12
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/FONDOS12.png");
                break;
            case 37: //OSCURIDAD
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/OSCURIDAD.png");
                break;
            case 38: //NUBA1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/NUBA1.png");
                break;
            case 39: //NUBA2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/NUBA2.png");
                break;
            case 40: //TORRE1
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TORRE1.png");
                break;
            case 41: //TORRE2
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/TORRE2.png");
                break;
        
            

            
            
            
                // Tipos 50-99: Decoraciones animadas
            case 50: // Ejemplo: Antorcha
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/Card.png", 0, new int[]{8});
                break;
            case 51: // Ejemplo: Fuente de agua
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/fuente.png", 0, new int[]{6});
                break;
            case 52: //LUVIA1
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/LLUVIA1.png", 0, new int[]{3});
                break;
            case 53: // REACTOR1
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/LLUVIA1.png", 0, new int[]{4});
                break;
            case 54: //HOJAS1
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/HOJAS1.png", 0, new int[]{7});
                break;
            case 55: //DESTELLO1
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/DESTELLO1.png", 0, new int[]{60});
                break;
            case 56: //REACTOR1
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/REACTOR1.png", 0, new int[]{4});
                break;
            case 57: //BANDERA
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/BANDERA.png", 0, new int[]{4});
                break;

            // En AdministradorDecoraciones.java, dentro del método crearDecoracion
            case 100: // Estación Química
                crearEstacionQuimica(x, y, ancho, alto, "Estacion/EstacionQuimica.png");
            break;
            case 200: // Hidrógeno
                crearElementoQuimico(x, y, "H");
                break;
            case 201: // Oxígeno
                crearElementoQuimico(x, y, "O");
                break;
            case 202: // Carbono
                crearElementoQuimico(x, y, "C");
            break;
            case 203: // Nitrógeno
                crearElementoQuimico(x, y, "N");
                break;
            case 204: // Sodio
                crearElementoQuimico(x, y, "Na");
                break;
            case 205: // Cloro
                crearElementoQuimico(x, y, "Cl");
                break;
            case 206: // Azufre
                crearElementoQuimico(x, y, "S");
                break;
            case 207: // Fósforo
                crearElementoQuimico(x, y, "P");
                break;
            case 208: // Cobre
                crearElementoQuimico(x, y, "Cu");
                break;
            case 209: // Plata
                crearElementoQuimico(x, y, "Ag");
                break;
            case 210: // Platino
                crearElementoQuimico(x, y, "Pt");
                break;
        }
    }
    
    // Método para crear una decoración estática
    public void crearDecoracionEstatica(float x, float y, int ancho, int alto, String spritePath) {
        BufferedImage sprite = LoadSave.GetSpriteAtlas(spritePath);
        if (sprite != null) {
            Decoracion nuevaDecoracion = new Decoracion(x, y, ancho, alto, sprite) {
            };
            agregarDecoracion(nuevaDecoracion);
        }
    }
    
    // Método para crear una decoración animada
    public void crearDecoracionAnimada(float x, float y, int ancho, int alto, 
                                     String spriteSheetPath, int tipoAnimacion, int[] framesPorAnimacion) {
        // Cargar el spritesheet
        BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(spriteSheetPath);
        if (spriteSheet != null) {
            // Preparar los frames de animación
            // Asumimos que cada fila en el spritesheet es un tipo de animación
            // y cada columna es un frame dentro de esa animación
            int numAnimaciones = framesPorAnimacion.length;
            int maxFrames = 0;
            for (int frames : framesPorAnimacion) {
                if (frames > maxFrames) maxFrames = frames;
            }
            
            // Calcular tamaño de cada frame en el spritesheet
            int frameWidth = spriteSheet.getWidth() / maxFrames;
            int frameHeight = spriteSheet.getHeight() / numAnimaciones;
            
            // Crear matriz de frames
            BufferedImage[][] animationFrames = new BufferedImage[numAnimaciones][maxFrames];
            
            // Extraer cada frame
            for (int j = 0; j < numAnimaciones; j++) {
                for (int i = 0; i < framesPorAnimacion[j]; i++) {
                    animationFrames[j][i] = spriteSheet.getSubimage(
                        i * frameWidth, j * frameHeight, frameWidth, frameHeight);
                }
            }
            
            // Crear la decoración animada
            DecoracionAnimada decoracion = new DecoracionAnimada(
                x, y, ancho, alto, animationFrames, tipoAnimacion, framesPorAnimacion);
            
            agregarDecoracion(decoracion);
        }
    }
    
    public void crearEstacionQuimica(float x, float y, int ancho, int alto, String spritePath) {
        BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(spritePath);
        if (spriteSheet != null) {
            // Dividir el spritesheet en los dos estados
            BufferedImage[] sprites = new BufferedImage[2];
            
            int frameWidth = 50; // Ancho correcto de cada sprite
            int frameHeight = 50; // Alto de cada sprite
            
            // Extraer los dos sprites
            sprites[0] = spriteSheet.getSubimage(0, 0, frameWidth, frameHeight); // Inactivo
            sprites[1] = spriteSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight); // Activo
            
            // Crear la estación química
            EstacionQuimica nuevaEstacion = new EstacionQuimica(x, y, ancho, alto, sprites);
            agregarDecoracion(nuevaEstacion);
        }
    }

    public void crearElementoQuimico(float x, float y, String simbolo) {
    BufferedImage sprite = LoadSave.GetSpriteAtlas("elementos/" + simbolo + ".png");
    if (sprite != null) {
        ElementoRecolectable elemento = new ElementoRecolectable(
            x, y, 
            (int)(24 * Juego.SCALE), 
            (int)(24 * Juego.SCALE), 
            sprite, 
            simbolo
        );
        agregarDecoracion(elemento);
    }
}
    // Método para limpiar todas las decoraciones
    public void limpiarDecoraciones() {
        decoraciones.clear();
    }

    public ArrayList<Decoracion> getDecoraciones() {
        return decoraciones;
    }
}