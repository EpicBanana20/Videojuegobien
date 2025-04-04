package Eventos;

import java.awt.event.*;

import Juegos.PanelJuego;

public class EventoTeclado implements KeyListener {
    private PanelJuego pan;

    public EventoTeclado(PanelJuego pan) {
        this.pan = pan;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // pan.setMoving(true);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                pan.getGame().getPlayer().setUp(true);
                break;
            case KeyEvent.VK_S:
                pan.getGame().getPlayer().setDown(true);
                break;
            case KeyEvent.VK_A:
                pan.getGame().getPlayer().setLeft(true);
                break;
            case KeyEvent.VK_D:
                pan.getGame().getPlayer().setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                pan.getGame().getPlayer().setJump(true);
                break;
            case KeyEvent.VK_Q:
                pan.getGame().getPlayer().cambiarArma();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                pan.getGame().getPlayer().setUp(false);
                break;
            case KeyEvent.VK_S:
                pan.getGame().getPlayer().setDown(false);
                break;
            case KeyEvent.VK_A:
                pan.getGame().getPlayer().setLeft(false);
                break;
            case KeyEvent.VK_D:
                pan.getGame().getPlayer().setRight(false);
                break;
                case KeyEvent.VK_SPACE:
                pan.getGame().getPlayer().setJump(false);;
                break;
        }
    }

}
