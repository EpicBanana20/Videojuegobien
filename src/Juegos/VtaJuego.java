package Juegos;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;

public class VtaJuego extends JFrame {
    private boolean isFullScreen = false;
    
    public VtaJuego(PanelJuego n){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(n);
        
        // Configurar ventana maximizada sin bordes por defecto
        setUndecorated(true);
        isFullScreen = true;
        
        // Agregar escucha para la tecla F11 para alternar pantalla completa
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Salir de pantalla completa con ESC
                    if (isFullScreen) {
                        toggleFullScreen();
                    }
                }
            }
        });
        
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
    
    // Método para activar/desactivar la pantalla completa
    public void toggleFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (isFullScreen) {
            // Salir del modo pantalla completa
            dispose(); // Liberar recursos de la ventana
            setUndecorated(false); // Mostrar decoración de ventana
            setVisible(true);
            pack();
            isFullScreen = false;
        } else {
            // Entrar en modo pantalla completa
            dispose(); // Liberar recursos de la ventana
            setUndecorated(true); // Quitar decoración de ventana
            setVisible(true);
            
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(this);
            } else {
                // Si no se soporta fullscreen, usar maximizado sin bordes
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            isFullScreen = true;
        }
    }
    
    // Método para activar pantalla completa directamente
    public void setFullScreen(boolean fullscreen) {
        if (fullscreen != isFullScreen) {
            toggleFullScreen();
        }
    }
}