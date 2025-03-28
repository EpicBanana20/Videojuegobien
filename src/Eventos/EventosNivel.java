package Eventos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Juegos.Juego;

/**
 * Clase para manejar eventos específicos de cambio de nivel
 */
public class EventosNivel implements KeyListener {
    private Juego juego;
    
    public EventosNivel(Juego juego) {
        this.juego = juego;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Cambiar niveles con las teclas F1, F2, F3, F4
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F1:
                juego.cambiarNivel(0);
                break;
            case KeyEvent.VK_F2:
                juego.cambiarNivel(1);
                break;
            case KeyEvent.VK_F3:
                juego.cambiarNivel(2);
                break;
            case KeyEvent.VK_F4:
                juego.cambiarNivel(3);
                break;
            case KeyEvent.VK_F5:
                // Siguiente nivel
                juego.siguienteNivel();
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    }
}