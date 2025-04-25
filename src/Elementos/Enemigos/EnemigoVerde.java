package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;

public class EnemigoVerde extends Enemigo {
    // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 96;
    private static final int ALTO_DEFAULT = 64;
    private static final int VIDA_DEFAULT = 50;
    
    // Ajuste específico para este enemigo
    private int ajuste = 20;
    
    public EnemigoVerde(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Configurar propiedades específicas
        inicializarEnemigo(16, 16, 48, 48, true, true);
        this.velocidadMovimiento = 0.5f * Juego.SCALE;
        this.velocidadX = -velocidadMovimiento; // Iniciar moviéndose a la izquierda
        
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
        
        if (enAire) {
            // Si tuviéramos una animación de salto/caída, la usaríamos aquí
            // Por ahora, seguimos usando CORRER o INACTIVO dependiendo de la velocidad
            if (velocidadX != 0) {
                nuevaAnimacion = CORRER;
            }
        } else if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        }
        
        // Configuramos la nueva acción en el objeto de animaciones
        animaciones.setAccion(nuevaAnimacion);
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Cargar la hoja de sprites del enemigo verde
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/ENEMIGO1.png");
        
        // Basado en la imagen, tiene 3 filas (acciones) con varios frames cada una
        // Fila 1 (índice 0): 6 frames - Inactivo/Idle
        // Fila 2 (índice 1): 6 frames - Correr/Moverse
        // Fila 3 (índice 2): 2 frames - Daño/Herido
        spritesEnemigo = new BufferedImage[3][6]; // 3 acciones, máximo 6 frames
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 48;
        int frameHeight = 32;
        
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
        animaciones.setAccion(CORRER);  // Comenzamos en animación de correr ya que estará en movimiento
    }
    
    // Sobrescribir renderizado para aplicar el ajuste específico
    @Override
    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
        
        boolean voltearHorizontal = invertirOrientacion ? !movimientoHaciaIzquierda : movimientoHaciaIzquierda;
        
        if (voltearHorizontal) {
            // Dibujar volteado horizontalmente con ajuste
            g.drawImage(animaciones.getImagenActual(),
                drawX + w - ajuste, drawY,
                -w, h, null);
        } else {
            // Dibujar normal
            g.drawImage(animaciones.getImagenActual(),
                drawX, drawY,
                w, h, null);
        }
    }
}