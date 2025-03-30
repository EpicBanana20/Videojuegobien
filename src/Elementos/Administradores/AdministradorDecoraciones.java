package Elementos.Administradores;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import Elementos.Decoraciones.Decoracion;
import Elementos.Decoraciones.DecoracionAnimada;
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
            case 10: // Ejemplo: Planta pequeña
                crearDecoracionEstatica(x, y, ancho, alto, "decoraciones/3.png");
                break;
                
            // Tipos 50-99: Decoraciones animadas
            case 50: // Ejemplo: Antorcha
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/Card.png", 0, new int[]{8});
                break;
            case 51: // Ejemplo: Fuente de agua
                crearDecoracionAnimada(x, y, ancho, alto, "decoraciones/fuente.png", 0, new int[]{6});
                break;
            
            // Añadir más tipos según sea necesario
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
    
    // Método para limpiar todas las decoraciones
    public void limpiarDecoraciones() {
        decoraciones.clear();
    }
}