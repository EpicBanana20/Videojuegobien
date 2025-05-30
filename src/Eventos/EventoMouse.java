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
                else if(e.getButton()==MouseEvent.BUTTON3)
                    pan.getGame().getPlayer().usarHabilidadEspecial();
                break;
            case OPCIONES:
                //TODO:. Implementar menú de opciones
                break;
            case LOGROS:
                //TODO: Implementar menú de LOGROS
                break;
            case PAUSA:
                    pan.getGame().getMenuPausa().mousePressed(e);
                break;
            case MUERTE:
                pan.getGame().getMenuMuerte().mousePressed(e);
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
                pan.getGame().getMenuPausa().mouseReleased(e);
            break;
        case MUERTE:
            pan.getGame().getMenuMuerte().mouseReleased(e);
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
            case SELECCION_PERSONAJE:
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
                    pan.getGame().getMenuPausa().mouseMoved(e);
                break;
            case MUERTE:
                pan.getGame().getMenuMuerte().mouseMoved(e);
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
