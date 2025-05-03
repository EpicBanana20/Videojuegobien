package Menus;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import Juegos.EstadoJuego;
import Juegos.Juego;
import Utilz.LoadSave;

public class SelectorPersonajes {
    private Juego juego;
    private BufferedImage backgroundImg;
    private BufferedImage[] personajeMarcos;
    
    // Estados de selección
    private int personajeSeleccionado = 0;
    private static final int TOTAL_PERSONAJES = 3;
    
    // Animación y posicionamiento
    private float posicionActualY = 0;
    private float posicionObjetivoY = 0;
    private boolean enTransicion = false;
    private float velocidadTransicion = 12f;
    
    // Constantes de dimensiones
    private static final int MARCO_WIDTH = 601;
    private static final int MARCO_HEIGHT = 972;
    private static final int MARGEN_VERTICAL = 50;
    
    public SelectorPersonajes(Juego juego) {
        this.juego = juego;
        cargarImagenes();
    }
    
    private void cargarImagenes() {
        // Cargar fondo del selector (puedes reutilizar el del menú o crear uno nuevo)
        backgroundImg = LoadSave.GetSpriteAtlas("FONDOMENU.png");
        
        // Cargar marcos de personajes
        personajeMarcos = new BufferedImage[TOTAL_PERSONAJES];
        
        // TODO: Actualizar con las rutas correctas de las imágenes
        personajeMarcos[0] = LoadSave.GetSpriteAtlas("marcos/EclipsaMarco.png");
        personajeMarcos[1] = LoadSave.GetSpriteAtlas("marcos/HalanMarco.png");
        personajeMarcos[2] = LoadSave.GetSpriteAtlas("marcos/ValthorMarco.png");
    }
    
    public void update() {
        if (enTransicion) {
            // Animación suave hacia la posición objetivo
            float diferencia = posicionObjetivoY - posicionActualY;
            
            if (Math.abs(diferencia) < 0.5f) {
                posicionActualY = posicionObjetivoY;
                enTransicion = false;
            } else {
                posicionActualY += diferencia * velocidadTransicion / 100f;
            }
        }
    }
    
    public void draw(Graphics g) {
        // Dibujar fondo
        g.drawImage(backgroundImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        
        // Calcular centro de la pantalla
        int centerX = Juego.GAME_WIDTH / 2 - MARCO_WIDTH / 2;
        int centerY = Juego.GAME_HEIGHT / 2 - MARCO_HEIGHT / 2;
        
        // Dibujar los personajes con efecto de ciclo
        for (int i = -1; i <= TOTAL_PERSONAJES; i++) {
            int indicePersonaje = (personajeSeleccionado + i + TOTAL_PERSONAJES) % TOTAL_PERSONAJES;
            
            // Calcular posición Y con offset de animación
            int posY = centerY + i * (MARCO_HEIGHT + MARGEN_VERTICAL) + (int)posicionActualY;
            
            if (posY + MARCO_HEIGHT > -100 && posY < Juego.GAME_HEIGHT + 100) {
                g.drawImage(personajeMarcos[indicePersonaje], centerX, posY, 
                          MARCO_WIDTH, MARCO_HEIGHT, null);
            }
        }
    }
    
    public void keyPressed(KeyEvent e) {
        if (enTransicion) return; // Ignorar inputs durante transición
        
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                seleccionarAnterior();
                break;
                
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                seleccionarSiguiente();
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                iniciarJuegoConPersonaje();
                break;
                
            case KeyEvent.VK_ESCAPE:
                juego.setEstadoJuego(EstadoJuego.MENU);
                break;
        }
    }
    
    private void seleccionarSiguiente() {
        personajeSeleccionado = (personajeSeleccionado + 1) % TOTAL_PERSONAJES;
        posicionObjetivoY -= (MARCO_HEIGHT + MARGEN_VERTICAL);
        enTransicion = true;
    }
    
    private void seleccionarAnterior() {
        personajeSeleccionado = (personajeSeleccionado - 1 + TOTAL_PERSONAJES) % TOTAL_PERSONAJES;
        posicionObjetivoY += (MARCO_HEIGHT + MARGEN_VERTICAL);
        enTransicion = true;
    }
    
    private void iniciarJuegoConPersonaje() {
        // TODO: Configurar el jugador según el personaje seleccionado
        // Por ahora solo cambiamos al estado PLAYING
        juego.setEstadoJuego(EstadoJuego.PLAYING);
    }
    
    // Para eventos del mouse (si quieres soporte de mouse)
    public void mouseMoved(int x, int y) {
        // Por ahora no implementado
    }
    
    public void mousePressed(int x, int y) {
        // Por ahora no implementado
    }
    
    public void mouseReleased(int x, int y) {
        // Por ahora no implementado
    }
    
    public int getPersonajeSeleccionado() {
        return personajeSeleccionado;
    }
}