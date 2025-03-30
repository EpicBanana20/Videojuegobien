package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import Juegos.Juego;

public abstract class Decoracion {
    protected float x, y;
    protected int width, height;
    protected BufferedImage sprite;
    protected boolean animada;
    
    public Decoracion(float x, float y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.animada = false;
    }
    
    // Método para actualizar la decoración (importante para animaciones)
    public void update() {
        // Este método será sobrescrito por subclases que necesiten actualización
    }
    
    // Método para renderizar la decoración
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Solo dibujar si está en pantalla o cerca (para optimizar)
        if (isOnScreen(xLvlOffset, yLvlOffset)) {
            g.drawImage(sprite, 
                (int)(x - xLvlOffset), 
                (int)(y - yLvlOffset), 
                width, height, null);
        }
    }
    
    // Método para comprobar si la decoración está en pantalla
    protected boolean isOnScreen(int xLvlOffset, int yLvlOffset) {
        return (x + width >= xLvlOffset && 
                x <= xLvlOffset + Juego.GAME_WIDTH && 
                y + height >= yLvlOffset && 
                y <= yLvlOffset + Juego.GAME_HEIGHT);
    }
    
    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isAnimada() { return animada; }
}