package Elementos.Armas;

import Elementos.Arma;
import Elementos.Administradores.AdministradorBalas;
import Juegos.Juego;
import Elementos.AimController;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;

public class ArmaLaser extends Arma {
    private boolean disparando = false;
    private float energiaActual = 100f;
    private float energiaMaxima = 100f;
    private float consumoEnergia = 1.5f; // Consumo por frame
    private float recargaEnergia = 0.5f; // Recarga por frame
    
    // Propiedades del láser
    private float longitudMaxima = 500f * Juego.SCALE;
    private int dañoPorTick = 2; // Daño por frame
    private float[] puntoImpacto = new float[2];
    
    public ArmaLaser(AdministradorBalas adminBalas) {
        super("armas/laser.png", 30 * Juego.SCALE, 3.0f, adminBalas);
        this.nombre = "Laser";
        this.tipoDaño = "Luz"; // Hace más daño al BOSS2
    }
    
    @Override
    public void disparar() {
        if (energiaActual > 0) {
            disparando = true;
        }
    }
    
    // Método para detener el disparo
    public void detenerDisparo() {
        disparando = false;
    }
    
    @Override
    public void update(float playerX, float playerY, AimController aimController) {
        super.update(playerX, playerY, aimController);
        
        if (disparando && energiaActual > 0) {
            // Consumir energía
            energiaActual -= consumoEnergia;
            if (energiaActual <= 0) {
                energiaActual = 0;
                disparando = false;
            }
            
            // Calcular punto de impacto del láser
            calcularPuntoImpacto();
            
            // Hacer daño a enemigos en la línea del láser
            aplicarDañoLaser();
        } else {
            // Recargar energía cuando no está disparando
            if (energiaActual < energiaMaxima) {
                energiaActual += recargaEnergia;
                if (energiaActual > energiaMaxima) {
                    energiaActual = energiaMaxima;
                }
            }
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.render(g, xLvlOffset, yLvlOffset);
        
        if (disparando && energiaActual > 0) {
            renderizarLaser(g, xLvlOffset, yLvlOffset);
        }
        
        // Renderizar barra de energía
        renderizarBarraEnergia(g);
    }
    
    private void calcularPuntoImpacto() {
        // Posición inicial del láser
        float[] posicionDisparo = new float[2];
        float distanciaCañon = 20 * Juego.SCALE;
        
        AimController.getPositionAtDistance(
            x, y,
            distanciaCañon,
            rotacion,
            posicionDisparo
        );
        
        // Calcular punto de impacto (simplified for now)
        float distancia = longitudMaxima;
        AimController.getPositionAtDistance(
            posicionDisparo[0], 
            posicionDisparo[1],
            distancia,
            rotacion,
            puntoImpacto
        );
    }
    
    private void aplicarDañoLaser() {
        // TODO: Implementar detección de colisión con el rayo láser
        // Por ahora, simplificado para testing
    }
    
    private void renderizarLaser(Graphics g, int xLvlOffset, int yLvlOffset) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Guardar configuración original
        Color originalColor = g2d.getColor();
        Stroke originalStroke = g2d.getStroke();
        
        // Posición inicial del láser
        float[] posicionDisparo = new float[2];
        float distanciaCañon = 20 * Juego.SCALE;
        
        AimController.getPositionAtDistance(
            x, y,
            distanciaCañon,
            rotacion,
            posicionDisparo
        );
        
        int startX = (int)(posicionDisparo[0] - xLvlOffset);
        int startY = (int)(posicionDisparo[1] - yLvlOffset);
        int endX = (int)(puntoImpacto[0] - xLvlOffset);
        int endY = (int)(puntoImpacto[1] - yLvlOffset);
        
        // Dibujar el láser con efecto de brillo
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(new Color(255, 0, 0, 150)); // Rojo semi-transparente
        g2d.drawLine(startX, startY, endX, endY);
        
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 100, 100, 200)); // Centro más brillante
        g2d.drawLine(startX, startY, endX, endY);
        
        // Restaurar configuración
        g2d.setColor(originalColor);
        g2d.setStroke(originalStroke);
    }
    
    private void renderizarBarraEnergia(Graphics g) {
        // Barra de energía en la UI
        int barraX = 10;
        int barraY = 100;
        int barraAncho = 100;
        int barraAlto = 10;
        
        // Fondo de la barra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barraX, barraY, barraAncho, barraAlto);
        
        // Energía actual
        int energiaAncho = (int)(barraAncho * (energiaActual / energiaMaxima));
        g.setColor(Color.CYAN);
        g.fillRect(barraX, barraY, energiaAncho, barraAlto);
        
        // Borde
        g.setColor(Color.BLACK);
        g.drawRect(barraX, barraY, barraAncho, barraAlto);
    }
    
    public boolean estaDisparando() {
        return disparando;
    }
}   