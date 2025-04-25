package Utilz;

import java.awt.image.BufferedImage;

/**
 * Clase genérica para manejar animaciones en el juego.
 * Esta clase puede ser utilizada por cualquier entidad que necesite animaciones.
 */
public class Animaciones {
    // Atributos para manejar las animaciones
    private BufferedImage[][] sprites;
    private int animIndice = 0;
    private int animTick = 0;
    private int animVelocidad = 15;
    private int accionActual = 0;
    private int[] spritesPorAccion; // Número de sprites para cada acción
    
    // Constructor para inicializar con sprites y cantidad de sprites por acción
    public Animaciones(BufferedImage[][] sprites, int[] spritesPorAccion) {
        this.sprites = sprites;
        this.spritesPorAccion = spritesPorAccion;
    }
    
    // Constructor que recibe los sprites y permite especificar manualmente cuántos sprites usar en cada animación
    public Animaciones(BufferedImage[][] sprites) {
        this.sprites = sprites;
        // Inicializamos con la cantidad máxima de sprites disponibles por animación
        this.spritesPorAccion = new int[sprites.length];
        for (int i = 0; i < sprites.length; i++) {
            this.spritesPorAccion[i] = sprites[i].length;
        }
    }
    
    // Método para establecer manualmente la cantidad de frames por animación
    public void setNumFramesPorAnimacion(int animacionID, int numFrames) {
        if (animacionID >= 0 && animacionID < spritesPorAccion.length) {
            // Aseguramos que no se exceda el número de frames disponibles
            int maxFrames = sprites[animacionID].length;
            if (numFrames > 0 && numFrames <= maxFrames) {
                spritesPorAccion[animacionID] = numFrames;
            }
        }
    }
    
    // Método para configurar todos los frames de una vez
    public void setTodosLosFrames(int[] framesPorAnimacion) {
        if (framesPorAnimacion != null && framesPorAnimacion.length <= spritesPorAccion.length) {
            for (int i = 0; i < framesPorAnimacion.length; i++) {
                setNumFramesPorAnimacion(i, framesPorAnimacion[i]);
            }
        }
    }
    public int getNumFramesPorAnimacion(int accion) {
        if (accion >= 0 && accion < spritesPorAccion.length) {
            return spritesPorAccion[accion];
        }
        return 1;
    }
    // Método para actualizar la animación actual
    public void actualizarAnimacion() {
        animTick++;
        if (animTick >= animVelocidad) {
            animTick = 0;
            animIndice++;
            if (animIndice >= getSpritesPorAccion(accionActual)) {
                animIndice = 0;
                // Aquí podríamos añadir un callback para notificar que la animación terminó
            }
        }
    }
    
    // Método para obtener la cantidad de sprites para una acción
    private int getSpritesPorAccion(int accion) {
        if (accion >= 0 && accion < spritesPorAccion.length) {
            return spritesPorAccion[accion];
        }
        return 1; // Por defecto, al menos 1 sprite
    }
    
    // Método para cambiar directamente a una animación específica
    public void setAccion(int nuevaAccion) {
        if (nuevaAccion != accionActual) {
            accionActual = nuevaAccion;
            resetearAnimacion();
        }
    }
    
    // Método para obtener la imagen actual de la animación
    public BufferedImage getImagenActual() {
        if (accionActual < sprites.length && animIndice < sprites[accionActual].length) {
            return sprites[accionActual][animIndice];
        }
        return null; // Por seguridad, devolvemos null si no hay imagen
    }
    
    // Método para resetear la animación
    public void resetearAnimacion() {
        animIndice = 0;
        animTick = 0;
    }
    
    // Método para verificar si la animación actual ha terminado de reproducirse
    public boolean animacionTerminada() {
        return animIndice == 0 && animTick == 0;
    }
    
    // Método para verificar si la animación está en el último frame
    public boolean esUltimoFrame() {
        return animIndice == getSpritesPorAccion(accionActual) - 1;
    }
    
    // Getters y setters
    public int getAccionActual() {
        return accionActual;
    }
    
    public int getAnimIndice() {
        return animIndice;
    }
    
    public void setAnimVelocidad(int velocidad) {
        this.animVelocidad = velocidad;
    }
    
    public int getAnimVelocidad() {
        return animVelocidad;
    }
    
    public void setSprites(BufferedImage[][] sprites) {
        this.sprites = sprites;
    }
    
    public BufferedImage[][] getSprites() {
        return sprites;
    }
}