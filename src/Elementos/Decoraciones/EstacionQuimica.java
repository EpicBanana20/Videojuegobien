package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import Juegos.Juego;
import Elementos.Quimica.SistemaQuimico;
import Elementos.Quimica.RecetaCompuesto;
import java.util.List;
import java.util.Map;

public class EstacionQuimica extends Decoracion {
    private BufferedImage[] sprites; // Array para los dos estados (inactivo/activo)
    private int spriteActual = 0; // 0 = inactivo, 1 = activo
    private float distanciaInteraccion = 50f * Juego.SCALE;
    private boolean jugadorCerca = false;
    private boolean estacionAbierta = false;
    private SistemaQuimico sistemaQuimico;
    
    public EstacionQuimica(float x, float y, int width, int height, BufferedImage[] sprites) {
        super(x, y, width, height, sprites[0]);
        this.sprites = sprites;
        this.sistemaQuimico = Juego.jugadorActual != null ? Juego.jugadorActual.getSistemaQuimico() : null;
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
            
            // Actualizar referencia al sistema químico
            if (sistemaQuimico == null && Juego.jugadorActual.getSistemaQuimico() != null) {
                sistemaQuimico = Juego.jugadorActual.getSistemaQuimico();
            }
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        super.render(g, xLvlOffset, yLvlOffset);
        
        // Indicar visualmente que se puede interactuar
        if (jugadorCerca) {
            g.setColor(Color.WHITE);
            String mensaje = "Presiona E para interactuar";
            int textX = (int)(x + width/2 - g.getFontMetrics().stringWidth(mensaje)/2) - xLvlOffset;
            int textY = (int)(y - 20) - yLvlOffset;
            g.drawString(mensaje, textX, textY);
        }
        
        // Si la estación está abierta, mostrar interfaz
        if (estacionAbierta && sistemaQuimico != null) {
            renderizarInterfazCrafteo(g, xLvlOffset, yLvlOffset);
        }
    }
    
    private void renderizarInterfazCrafteo(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Centrar el panel en la pantalla en lugar de en la estación
        int panelX = Juego.GAME_WIDTH / 2 - 200;
        int panelY = Juego.GAME_HEIGHT / 2 - 250;
        int panelAncho = 400;
        int panelAlto = 500;
        
        // Fondo semitransparente
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(panelX, panelY, panelAncho, panelAlto);
        
        // Borde y título
        g.setColor(new Color(0, 150, 200));
        g.drawRect(panelX, panelY, panelAncho, panelAlto);
        
        // Título
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String titulo = "Estación Química";
        g.drawString(titulo, panelX + panelAncho/2 - g.getFontMetrics().stringWidth(titulo)/2, panelY + 30);
        
        // Mostrar inventario de elementos
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Elementos Disponibles:", panelX + 20, panelY + 70);
        
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int elementoY = panelY + 100;
        int columna = 0;
        
        for (String simbolo : sistemaQuimico.getInventarioElementos().getElementos().keySet()) {
            int cantidad = sistemaQuimico.getInventarioElementos().getElemento(simbolo).getCantidad();
            if (cantidad > 0) { // Solo mostrar elementos que tenemos
                int xPos = panelX + 30 + (columna * 100);
                g.drawString(simbolo + ": " + cantidad, xPos, elementoY);
                columna++;
                if (columna > 2) {
                    columna = 0;
                    elementoY += 25;
                }
            }
        }
        
        // Separador
        g.setColor(new Color(100, 100, 100));
        g.drawLine(panelX + 20, panelY + 180, panelX + panelAncho - 20, panelY + 180);
        
        // Recetas disponibles
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Compuestos que puedes crear:", panelX + 20, panelY + 210);
        
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int recetaY = panelY + 240;
        List<RecetaCompuesto> recetas = sistemaQuimico.getRecetasDisponibles();
        int recetaNum = 1;
        
        for (RecetaCompuesto receta : recetas) {
            boolean puedeCrear = receta.puedeCrearCon(sistemaQuimico.getInventarioElementos());
            
            // Color verde si puede crear, gris si no
            if (puedeCrear) g.setColor(new Color(100, 255, 100));
            else g.setColor(new Color(150, 150, 150));
            
            g.drawString(recetaNum + ". " + receta.getNombre() + " (" + receta.getFormula() + ")", 
                panelX + 30, recetaY);
            
            // Mostrar ingredientes
            String ingredientes = "   Requiere: ";
            for (Map.Entry<String, Integer> elem : receta.getElementos().entrySet()) {
                ingredientes += elem.getKey() + ":" + elem.getValue() + " ";
            }
            g.drawString(ingredientes, panelX + 40, recetaY + 20);
            
            recetaY += 50;
            recetaNum++;
            
            // Limitar número de recetas mostradas
            if (recetaNum > 5) break;
        }
        
        // Instrucciones
        g.setColor(new Color(255, 255, 150));
        g.drawString("Presiona 1-5 para crear el compuesto correspondiente", panelX + 30, panelY + 420);
        g.drawString("ESC para cerrar", panelX + 30, panelY + 450);
        
        // Mostrar últimos compuestos creados
        g.setColor(new Color(150, 255, 255));
        g.drawString("Compuestos en inventario:", panelX + 30, panelY + 480);
        
        // Aquí podrías mostrar algunos de los compuestos que ya tienes
    }
    
    public void interactuar() {
        if (jugadorCerca) {
            estacionAbierta = !estacionAbierta;
        }
    }
    
    public boolean procesarTecla(int keyCode) {
        if (!estacionAbierta) return false;
        
        // Teclas del 1 al 5 (49-53) para crear compuestos
        if (keyCode >= 49 && keyCode <= 53) {
            int index = keyCode - 49;
            List<RecetaCompuesto> recetas = sistemaQuimico.getRecetasDisponibles();
            if (index < recetas.size()) {
                String formula = recetas.get(index).getFormula();
                return sistemaQuimico.crearCompuesto(formula);
            }
        }
        
        // ESC para cerrar
        if (keyCode == 27) {
            estacionAbierta = false;
            return true;
        }
        
        return false;
    }
    
    public boolean isJugadorCerca() {
        return jugadorCerca;
    }
    
    public boolean isEstacionAbierta() {
        return estacionAbierta;
    }
}