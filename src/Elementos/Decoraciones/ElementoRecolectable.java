package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import Juegos.Juego;

public class ElementoRecolectable extends Decoracion {
    private String simbolo;
    private float distanciaInteraccion = 30f * Juego.SCALE;
    private boolean jugadorCerca = false;
    private boolean activo = true;
    
    public ElementoRecolectable(float x, float y, int width, int height, BufferedImage sprite, String simbolo) {
        super(x, y, width, height, sprite);
        this.simbolo = simbolo;
    }
    
    @Override
    public void update() {
        if (!activo) return;
        
        // Verificar si el jugador está cerca
        if (Juego.jugadorActual != null) {
            float playerX = Juego.jugadorActual.getXCenter();
            float playerY = Juego.jugadorActual.getYCenter();
            
            float elementoX = x + width/2;
            float elementoY = y + height/2;
            
            float distX = elementoX - playerX;
            float distY = elementoY - playerY;
            float distancia = (float) Math.sqrt(distX * distX + distY * distY);
            
            jugadorCerca = distancia <= distanciaInteraccion;
            
            // Si el jugador está cerca y presiona la tecla de interacción
            if (jugadorCerca) {
                // Auto-recolectar si el jugador está suficientemente cerca
                recolectar();
            }
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        super.render(g, xLvlOffset, yLvlOffset);
        
        if (jugadorCerca) {
            g.setColor(Color.WHITE);
            String mensaje = simbolo;
            int textX = (int)(x + width/2 - g.getFontMetrics().stringWidth(mensaje)/2) - xLvlOffset;
            int textY = (int)(y - 10) - yLvlOffset;
            g.drawString(mensaje, textX, textY);
        }
    }
    
    private void recolectar() {
        if (activo && Juego.jugadorActual != null && Juego.jugadorActual.getSistemaQuimico() != null) {
            // Añadir el elemento al inventario del jugador
            Juego.jugadorActual.getSistemaQuimico().getInventarioElementos().recolectarElemento(simbolo, 1);
            activo = false;
            
            // Mostrar un mensaje o efecto de recolección
            System.out.println("¡Elemento " + simbolo + " recolectado!");
        }
    }
    
    public String getSimbolo() {
        return simbolo;
    }
    
    public boolean isActivo() {
        return activo;
    }
}