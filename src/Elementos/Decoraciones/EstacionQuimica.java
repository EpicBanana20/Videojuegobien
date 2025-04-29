package Elementos.Decoraciones;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import Juegos.Juego;
import Elementos.Quimica.SistemaQuimico;
import Elementos.Quimica.RecetaCompuesto;
import java.util.List;

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
        // Fondo semitransparente
        int panelX = (int)(x - 100) - xLvlOffset;
        int panelY = (int)(y - 150) - yLvlOffset;
        int panelAncho = 300;
        int panelAlto = 400;
        
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(panelX, panelY, panelAncho, panelAlto);
        
        g.setColor(Color.WHITE);
        g.drawRect(panelX, panelY, panelAncho, panelAlto);
        
        // Título
        String titulo = "Estación Química";
        g.drawString(titulo, panelX + panelAncho/2 - g.getFontMetrics().stringWidth(titulo)/2, panelY + 20);
        
        // Mostrar inventario de elementos
        g.drawString("Elementos:", panelX + 10, panelY + 50);
        int elementoY = panelY + 70;
        for (String simbolo : sistemaQuimico.getInventarioElementos().getElementos().keySet()) {
            int cantidad = sistemaQuimico.getInventarioElementos().getElemento(simbolo).getCantidad();
            g.drawString(simbolo + ": " + cantidad, panelX + 20, elementoY);
            elementoY += 20;
            
            // Solo mostrar los primeros 10 elementos para que no sea demasiado largo
            if (elementoY > panelY + 270) {
                g.drawString("...", panelX + 20, elementoY);
                break;
            }
        }
        
        // Mostrar recetas disponibles
        g.drawString("Recetas:", panelX + 150, panelY + 50);
        int recetaY = panelY + 70;
        List<RecetaCompuesto> recetas = sistemaQuimico.getRecetasDisponibles();
        for (RecetaCompuesto receta : recetas) {
            g.drawString(receta.getFormula(), panelX + 160, recetaY);
            recetaY += 20;
            
            // Solo mostrar las primeras 5 recetas
            if (recetaY > panelY + 170) {
                g.drawString("...", panelX + 160, recetaY);
                break;
            }
        }
        
        // Instrucciones
        g.drawString("Presiona números (1-5) para craftear", panelX + 20, panelY + 300);
        g.drawString("ESC para cerrar", panelX + 20, panelY + 320);
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