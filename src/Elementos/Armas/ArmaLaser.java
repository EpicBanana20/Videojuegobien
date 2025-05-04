package Elementos.Armas;

import Elementos.Arma;
import Elementos.Enemigo;
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
    private float alcanceReal = 0f; // Distancia hasta el punto de impacto
    private Enemigo enemigoImpactado = null; // Enemigo que está siendo impactado
    private boolean intentandoDisparar = false; // Si el usuario mantiene presionado
    
    public ArmaLaser(AdministradorBalas adminBalas) {
        super("armas/laser.png", 30 * Juego.SCALE, 3.0f, adminBalas);
        this.nombre = "Laser";
        this.tipoDaño = "Luz"; // Hace más daño al BOSS2
    }
    
    @Override
    public void disparar() {
        intentandoDisparar = true;
        if (energiaActual > 0) {
            disparando = true;
            calcularPuntoImpacto();
        }
    }
    
    // Método para detener el disparo
    public void detenerDisparo() {
        intentandoDisparar = false;
        disparando = false;
        puntoImpacto[0] = x;
        puntoImpacto[1] = y;
        alcanceReal = 0f;
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
            if (energiaActual < energiaMaxima && !intentandoDisparar) {
                energiaActual += recargaEnergia;
                if (energiaActual > energiaMaxima) {
                    energiaActual = energiaMaxima;
                }
            }
        }
        
        // Si el usuario sigue intentando disparar pero no tiene energía, evitar reactivación
        if (intentandoDisparar && energiaActual <= 0) {
            disparando = false;
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.render(g, xLvlOffset, yLvlOffset);
        
        if (disparando && energiaActual > 0 && alcanceReal > 0) {
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
        
        // Buscar colisión con bloques o enemigos
        float paso = 10 * Juego.SCALE; // Resolución de búsqueda
        float distanciaActual = 0;
        enemigoImpactado = null;
        
        while (distanciaActual < longitudMaxima) {
            // Calcular posición actual del rayo
            float[] posActual = new float[2];
            AimController.getPositionAtDistance(
                posicionDisparo[0], 
                posicionDisparo[1],
                distanciaActual,
                rotacion,
                posActual
            );
            
            // Verificar colisión con bloques
            if (Utilz.MetodoAyuda.isSolido(posActual[0], posActual[1], Juego.NIVEL_ACTUAL_DATA)) {
                puntoImpacto[0] = posActual[0];
                puntoImpacto[1] = posActual[1];
                alcanceReal = distanciaActual;
                return;
            }
            
            // Verificar colisión con enemigos
            if (Juego.ADMIN_ENEMIGOS != null) {
                for (Enemigo enemigo : Juego.ADMIN_ENEMIGOS.getEnemigos()) {
                    if (enemigo.estaActivo() && enemigo.getHitBox().contains(posActual[0], posActual[1])) {
                        puntoImpacto[0] = posActual[0];
                        puntoImpacto[1] = posActual[1];
                        alcanceReal = distanciaActual;
                        enemigoImpactado = enemigo;
                        return;
                    }
                }
            }
            
            distanciaActual += paso;
        }
        
        // Si no hay colisión, usar distancia máxima
        AimController.getPositionAtDistance(
            posicionDisparo[0], 
            posicionDisparo[1],
            longitudMaxima,
            rotacion,
            puntoImpacto
        );
        alcanceReal = longitudMaxima;
    }
    
    private void aplicarDañoLaser() {
        if (enemigoImpactado != null && enemigoImpactado.estaActivo()) {
            enemigoImpactado.recibirDaño(dañoPorTick, tipoDaño);
        }
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
        
        // Efecto de impacto
        if (enemigoImpactado != null) {
            g2d.setColor(new Color(255, 150, 150, 200));
            g2d.fillOval(endX - 5, endY - 5, 10, 10);
        }
        
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