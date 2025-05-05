package Menus;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Juegos.EstadoJuego;
import Juegos.Juego;
import Utilz.LoadSave;

public class MenuPausa {
    private Juego juego;
    private Boton[] botones = new Boton[3];
    private BufferedImage[][] botonesImgs;
    
    // Constantes para posiciones de botones
    private final int BOTON_SPACING = 200;
    private final int ALTURA_PRIMER_BOTON = 450;
    private final int BOTON_X_POSICION = Juego.GAME_WIDTH / 2;
    
    // Índices de botones en el menú de pausa
    private static final int BOTON_REINTENTAR = 0;
    private static final int BOTON_OPCIONES = 1;
    private static final int BOTON_MENU = 2;
    
    // Índices reales en el sprite sheet
    private static final int SPRITE_REINTENTAR = 4;
    private static final int SPRITE_OPCIONES = 1;
    private static final int SPRITE_MENU = 5;

    private BufferedImage pausaImg;

    public MenuPausa(Juego juego) {
        this.juego = juego;
        cargarImagenes();
        cargarBotones();
    }

    private void cargarImagenes() {
        pausaImg = LoadSave.GetSpriteAtlas("PAUSA.png");
        // Cargar sprites de botones (ahora 6 filas, 40x25 cada uno)
        BufferedImage botonesSprite = LoadSave.GetSpriteAtlas("Botones 40x25.png");
        botonesImgs = new BufferedImage[6][3];
        
        for (int j = 0; j < botonesImgs.length; j++) {
            for (int i = 0; i < botonesImgs[j].length; i++) {
                botonesImgs[j][i] = botonesSprite.getSubimage(
                    i * 40, j * 25, 40, 25);
            }
        }
    }

    private void cargarBotones() {
        // Botón Reintentar
        botones[BOTON_REINTENTAR] = new Boton(
            BOTON_X_POSICION, 
            ALTURA_PRIMER_BOTON, 
            BOTON_REINTENTAR,  // Cambiar de SPRITE_REINTENTAR a BOTON_REINTENTAR
            botonesImgs[SPRITE_REINTENTAR]
        );
        
        // Botón Opciones
        botones[BOTON_OPCIONES] = new Boton(
            BOTON_X_POSICION, 
            ALTURA_PRIMER_BOTON + BOTON_SPACING, 
            BOTON_OPCIONES,  // Cambiar de SPRITE_OPCIONES a BOTON_OPCIONES
            botonesImgs[SPRITE_OPCIONES]
        );
        
        // Botón Menú
        botones[BOTON_MENU] = new Boton(
            BOTON_X_POSICION, 
            ALTURA_PRIMER_BOTON + BOTON_SPACING * 2, 
            BOTON_MENU,  // Cambiar de SPRITE_MENU a BOTON_MENU
            botonesImgs[SPRITE_MENU]
        );
    }

    public void update() {
        for (Boton b : botones)
            b.update();
    }

    public void draw(Graphics g) {
        // Dibujar fondo semitransparente oscuro
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        
        // Dibujar imagen de pausa (centrada horizontalmente y arriba de los botones)
        int pausaX = Juego.GAME_WIDTH / 2 - pausaImg.getWidth() / 2;
        int pausaY = ALTURA_PRIMER_BOTON - 400; // 100 píxeles arriba del primer botón
        g.drawImage(pausaImg, pausaX, pausaY, null);
        
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
                        case BOTON_REINTENTAR:
                            juego.reiniciarJuego();
                            juego.setEstadoJuego(EstadoJuego.PLAYING);
                            break;
                        case BOTON_OPCIONES:
                            // TODO: Implementar menú de opciones
                            System.out.println("Opciones - Por implementar");
                            break;
                        case BOTON_MENU:
                            juego.setEstadoJuego(EstadoJuego.MENU);
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