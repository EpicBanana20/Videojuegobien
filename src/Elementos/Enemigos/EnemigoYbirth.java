package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Jugador;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;

public class EnemigoYbirth extends Enemigo{
     // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 120;
    private static final int ALTO_DEFAULT = 90;
    private static final int VIDA_DEFAULT = 50;
    private int INACTIVO = 0;
    private int DISPARO = 1;
    private int HERIDO = 2;

    private boolean disparoEnProceso = false;
    private int frameDisparo = 5; // El disparo ocurrirá en el tercer frame (0,1,2,3)
    private boolean disparoPendiente = false;
    private float anguloDisparo = 0;
    
    // Ajuste específico para este enemigo
    private int ajuste = -75;
    
    public EnemigoYbirth(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Configurar propiedades específicas
        inicializarEnemigo(0, 0, 50, 90, true, true);
        this.velocidadMovimiento = 0f * Juego.SCALE;
        this.velocidadX = -velocidadMovimiento; // Iniciar moviéndose a la izquierda
        this.checkOffset = 20 * Juego.SCALE; // Ajustar el offset de verificación para el salto
        this.patrullando = false;
        this.puedeDisparar = true;
        this.disparoMaxCooldown = 30; // Cada 3 segundos
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
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/Ybirth 226x120.png");
        spritesEnemigo = new BufferedImage[3][7]; 
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 226;
        int frameHeight = 120;
        int framesEnFila;
        
        // Extraer cada frame de la hoja de sprites
        for (int j = 0; j < spritesEnemigo.length; j++) {
            switch (j) {
                case 0:
                    framesEnFila = 5;
                    break;
                case 1:
                    framesEnFila = 7;
                    break;
                default:
                    framesEnFila = 2;
                    break;
            }
            for (int i = 0; i < framesEnFila; i++) {
                spritesEnemigo[j][i] = img.getSubimage(i * frameWidth, j * frameHeight, frameWidth, frameHeight);
            }
        }
        
        // Crear animaciones
        animaciones = new Animaciones(spritesEnemigo);
        
        // Configurar el número correcto de frames para cada animación
        animaciones.setNumFramesPorAnimacion(INACTIVO, 5); // 6 frames para inactivo/idle   // 6 frames para correr/moverse
        animaciones.setNumFramesPorAnimacion(HERIDO, 2);   // 2 frames para herido
        animaciones.setNumFramesPorAnimacion(DISPARO, 7);   // 2 frames para disparo
        
        // Establecer animación inicial
        animaciones.setAccion(INACTIVO);  // Comenzamos en animación de correr ya que estará en movimiento
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
                drawX - ajuste, drawY,
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
        // Iniciar animación
        disparoEnProceso = true;
        disparoPendiente = true;
        anguloDisparo = angulo;
        animaciones.setAccion(DISPARO);
        animaciones.resetearAnimacion();
        
        // Asegurar que no se mueve
        velocidadX = 0;
        
        // Eliminar el timer
    }

    @Override
    protected void manejarDisparo(Jugador jugador) {
        if (!puedeDisparar || !activo) return;
        
        // Reducir cooldown si está activo
        if (disparoCooldown > 0) {
            disparoCooldown--;
            return;
        }
        
        // Verificar si el jugador está en rango
        if (puedeVerJugador(jugador)) {
            // Mantener patrullando en false y velocidad en 0
            patrullando = false;
            velocidadX = 0;
            float jugadorX = jugador.getXCenter();
            float enemigoX = hitbox.x + hitbox.width/2;
            movimientoHaciaIzquierda = jugadorX < enemigoX;
            
            // Disparar
            float angulo = calcularAnguloHaciaJugador(jugador);
            disparar(angulo);
            disparoCooldown = disparoMaxCooldown;
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
                LoadSave.BULLET_YIBIRTH,
                4, // Daño enemigo
                1.2f // Velocidad
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
