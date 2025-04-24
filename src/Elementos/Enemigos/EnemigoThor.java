package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;

public class EnemigoThor extends Enemigo {
    // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 96;
    private static final int ALTO_DEFAULT = 72;
    private static final int VIDA_DEFAULT = 50;
    
    // Variables para el comportamiento de patrulla
    private boolean movimientoHaciaIzquierda = false;
    private float velocidadMovimiento = 0.5f * Juego.SCALE;
    private boolean patrullando = true;
    private int ajuste = -23;
    
    
    // Variable para comprobar si hay suelo
    private float checkOffset = 15 * Juego.SCALE; // Distancia para comprobar suelo delante
    
    
    public EnemigoThor(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Ajustar el offset para este enemigo específico
        this.xDrawOffset = 24 * Juego.SCALE;
        this.yDrawOffset = 29 * Juego.SCALE;
        
        // Configurar hitbox específico para el enemigo verde
        initHitBox(x, y, 72 * Juego.SCALE, 40 * Juego.SCALE);
        
        // Inicializar velocidad (negativa para moverse a la izquierda al inicio)
        this.velocidadX = -velocidadMovimiento;
        movimientoHaciaIzquierda = true;
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    public void update() {
        if (!activo) return;
        
        aplicarGravedad();
        if (!enAire && patrullando) {
            patrullar();
        }
        mover();
        
        if (animaciones != null) {
            animaciones.actualizarAnimacion();
            determinarAnimacion();
        }
    }
    
    @Override
    protected void aplicarGravedad() {
        // Verificar primero si estamos en el suelo
        boolean enSuelo = MetodoAyuda.isEntityOnFloor(hitbox, Juego.NIVEL_ACTUAL_DATA);
        
        if (enSuelo) {
            enAire = false;
            velocidadAire = 0;  // Asegurarnos de resetear la velocidad de aire
        } else {
            enAire = true;
        }
        
        // Aplicar gravedad SOLO si estamos en el aire
        if (enAire) {
            // Gravedad personalizada para este enemigo
            float gravedadPersonalizada = 0.03f * Juego.SCALE;
            
            velocidadAire += gravedadPersonalizada;
            
            // Verificar si podemos movernos hacia abajo
            if (MetodoAyuda.CanMoveHere(
                    hitbox.x, 
                    hitbox.y + velocidadAire, 
                    hitbox.width, 
                    hitbox.height, 
                    Juego.NIVEL_ACTUAL_DATA)) {
                
                hitbox.y += velocidadAire;
            } else {
                // Si hay colisión, ajustar posición y resetear velocidad aire
                hitbox.y = MetodoAyuda.GetEntityYPosUnderRoofOrAboveFloor(hitbox, velocidadAire);
                
                // Resetear valores
                if (velocidadAire > 0) {
                    // Tocando el suelo
                    velocidadAire = 0;
                    enAire = false;
                } else {
                    // Chocando con el techo
                    velocidadAire = 0.1f;
                }
            }
        }
        
        // Actualizar la coordenada y
        y = hitbox.y;
    }
    
    private void patrullar() {
        // Usar los nuevos métodos centralizados para comprobar pared y suelo
        boolean hayPared = MetodoAyuda.hayParedAdelante(
            hitbox, 
            Juego.NIVEL_ACTUAL_DATA, 
            movimientoHaciaIzquierda ? -checkOffset : checkOffset
        );
        
        boolean haySueloAdelante = MetodoAyuda.haySueloAdelante(
            hitbox, 
            Juego.NIVEL_ACTUAL_DATA, 
            movimientoHaciaIzquierda ? -checkOffset : checkOffset
        );
        
        // Si hay una pared adelante o no hay suelo, cambiar dirección
        if (hayPared || !haySueloAdelante) {
            cambiarDireccion();
        }
    }
    
    private void cambiarDireccion() {
        movimientoHaciaIzquierda = !movimientoHaciaIzquierda;
        velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
    }
    
    @Override
    protected void mover() {
        // Usar el método centralizado para mover horizontalmente
        boolean movimientoExitoso = MetodoAyuda.moverHorizontal(
            hitbox, 
            velocidadX, 
            Juego.NIVEL_ACTUAL_DATA
        );
        
        // Actualizar la posición x
        x = hitbox.x;
        
        // Si hubo colisión, cambiar de dirección
        if (!movimientoExitoso) {
            cambiarDireccion();
        }
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
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/Thor 72x72.png");

        spritesEnemigo = new BufferedImage[4][4]; // 3 acciones, máximo 6 frames
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 72;
        int frameHeight = 72;
        
        // Extraer cada frame de la hoja de sprites
        for (int j = 0; j < spritesEnemigo.length; j++) {
             int framesEnFila = (j == 3) ? 2 : 4;
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
        animaciones.setNumFramesPorAnimacion(DISPARO, 4);
        
        // Establecer animación inicial
        animaciones.setAccion(CORRER);  // Comenzamos en animación de correr ya que estará en movimiento
    }
    
    // Métodos para configurar el comportamiento
    public void setPatrullando(boolean patrullando) {
        this.patrullando = patrullando;
        if (!patrullando) {
            velocidadX = 0;
        } else if (velocidadX == 0) {
            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
        }
    }
    
    public void setVelocidadMovimiento(float velocidad) {
        this.velocidadMovimiento = velocidad;
        // Actualizar la velocidad actual manteniendo la dirección
        if (velocidadX != 0) {
            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
        }
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        // Guardar flip horizontal basado en dirección
        boolean flipX = !movimientoHaciaIzquierda;
        
        // Dibujar enemigo con posible flip horizontal
        if (animaciones != null) {
            // Calcular posición con offset
            int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
            int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
            
            if (flipX) {
                // Dibujar volteado horizontalmente
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
        
        // Para debugging
        drawHitBox(g, xLvlOffset, yLvlOffset);
    }
}