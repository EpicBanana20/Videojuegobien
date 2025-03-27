package Utilz;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import Juegos.Juego;

public class MetodoAyuda {
    // Constantes para los tipos de entidades
    public static final int ENTIDAD_JUGADOR = 0;
    public static final int ENTIDAD_ENEMIGO = 1;
    public static final int ENTIDAD_OBJETO = 2;
    
    // Valores predeterminados de gravedad por tipo de entidad
    
    public static boolean CanMoveHere(float x, float y,
            float width, float height, int[][] lvlData) {
        if (!isSolido(x, y, lvlData))
            if (!isSolido(x + width, y + height, lvlData))
                if (!isSolido(x + width, y, lvlData))
                    if (!isSolido(x, y + height, lvlData))
                        return true;
        return false;
    }

    private static final Set<Integer> bloquesSinHitbox = Set.of(58);
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

    public static float GetEntityYPosUnderRoofOrAboveFloor(
            Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / Juego.TILES_SIZE);
        if(airSpeed>0){
            int tileYPos = currentTile * Juego.TILES_SIZE;
            int yOffset = (int) (Juego.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else
            return currentTile * Juego.TILES_SIZE;
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
     * 
     * @param hitbox Hitbox de la entidad
     * @param lvlData Datos del nivel para detectar colisiones
     * @param distancia Distancia a comprobar (positiva para derecha, negativa para izquierda)
     * @return true si hay suelo, false si no lo hay
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
     * 
     * @param hitbox Hitbox de la entidad
     * @param lvlData Datos del nivel para detectar colisiones
     * @param distancia Distancia a comprobar (positiva para derecha, negativa para izquierda)
     * @return true si hay pared, false si no la hay
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
}