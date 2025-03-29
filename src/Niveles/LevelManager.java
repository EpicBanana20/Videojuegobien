package Niveles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.MetodoAyuda;

public class LevelManager {
    private Juego game;
    private BufferedImage[] levelSprite;
    private Level[] levels;
    private int currentLevelIndex = 0;
    
    // Los atlas de niveles disponibles
    private static final String[] LEVEL_ATLASES = {
        LoadSave.LEVEL_ATLAS,
        LoadSave.LEVEL_ATLAS2,
        LoadSave.LEVEL_ATLAS3,
    };
    
    // Los datos de niveles disponibles
    private static final String[] LEVEL_DATA_FILES = {
        LoadSave.LEVEL_ONE_DATA,
        LoadSave.LEVEL_TWO_DATA,
        LoadSave.LEVEL_THREE_DATA,
    };

    private static final String[] LEVEL_ENVIRONMENT_FILES = {
        LoadSave.LEVEL_ONE_ENVIRONMENT,
        LoadSave.LEVEL_TWO_ENVIRONMENT,
        LoadSave.LEVEL_THREE_ENVIRONMENT,
    };

    private static final String[] LEVEL_ENTITIES_FILES = {
        LoadSave.LEVEL_ONE_ENTITIES,
        LoadSave.LEVEL_TWO_ENTITIES,
        LoadSave.LEVEL_THREE_ENTITIES,
    };

    public LevelManager(Juego game) {
        this.game = game;
        // Inicializamos con el nivel 1
        importOutsideSprite(0);
        loadAllLevels();
    }

    private void loadAllLevels() {
        levels = new Level[LEVEL_DATA_FILES.length];
        for (int i = 0; i < LEVEL_DATA_FILES.length; i++) {
            levels[i] = new Level(LoadSave.GetLevelData(LEVEL_DATA_FILES[i], i));
        }
    }

    private void importOutsideSprite(int levelIndex) {
        // Cargamos el atlas correspondiente al nivel
        BufferedImage img = LoadSave.GetSpriteAtlas(LEVEL_ATLASES[levelIndex]);
        
        // Obtenemos la información específica de este atlas
        int maxTileIndex = LoadSave.LEVEL_INFO[levelIndex][0];
        int numFilas = LoadSave.LEVEL_INFO[levelIndex][2];
        int numColumnas = LoadSave.LEVEL_INFO[levelIndex][3];
        
        System.out.println("Cargando atlas nivel " + (levelIndex + 1) + 
                          ": " + numFilas + " filas x " + numColumnas + " columnas");
        
        // Inicializamos el array de sprites
        levelSprite = new BufferedImage[maxTileIndex];
        
        // Cargamos cada tile según la estructura de filas y columnas de este atlas
        int index = 0;
        for (int j = 0; j < numFilas; j++) {
            for (int i = 0; i < numColumnas; i++) {
                if (index < maxTileIndex) {
                    try {
                        levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
                    } catch (Exception e) {
                        System.err.println("Error al cargar tile [" + j + "," + i + "] del nivel " + 
                                          (levelIndex + 1) + ": " + e.getMessage());
                    }
                    index++;
                }
            }
        }
        
        System.out.println("Cargados " + index + " tiles para el nivel " + (levelIndex + 1));
    }

    public void cargarEntidades(Juego game) {
        BufferedImage img = LoadSave.GetSpriteAtlas(LEVEL_ENTITIES_FILES[currentLevelIndex]);
        
        // Limpiar enemigos existentes antes de cargar nuevos
        game.getAdminEnemigos().limpiarEnemigos();
        
        // Iterar a través de cada píxel de la imagen
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valorRojo = color.getRed();
                
                // Convertir la posición del píxel a posición del mundo
                float x = i * Juego.TILES_SIZE;
                float y = j * Juego.TILES_SIZE;
                switch (valorRojo) {
                    case 1:
                    game.getPlayer().resetPosition(x, y);
                        break;
                    case 2:
                    game.getAdminEnemigos().crearEnemigoVerde(x, y);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    public void cargarDecoraciones() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LEVEL_ENVIRONMENT_FILES[currentLevelIndex]);
        // Procesar decoraciones - Puedes crear una nueva clase AdministradorDecoraciones
    }

    public void update() {
        // Actualización específica del nivel si es necesario
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Aumentar el área de dibujo añadiendo un margen de tiles extra
        int margenExtra = 5; // Número de tiles adicionales en cada dirección
        
        int filaInicio = Math.max(0, (yLvlOffset / Juego.TILES_SIZE) - margenExtra);
        int filaFin = Math.min(getCurrentLevel().getLvlData().length,
                ((yLvlOffset + Juego.GAME_HEIGHT) / Juego.TILES_SIZE) + margenExtra);
    
        int colInicio = Math.max(0, (xLvlOffset / Juego.TILES_SIZE) - margenExtra);
        int colFin = Math.min(getCurrentLevel().getLvlData()[0].length,
                ((xLvlOffset + Juego.GAME_WIDTH) / Juego.TILES_SIZE) + margenExtra);
    
        // Dibujar solo las tiles visibles y el margen extra
        for (int j = filaInicio; j < filaFin; j++) {
            for (int i = colInicio; i < colFin; i++) {
                int index = getCurrentLevel().getSpriteIndex(j, i);
                if (index < levelSprite.length && levelSprite[index] != null) {
                    g.drawImage(levelSprite[index],
                            Juego.TILES_SIZE * i - xLvlOffset,
                            Juego.TILES_SIZE * j - yLvlOffset,
                            Juego.TILES_SIZE,
                            Juego.TILES_SIZE,
                            null);
                }
            }
        }
    }

    public Level getCurrentLevel() {
        return levels[currentLevelIndex];
    }
    
    // Método para cambiar al siguiente nivel
    public void nextLevel() {
        int nextLevelIndex = (currentLevelIndex + 1) % levels.length;
        changeLevel(nextLevelIndex);
    }
    
    // Método para cambiar a un nivel específico
    public void changeLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.length) {
            currentLevelIndex = levelIndex;
            
            // Actualizar las tiles sin hitbox para este nivel
            MetodoAyuda.actualizarBloquesSinHitbox(currentLevelIndex);
            
            // Importar sprites del nuevo nivel
            importOutsideSprite(currentLevelIndex);
            
            // Actualizar variables globales
            Juego.NIVEL_ACTUAL_DATA = levels[currentLevelIndex].getLvlData();
            Juego.NIVEL_ACTUAL_ALTO = Juego.NIVEL_ACTUAL_DATA.length * Juego.TILES_SIZE;
            Juego.NIVEL_ACTUAL_ANCHO = Juego.NIVEL_ACTUAL_DATA[0].length * Juego.TILES_SIZE;
            
            // Cargar entidades para el nuevo nivel
            cargarEntidades(game);
            cargarDecoraciones();
            
            // Resetear jugador y otros elementos si es necesario
            // (esto puede variar según la implementación específica)
            
            System.out.println("Cambiado al nivel " + (currentLevelIndex + 1));
        }
    }
    
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }
    
    public int getTotalLevels() {
        return levels.length;
    }
}