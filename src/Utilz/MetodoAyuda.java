package Utilz;

import java.awt.geom.Rectangle2D;

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
        return value >= 49 || value < 0 || value != 47;
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
    
    /**
     * Aplica la gravedad a una entidad, manejando la caída y la colisión con el suelo.
     * 
     * @param hitbox Hitbox de la entidad
     * @param velocidadAire Velocidad actual en el aire [pasado por referencia como array de 1 elemento]
     * @param enAire Estado actual de la entidad (en aire o no) [pasado por referencia como array de 1 elemento]
     * @param lvlData Datos del nivel para detectar colisiones
     * @param tipoEntidad Tipo de entidad (ENTIDAD_JUGADOR, ENTIDAD_ENEMIGO, etc.)
     * @param gravedadPersonalizada Valor de gravedad personalizado (si es < 0, se usa el predeterminado)
     * @return La posición Y actualizada
     */
    public static float aplicarGravedad(
            Rectangle2D.Float hitbox, 
            float[] velocidadAire, 
            boolean[] enAire, 
            int[][] lvlData,
            float gravedadPersonalizada) {
        
        // Obtener el valor de gravedad según el tipo de entidad o usar el personalizado
        float gravedad;

        gravedad = gravedadPersonalizada;
        
        // Verificar si estamos en el suelo
        boolean enSuelo = isEntityOnFloor(hitbox, lvlData);
        
        if (!enSuelo) {
            enAire[0] = true;
        }
        
        // Aplicar gravedad si estamos en el aire
        if (enAire[0]) {
            velocidadAire[0] += gravedad;
            
            // Verificar si podemos movernos hacia abajo
            if (CanMoveHere(
                    hitbox.x, 
                    hitbox.y + velocidadAire[0], 
                    hitbox.width, 
                    hitbox.height, 
                    lvlData)) {
                
                hitbox.y += velocidadAire[0];
            } else {
                // Si hay colisión, ajustar posición y resetear velocidad aire
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, velocidadAire[0]);
                
                // Resetear valores
                if (velocidadAire[0] > 0) {
                    velocidadAire[0] = 0;
                    enAire[0] = false;
                } else {
                    // Si chocamos con el techo, cambiamos la dirección
                    velocidadAire[0] = 0.1f;
                }
            }
        }
        
        return hitbox.y;
    }
    
    /**
     * Mueve una entidad horizontalmente, manejando colisiones con paredes.
     * 
     * @param hitbox Hitbox de la entidad
     * @param velocidadX Velocidad horizontal actual
     * @param lvlData Datos del nivel para detectar colisiones
     * @return true si se movió correctamente, false si hubo colisión con pared
     */
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