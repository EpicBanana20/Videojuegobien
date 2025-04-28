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
        
        // Imprimir para debug
        System.out.println("Plataformas atravesables para nivel " + nivelIndex + ": " + plataformasAtravesables);
    }
    
    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData) {
        return CanMoveHere(x, y, width, height, lvlData, false);
    }
    
    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData, boolean quiereBajar) {
        if (!isSolido(x, y, lvlData, false, quiereBajar))
            if (!isSolido(x + width, y + height, lvlData, true, quiereBajar))
                if (!isSolido(x + width, y, lvlData, false, quiereBajar))
                    if (!isSolido(x, y + height, lvlData, true, quiereBajar))
                        return true;
        return false;
    }

    // Versión modificada de isSolido que considera la dirección del movimiento
    public static boolean isSolido(float x, float y, int[][] lvlData) {
        return isSolido(x, y, lvlData, false, false);
    }
    
    // Versión extendida para manejar plataformas atravesables
    public static boolean isSolido(float x, float y, int[][] lvlData, boolean movingDown, boolean quiereBajar) {
        int maxWidth = lvlData[0].length * Juego.TILES_SIZE;
        int maxHeight = lvlData.length * Juego.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= maxHeight)
            return true;
        int xIndex = (int) (x / Juego.TILES_SIZE);
        int yIndex = (int) (y / Juego.TILES_SIZE);
        int value = lvlData[yIndex][xIndex];
        
        // Si es una plataforma atravesable
        if (plataformasAtravesables.contains(value)) {
            // Si queremos bajar a través de la plataforma, no es sólida
            if (quiereBajar) {
                return false;
            }
            
            // Si estamos moviéndonos hacia abajo, es sólido (para pararse encima)
            // Si estamos moviéndonos hacia arriba, no es sólido (para atravesar desde abajo)
            return movingDown;
        }
        
        return !bloquesSinHitbox.contains(value);
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        return isEntityOnFloor(hitbox, lvlData, false);
    }
    
    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData, boolean quiereBajar) {
        // Posiciones a verificar para la colisión con el suelo
        float y = hitbox.y + hitbox.height + 1;
        
        // Varios puntos a lo ancho de la hitbox para mejorar detección
        int puntos = 3;
        float incrementoX = hitbox.width / (puntos - 1);
        
        for (int i = 0; i < puntos; i++) {
            float x = hitbox.x + (i * incrementoX);
            if (isSolido(x, y, lvlData, true, quiereBajar)) {
                return true;
            }
        }
        
        return false;
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
        return GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed, false);
    }
    
    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed, boolean quiereBajar) {
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
                        
                        // Verificar si este punto es una plataforma atravesable
                        int xIndex = (int) (xPos / Juego.TILES_SIZE);
                        if (xIndex < 0 || xIndex >= Juego.NIVEL_ACTUAL_DATA[0].length || 
                            tileY < 0 || tileY >= Juego.NIVEL_ACTUAL_DATA.length) {
                            continue;
                        }
                        
                        int tileValue = Juego.NIVEL_ACTUAL_DATA[tileY][xIndex];
                        
                        // Si es una plataforma atravesable y queremos bajar, ignorarla
                        if (plataformasAtravesables.contains(tileValue) && quiereBajar) {
                            continue;
                        }
                        
                        if (isSolido(xPos, tileY * Juego.TILES_SIZE, Juego.NIVEL_ACTUAL_DATA, true, quiereBajar)) {
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
                        if (isSolido(xPos, (tileY + 1) * Juego.TILES_SIZE - 1, Juego.NIVEL_ACTUAL_DATA, false, false)) {
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
    
    /**
     * Comprueba si la entidad está sobre una plataforma atravesable
     */
    public static boolean isEntityOnPlatform(Rectangle2D.Float hitbox, int[][] lvlData) {
        // Verificar el tile inmediatamente debajo de la hitbox
        float y = hitbox.y + hitbox.height + 1;
        
        // Varios puntos a lo ancho de la hitbox para mejorar detección
        int puntos = 3;
        float incrementoX = hitbox.width / (puntos - 1);
        
        for (int i = 0; i < puntos; i++) {
            float x = hitbox.x + (i * incrementoX);
            int xIndex = (int) (x / Juego.TILES_SIZE);
            int yIndex = (int) (y / Juego.TILES_SIZE);
            
            if (yIndex >= lvlData.length || xIndex < 0 || xIndex >= lvlData[0].length) {
                continue;
            }
            
            int value = lvlData[yIndex][xIndex];
            if (plataformasAtravesables.contains(value)) {
                return true;
            }
        }
        
        return false;
    }
}