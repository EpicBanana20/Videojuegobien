package Elementos.Enemigos;

import java.awt.image.BufferedImage;

import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;

public class EnemigoVerde extends Enemigo {
    // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 32;
    private static final int ALTO_DEFAULT = 32;
    private static final int VIDA_DEFAULT = 50;
    
    public EnemigoVerde(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Ajustar el offset para este enemigo específico
        this.xDrawOffset = 8 * Juego.SCALE;
        this.yDrawOffset = 5 * Juego.SCALE;
        
        // Configurar hitbox específico para el enemigo verde
        initHitBox(x, y, 20 * Juego.SCALE, 14 * Juego.SCALE);
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    protected void determinarAnimacion() {
        // Si la animación actual es HERIDO y no ha terminado, no cambiar
        if (animaciones.getAccionActual() == HERIDO && !animaciones.esUltimoFrame()) {
            return;
        }
        
        // De lo contrario, determinar animación basada en el estado
        int nuevaAnimacion = INACTIVO; // Por defecto, estamos inactivos
        
        if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        }
        
        // Configuramos la nueva acción en el objeto de animaciones
        animaciones.setAccion(nuevaAnimacion);
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Cargar la hoja de sprites del enemigo verde
        BufferedImage img = LoadSave.GetSpriteAtlas("ENEMIGO1.png");
        
        // Basado en la imagen, tiene 3 filas (acciones) con varios frames cada una
        // Fila 1 (índice 0): 6 frames - Inactivo/Idle
        // Fila 2 (índice 1): 6 frames - Correr/Moverse
        // Fila 3 (índice 2): 2 frames - Daño/Herido
        spritesEnemigo = new BufferedImage[3][6]; // 3 acciones, máximo 6 frames
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 48;
        int frameHeight = 48;
        
        // Extraer cada frame de la hoja de sprites
        for (int j = 0; j < spritesEnemigo.length; j++) {
            int framesEnFila = (j == 2) ? 2 : 6; // La fila 3 solo tiene 2 frames
            for (int i = 0; i < framesEnFila; i++) {
                spritesEnemigo[j][i] = img.getSubimage(i * frameWidth, j * frameHeight, frameWidth, frameHeight);
            }
        }
        
        // Crear animaciones
        animaciones = new Animaciones(spritesEnemigo);
        
        // Configurar el número correcto de frames para cada animación
        animaciones.setNumFramesPorAnimacion(INACTIVO, 6); // 6 frames para inactivo/idle
        animaciones.setNumFramesPorAnimacion(CORRER, 6);   // 6 frames para correr/moverse
        animaciones.setNumFramesPorAnimacion(HERIDO, 2);   // 2 frames para herido
        
        // Establecer animación inicial
        animaciones.setAccion(INACTIVO);
    }
    
    // Métodos específicos para el comportamiento del enemigo verde
    
    // Método para implementar un comportamiento simple de patrulla
    public void iniciarPatrulla(float velocidad, float distanciaMaxima) {
        // Implementar lógica de patrulla (ir y venir)
        this.velocidadX = velocidad;
        // Código para cambiar dirección cuando llega a cierta distancia...
    }
    
    // Sobrescribir el método mover para incluir comportamiento específico
    @Override
    protected void mover() {
        // Podemos añadir comportamiento específico aquí
        super.mover(); // O llamar al comportamiento básico
    }
}