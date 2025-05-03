package Menus;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Juegos.EstadoJuego;
import Juegos.Juego;
import Utilz.LoadSave;

public class Menu {
    private Juego juego;
    private BufferedImage backgroundImg;
    private Boton[] botones = new Boton[4];
    private BufferedImage[][] botonesImgs;
    
    // Constantes para posiciones de botones
    private final int BOTON_SPACING = 150;
    private final int ALTURA_PRIMER_BOTON = 300;
    private final int BOTON_X_POSICION = 400;
    
    // Índices de botones
    private static final int BOTON_JUGAR = 0;
    private static final int BOTON_OPCIONES = 1;
    private static final int BOTON_LOGROS = 2;
    private static final int BOTON_SALIR = 3;

    public Menu(Juego juego) {
        this.juego = juego;
        cargarImagenes();
        cargarBotones();
    }

    private void cargarImagenes() {
        // Cargar fondo del menú
        backgroundImg = LoadSave.GetSpriteAtlas("FONDOMENU.png");
        
        // Cargar sprites de botones
        BufferedImage botonesSprite = LoadSave.GetSpriteAtlas("Botones 40x25.png");
        botonesImgs = new BufferedImage[4][3];
        
        for (int j = 0; j < botonesImgs.length; j++) {
            for (int i = 0; i < botonesImgs[j].length; i++) {
                botonesImgs[j][i] = botonesSprite.getSubimage(
                    i * 40, j * 25, 40, 25);
            }
        }
    }

    private void cargarBotones() {
        
        for (int i = 0; i < botones.length; i++) {
            botones[i] = new Boton(
                BOTON_X_POSICION, 
                ALTURA_PRIMER_BOTON + i * BOTON_SPACING, 
                i, 
                botonesImgs[i]
            );
        }
    }

    public void update() {
        for (Boton b : botones)
            b.update();
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        
        // Dibujar botones
        for (Boton b : botones)
            b.draw(g);
    }

    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < botones.length; i++) {
            if (estaDentroBoton(e, botones[i])) {
                botones[i].setMousePressed(true);
                break;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        for (int i = 0; i < botones.length; i++) {
            if (estaDentroBoton(e, botones[i])) {
                if (botones[i].isMousePressed()) {
                    // Acciones según el botón presionado
                    switch (i) {
                        case BOTON_JUGAR:
                            juego.setEstadoJuego(EstadoJuego.PLAYING);
                            break;
                        case BOTON_OPCIONES:
                            // TODO: Implementar menú de opciones
                            System.out.println("Opciones - Por implementar");
                            break;
                        case BOTON_LOGROS:
                            // TODO: Implementar menú de LOGROS
                            System.out.println("Créditos - Por implementar");
                            break;
                        case BOTON_SALIR:
                            System.exit(0);
                            break;
                    }
                }
                break;
            }
        }
        
        resetBotones();
    }

    public void mouseMoved(MouseEvent e) {
        for (Boton b : botones)
            b.setMouseOver(false);
        
        for (Boton b : botones) {
            if (estaDentroBoton(e, b)) {
                b.setMouseOver(true);
                break;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            juego.setEstadoJuego(EstadoJuego.PLAYING);
    }

    private void resetBotones() {
        for (Boton b : botones)
            b.resetBools();
    }
    
    private boolean estaDentroBoton(MouseEvent e, Boton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }
}