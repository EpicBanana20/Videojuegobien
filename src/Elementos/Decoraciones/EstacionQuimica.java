package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import Juegos.Juego;

public class EstacionQuimica extends Decoracion {
    private BufferedImage[] sprites; // Array para los dos estados (inactivo/activo)
    private int spriteActual = 0; // 0 = inactivo, 1 = activo
    private float distanciaInteraccion = 50f * Juego.SCALE;
    private boolean jugadorCerca = false;
    
    public EstacionQuimica(float x, float y, int width, int height, BufferedImage[] sprites) {
        super(x, y, width, height, sprites[0]);
        this.sprites = sprites;
    }
    
    @Override
    public void update() {
        // Verificar si el jugador está cerca para poder interactuar
        if (Juego.jugadorActual != null) {
            float playerX = Juego.jugadorActual.getXCenter();
            float playerY = Juego.jugadorActual.getYCenter();
            
            float estacionX = x + width/2;
            float estacionY = y + height/2;
            
            float distX = estacionX - playerX;
            float distY = estacionY - playerY;
            float distancia = (float) Math.sqrt(distX * distX + distY * distY);
            
            // Actualizar estado según distancia
            jugadorCerca = distancia <= distanciaInteraccion;
            spriteActual = jugadorCerca ? 1 : 0;
            sprite = sprites[spriteActual];
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.render(g, xLvlOffset, yLvlOffset);
        
        // Opcional: Indicar visualmente que se puede interactuar
        if (jugadorCerca) {
            g.setColor(Color.WHITE);
            String mensaje = "Presiona E para interactuar";
            int textX = (int)(x + width/2 - g.getFontMetrics().stringWidth(mensaje)/2) - xLvlOffset;
            int textY = (int)(y - 20) - yLvlOffset;
            g.drawString(mensaje, textX, textY);
        }
    }
    
    public boolean isJugadorCerca() {
        return jugadorCerca;
    }
}