package Utilz;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import Juegos.Juego;

public class MetodoAyuda {
    // Constantes para los tipos de entidades
    public static final int ENTIDAD_JUGADOR = 0;
    public static final int ENTIDAD_ENEMIGO = 1;
    public static final int ENTIDAD_OBJETO = 2;
    
    // Set de bloques sin hitbox (se irá actualizando según el nivel actual)
    private static Set<Integer> bloquesSinHitbox = new HashSet<>();
    private static Set<Integer> plataformasAtravesables = new HashSet<>();
    // Inicializar con el bloque de aire del nivel 1
    static {
        // Añadir el bloque de aire del nivel 1 por defecto
        bloquesSinHitbox.add(58);
    }
    
    // Método para actualizar los bloques sin hitbox según el nivel
    public static void actualizarBloquesSinHitbox(int nivelIndex) {
        bloquesSinHitbox.clear();
        plataformasAtravesables.clear();
        
        // Añadir bloque de aire
        bloquesSinHitbox.add(LoadSave.LEVEL_INFO[nivelIndex][1]);
        
        // Añadir tiles sin hitbox
        for (int tileId : LoadSave.TILES_SIN_HITBOX[nivelIndex]) {
            bloquesSinHitbox.add(tileId);
        }
        
        // Añadir plataformas atravesables
        for (int tileId : LoadSave.TILES_PLATAFORMAS[nivelIndex]) {
            plataformasAtravesables.add(tileId);
        }
    }
    
    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData) {
        if (!isSolido(x, y, lvlData))
            if (!isSolido(x + width, y + height, lvlData))
                if (!isSolido(x + width, y, lvlData))
                    if (!isSolido(x, y + height, lvlData))
                        return true;
        return false;
    }

    public static boolean isSolido(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Juego.TILES_SIZE;
        int maxHeight = lvlData.length * Juego.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= maxHeight)
            return true;
        int xIndex = (int) (x / Juego.TILES_SIZE);
        int yIndex = (int) (y / Juego.TILES_SIZE);
        int value = lvlData[yIndex][xIndex];
        return !bloquesSinHitbox.contains(value);
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        return isSolido(hitbox.x, hitbox.y + hitbox.height + 1, lvlData) || 
               isSolido(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData);
    }

    public static float GetEntityXPosNexttoWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Juego.TILES_SIZE);
        if (xSpeed > 0) {
            int tileXPos = currentTile * Juego.TILES_SIZE;
            int xOffset = (int) (Juego.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        } else
            return currentTile * Juego.TILES_SIZE;
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        if (airSpeed > 0) {
            // CAYENDO - Detectar suelo
            float maxPosY = hitbox.y + hitbox.height + airSpeed;
            int startTileY = (int)((hitbox.y + hitbox.height) / Juego.TILES_SIZE);
            int endTileY = (int)(maxPosY / Juego.TILES_SIZE);
            
            // Si cruzamos algún tile nuevo
            if (startTileY != endTileY) {
                // Verificar cada tile en la trayectoria
                for (int tileY = startTileY + 1; tileY <= endTileY; tileY++) {
                    // Revisar múltiples puntos a lo ancho de la hitbox
                    int puntos = Math.max(3, (int)(hitbox.width / Juego.TILES_SIZE) + 1);
                    float incrementoX = hitbox.width / (puntos - 1);
                    
                    for (int i = 0; i < puntos; i++) {
                        float xPos = hitbox.x + (i * incrementoX);
                        if (isSolido(xPos, tileY * Juego.TILES_SIZE, Juego.NIVEL_ACTUAL_DATA)) {
                            // Encontramos suelo - posicionar justo encima
                            return tileY * Juego.TILES_SIZE - hitbox.height - 1;
                        }
                    }
                }
            }
        } else {
            // SALTANDO - Detectar techo
            float minPosY = hitbox.y + airSpeed;
            int startTileY = (int)(hitbox.y / Juego.TILES_SIZE);
            int endTileY = (int)(minPosY / Juego.TILES_SIZE);
            
            // Si cruzamos algún tile nuevo
            if (startTileY != endTileY) {
                // Verificar cada tile en la trayectoria
                for (int tileY = startTileY - 1; tileY >= endTileY; tileY--) {
                    // Revisar múltiples puntos a lo ancho de la hitbox
                    int puntos = Math.max(3, (int)(hitbox.width / Juego.TILES_SIZE) + 1);
                    float incrementoX = hitbox.width / (puntos - 1);
                    
                    for (int i = 0; i < puntos; i++) {
                        float xPos = hitbox.x + (i * incrementoX);
                        if (isSolido(xPos, (tileY + 1) * Juego.TILES_SIZE - 1, Juego.NIVEL_ACTUAL_DATA)) {
                            // Encontramos techo - posicionar justo debajo
                            return (tileY + 1) * Juego.TILES_SIZE;
                        }
                    }
                }
            }
        }
        
        // Si no hay colisiones, continuar el movimiento
        return hitbox.y + airSpeed;
    }
    
    public static boolean moverHorizontal(
            Rectangle2D.Float hitbox, 
            float velocidadX, 
            int[][] lvlData) {
        
        // Verificar si podemos movernos horizontalmente
        if (CanMoveHere(
                hitbox.x + velocidadX, 
                hitbox.y, 
                hitbox.width, 
                hitbox.height, 
                lvlData)) {
            
            hitbox.x += velocidadX;
            return true; // Movimiento exitoso
        } else {
            // Si hay colisión, ajustar posición
            hitbox.x = GetEntityXPosNexttoWall(hitbox, velocidadX);
            return false; // Hubo colisión
        }
    }
    
    /**
     * Comprueba si hay suelo en una dirección específica a cierta distancia
     */
    public static boolean haySueloAdelante(
            Rectangle2D.Float hitbox,
            int[][] lvlData,
            float distancia) {
        
        float checkX = distancia > 0 ? 
                hitbox.x + hitbox.width + distancia : 
                hitbox.x + distancia;
                
        return isSolido(checkX, hitbox.y + hitbox.height + 1, lvlData);
    }
    
    /**
     * Comprueba si hay pared en una dirección específica a cierta distancia
     */
    public static boolean hayParedAdelante(
            Rectangle2D.Float hitbox,
            int[][] lvlData,
            float distancia) {
        
        float checkX = distancia > 0 ? 
                hitbox.x + hitbox.width + distancia : 
                hitbox.x + distancia;
                
        return isSolido(checkX, hitbox.y + hitbox.height / 2, lvlData);
    }

    public static boolean esPlataformaAtravesable(int tileValue) {
        return plataformasAtravesables.contains(tileValue);
    }
}