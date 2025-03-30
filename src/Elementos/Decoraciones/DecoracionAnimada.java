package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import Utilz.Animaciones;

public class DecoracionAnimada extends Decoracion {
    private Animaciones animaciones;
    private BufferedImage[][] animationFrames;
    private int tipoAnimacion;
    
    public DecoracionAnimada(float x, float y, int width, int height, 
                            BufferedImage[][] sprites, int tipoAnimacion, 
                            int[] framesPorAnimacion) {
        super(x, y, width, height, null); // La imagen sprite será manejada por Animaciones
        this.animada = true;
        this.animationFrames = sprites;
        this.tipoAnimacion = tipoAnimacion;
        
        // Inicializar animaciones
        animaciones = new Animaciones(sprites);
        
        // Configurar el número de frames para cada tipo de animación
        if (framesPorAnimacion != null) {
            for (int i = 0; i < framesPorAnimacion.length; i++) {
                animaciones.setNumFramesPorAnimacion(i, framesPorAnimacion[i]);
            }
        }
        
        // Establecer la animación inicial
        animaciones.setAccion(tipoAnimacion);
    }
    
    @Override
    public void update() {
        // Actualizar la animación
        animaciones.actualizarAnimacion();
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Solo dibujar si está en pantalla
        if (isOnScreen(xLvlOffset, yLvlOffset)) {
            BufferedImage currentFrame = animaciones.getImagenActual();
            if (currentFrame != null) {
                g.drawImage(currentFrame, 
                    (int)(x - xLvlOffset), 
                    (int)(y - yLvlOffset), 
                    width, height, null);
            }
        }
    }
    
    // Métodos adicionales para controlar la animación
    public void cambiarAnimacion(int nuevaAnimacion) {
        if (nuevaAnimacion != tipoAnimacion) {
            tipoAnimacion = nuevaAnimacion;
            animaciones.setAccion(nuevaAnimacion);
        }
    }
    
    public void setVelocidadAnimacion(int velocidad) {
        animaciones.setAnimVelocidad(velocidad);
    }
    
    public int getTipoAnimacion() {
        return tipoAnimacion;
    }
}