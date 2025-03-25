package Juegos;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class VtaJuego extends JFrame {
    
    public VtaJuego(PanelJuego n){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(n);
        
        // Configurar ventana maximizada sin bordes por defecto
        setUndecorated(true);
        
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                n.getGame().windowFocusLost();
            }
        });
        
        // Primero hacemos visible la ventana
        this.setVisible(true);
        // Luego maximizamos
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}