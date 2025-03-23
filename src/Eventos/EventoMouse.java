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
         if(e.getButton()==MouseEvent.BUTTON1)
            pan.getGame().getPlayer().setAttacking(true);
       }

    @Override
    public void mouseReleased(MouseEvent e) {
      if(e.getButton()==MouseEvent.BUTTON1)
         pan.getGame().getPlayer().setAttacking(false);

     }

     @Override
     public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
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
