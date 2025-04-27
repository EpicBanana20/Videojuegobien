package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Jugador;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;

public class EnemigoThor extends Enemigo{
     // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 96;
    private static final int ALTO_DEFAULT = 72;
    private static final int VIDA_DEFAULT = 50;

    private boolean disparoEnProceso = false;
    private int frameDisparo = 2; // El disparo ocurrirá en el tercer frame (0,1,2,3)
    private boolean disparoPendiente = false;
    private float anguloDisparo = 0;
    
    // Ajuste específico para este enemigo
    private int ajuste = 20;
    
    public EnemigoThor(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Configurar propiedades específicas
        inicializarEnemigo(12, 35, 72, 36, true, true);
        this.velocidadMovimiento = 0.5f * Juego.SCALE;
        this.velocidadX = -velocidadMovimiento; // Iniciar moviéndose a la izquierda
        this.checkOffset = 20 * Juego.SCALE; // Ajustar el offset de verificación para el salto

        this.puedeDisparar = true;
        this.disparoMaxCooldown = 180; // Cada 3 segundos
        this.rangoDeteccionJugador = 400 * Juego.SCALE; // Mayor rango
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    protected void determinarAnimacion() {
        // Si estamos disparando, priorizar esa animación
        if (disparoEnProceso) {
            if (!animaciones.esUltimoFrame()) {
                return;
            } else {
                disparoEnProceso = false;
            }
        }
        
        // Si recibimos daño, mostrar animación de daño
        if (animaciones.getAccionActual() == HERIDO && !animaciones.esUltimoFrame()) {
            return;
        }
        
        // Determinar animación por estado
        int nuevaAnimacion;
        
        if (enAire) {
            nuevaAnimacion = CORRER;
        } else if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        } else {
            nuevaAnimacion = INACTIVO; // Cuando está quieto
        }
        
        animaciones.setAccion(nuevaAnimacion);
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Cargar la hoja de sprites del enemigo verde
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/Thor 72x72.png");
        
        // Basado en la imagen, tiene 3 filas (acciones) con varios frames cada una
        // Fila 1 (índice 0): 6 frames - Inactivo/Idle
        // Fila 2 (índice 1): 6 frames - Correr/Moverse
        // Fila 3 (índice 2): 2 frames - Daño/Herido
        spritesEnemigo = new BufferedImage[4][4]; // 3 acciones, máximo 6 frames
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 72;
        int frameHeight = 72;
        
        // Extraer cada frame de la hoja de sprites
        for (int j = 0; j < spritesEnemigo.length; j++) {
            int framesEnFila = (j == 2) ? 2 : 4; // La fila 3 solo tiene 2 frames
            for (int i = 0; i < framesEnFila; i++) {
                spritesEnemigo[j][i] = img.getSubimage(i * frameWidth, j * frameHeight, frameWidth, frameHeight);
            }
        }
        
        // Crear animaciones
        animaciones = new Animaciones(spritesEnemigo);
        
        // Configurar el número correcto de frames para cada animación
        animaciones.setNumFramesPorAnimacion(INACTIVO, 4); // 6 frames para inactivo/idle
        animaciones.setNumFramesPorAnimacion(CORRER, 4);   // 6 frames para correr/moverse
        animaciones.setNumFramesPorAnimacion(HERIDO, 2);   // 2 frames para herido
        animaciones.setNumFramesPorAnimacion(DISPARO, 4);   // 2 frames para disparo
        
        // Establecer animación inicial
        animaciones.setAccion(CORRER);  // Comenzamos en animación de correr ya que estará en movimiento
    }
    
    // Sobrescribir renderizado para aplicar el ajuste específico
    @Override
    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
        
        if (movimientoHaciaIzquierda) {
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

    @Override
    protected void disparar(float angulo) {
        // En lugar de crear la bala inmediatamente, iniciamos la animación
        disparoEnProceso = true;
        disparoPendiente = true;
        anguloDisparo = angulo;
        animaciones.setAccion(DISPARO);
        animaciones.resetearAnimacion();
        
        // Detenemos temporalmente el movimiento durante el disparo
        float velocidadOriginal = velocidadX;
        velocidadX = 0;
        
        // Programamos un timer para restaurar la velocidad cuando termine la animación
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    velocidadX = velocidadOriginal;
                }
            },
            animaciones.getNumFramesPorAnimacion(DISPARO) * (animaciones.getAnimVelocidad() * 16)
        );
    }

    @Override
    protected void manejarDisparo(Jugador jugador) {
        if (!puedeDisparar || !activo) return;
        
        // Reducir cooldown si está activo
        if (disparoCooldown > 0) {
            disparoCooldown--;
            return;
        }
        
        // Verificar si el jugador está en rango, SIN importar si estamos quietos
        if (puedeVerJugador(jugador)) {
            // Detener movimiento temporalmente para disparar
            float velocidadOriginal = velocidadX;
            patrullando = false;
            velocidadX = 0;
            
            // Disparar
            float angulo = calcularAnguloHaciaJugador(jugador);
            disparar(angulo);
            disparoCooldown = disparoMaxCooldown;
            
            // Reanudar movimiento después de un tiempo
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        patrullando = true;
                        velocidadX = velocidadOriginal;
                    }
                },
                1000 // Reanudar después de 1 segundo
            );
        }
    }

    @Override
    public void update() {
        super.update();
        
        // Verificar si es momento de crear la bala durante la animación
        if (disparoPendiente && disparoEnProceso && 
            animaciones.getAccionActual() == DISPARO && 
            animaciones.getAnimIndice() == frameDisparo) {
            
            // Calcular posición de origen de la bala
            float origenX = hitbox.x + hitbox.width/2;
            float origenY = hitbox.y + hitbox.height/2;
            
            // Ajustar el origen según la dirección
            if (movimientoHaciaIzquierda) {
                origenX -= 20 * Juego.SCALE;
            } else {
                origenX += 20 * Juego.SCALE;
            }
            
            // Crear la bala real
            Bala nuevaBala = new Bala(
                origenX, 
                origenY, 
                anguloDisparo,
                LoadSave.BULLET_ENEMY,
                4, // Daño enemigo
                1.8f // Velocidad
            );
            adminBalas.agregarBala(nuevaBala);
            
            // Ya disparamos, no repetir hasta la próxima animación
            disparoPendiente = false;
        }
        
        // Intentar detectar al jugador y disparar
        if (!disparoEnProceso && Juego.jugadorActual != null) {
            manejarDisparo(Juego.jugadorActual);
        }
    }
}
