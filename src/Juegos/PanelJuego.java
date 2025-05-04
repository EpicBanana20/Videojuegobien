package Juegos;

import javax.swing.JPanel;

import Eventos.EventoMouse;
import Eventos.EventoTeclado;
import Eventos.EventosNivel;

import java.awt.*;

public class PanelJuego extends JPanel {
    private EventoMouse ev;
    private EventoTeclado et;
    private EventosNivel en;
    Juego game;

    public PanelJuego(Juego game) {
        ev = new EventoMouse(this);
        et = new EventoTeclado(this);
        en = new EventosNivel(game);
        this.game = game;
        setPanelSize();
        addKeyListener(et);
        addKeyListener(en); // Agregar el listener para cambio de niveles
        addMouseListener(ev);
        addMouseMotionListener(ev);
    }

    private void setPanelSize() {
        Dimension size=new Dimension(1920,1080);
        this.setPreferredSize(size);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Juego game = getGame();
        game.render(g);
        dibujarBarraVida(g);
        
        // Dibujar información del nivel actual (opcional)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Nivel: " + (game.getLevelManager().getCurrentLevelIndex() + 1) + 
                     "/" + game.getLevelManager().getTotalLevels(), 20, 30);
        g.drawString("Usa F1-F4 para cambiar de nivel, F5 para siguiente nivel", 20, 50);
    }

    public Juego getGame() {
        return game;
    }
    
    void updateGame() {
        // Actualiza la información del mouse en el jugador antes de la actualización general
        game.updateMouseInfo(ev.getMouseX(), ev.getMouseY());
        // La actualización normal del juego continúa en el método update() de la clase Juego
    }

    private void dibujarBarraVida(Graphics g) {
        int barraX = 20;
        int barraY = 70;
        int barraAncho = 200;
        int barraAlto = 20;
        
        // Fondo de la barra
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barraX, barraY, barraAncho, barraAlto);
        
        // Vida actual
        float porcentajeVida = game.getPlayer().getVidaActual() / 
                               game.getPlayer().getVidaMaxima();
        int vidaAncho = (int)(barraAncho * porcentajeVida);
        
        // Color según porcentaje
        if (porcentajeVida > 0.6f)
            g.setColor(Color.GREEN);
        else if (porcentajeVida > 0.3f)
            g.setColor(Color.YELLOW);
        else
            g.setColor(Color.RED);
        
        g.fillRect(barraX, barraY, vidaAncho, barraAlto);
        
        // Borde
        g.setColor(Color.BLACK);
        g.drawRect(barraX, barraY, barraAncho, barraAlto);
        
        // Texto
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Vida: " + (int)game.getPlayer().getVidaActual() + 
                     "/" + (int)game.getPlayer().getVidaMaxima(), 
                     barraX + 5, barraY + 15);
    }
}