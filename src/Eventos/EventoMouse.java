package Eventos;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Juegos.PanelJuego;

public class EventoMouse extends MouseAdapter{
    private PanelJuego pan;
    private int mouseX, mouseY;
    

    public EventoMouse(PanelJuego pan) {
        this.pan = pan;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (pan.getGame().getEstadoJuego()) {
            case MENU:
                pan.getGame().getMenu().mousePressed(e);
                break;
            case SELECCION_PERSONAJE:
                // Por ahora solo usa teclado
                break;
            case PLAYING:
                if(e.getButton()==MouseEvent.BUTTON1)
                    pan.getGame().getPlayer().setAttacking(true);
                break;
            case OPCIONES:
                //TODO:. Implementar menú de opciones
                break;
            case LOGROS:
                //TODO: Implementar menú de LOGROS
                break;
            case PAUSA:
                //TODO: Implementar menú de PAUSA
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    switch (pan.getGame().getEstadoJuego()) {
        case MENU:
            pan.getGame().getMenu().mouseReleased(e);
            break;
        case SELECCION_PERSONAJE:
            // Por ahora solo usa teclado
            break;
        case PLAYING:
            if(e.getButton()==MouseEvent.BUTTON1)
                pan.getGame().getPlayer().setAttacking(false);
            break;
        case OPCIONES:
            //TODO:. Implementar menú de opciones
            break;
        case LOGROS:
            //TODO: Implementar menú de LOGROS
            break;
        case PAUSA:
            //TODO: Implementar menú de PAUSA
            break;
    }
}

     @Override
     public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        switch (pan.getGame().getEstadoJuego()) {
            case MENU:
                pan.getGame().getMenu().mouseMoved(e);
                break;
            case PLAYING:
                break;
                case OPCIONES:
                //TODO:. Implementar menú de opciones
                break;
            case LOGROS:
                //TODO: Implementar menú de LOGROS
                break;
            case PAUSA:
                //todo: Implementar menú de PAUSA
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public int getMouseX() {
        return mouseX;
    }
    public int getMouseY() {
        return mouseY;
    }
}
