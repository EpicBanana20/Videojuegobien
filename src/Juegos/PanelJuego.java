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
}