package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;

public class EnemigoVerde extends Enemigo {
    // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 96;
    private static final int ALTO_DEFAULT = 64;
    private static final int VIDA_DEFAULT = 50;
    
    // Variables para el comportamiento de patrulla
    private boolean movimientoHaciaIzquierda = false;
    private float velocidadMovimiento = 0.5f * Juego.SCALE;
    private float distanciaRecorrida = 0;
    private float distanciaMaxima = 100 * Juego.SCALE; // Distancia máxima antes de cambiar dirección
    private boolean patrullando = true;
    
    // Variable para comprobar si hay suelo
    private float checkOffset = 15 * Juego.SCALE; // Distancia para comprobar suelo delante
    
    public EnemigoVerde(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Ajustar el offset para este enemigo específico
        this.xDrawOffset = 16 * Juego.SCALE;
        this.yDrawOffset = 16 * Juego.SCALE;
        
        // Configurar hitbox específico para el enemigo verde
        initHitBox(x, y, 48 * Juego.SCALE, 48 * Juego.SCALE);
        
        // Inicializar velocidad (negativa para moverse a la izquierda al inicio)
        this.velocidadX = -velocidadMovimiento;
        movimientoHaciaIzquierda = true;
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    public void update() {
        if (!activo) return;
        
        // Aplicar gravedad primero
        aplicarGravedad();
        
        // Comportamiento de patrulla solo si estamos en el suelo
        if (!enAire && patrullando) {
            patrullar();
        }
        
        // Actualizar posición basado en velocidad actual
        mover();
        
        // Actualizar animaciones
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
        
        // Actualizar distancia recorrida
        distanciaRecorrida += Math.abs(velocidadX);
        
        // Si hemos recorrido la distancia máxima de patrulla, cambiar dirección
        if (distanciaRecorrida >= distanciaMaxima) {
            cambiarDireccion();
            distanciaRecorrida = 0;
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
    
    public void setDistanciaMaxima(float distancia) {
        this.distanciaMaxima = distancia;
    }
    
    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        // Guardar flip horizontal basado en dirección
        boolean flipX = movimientoHaciaIzquierda;
        
        // Dibujar enemigo con posible flip horizontal
        if (animaciones != null) {
            // Calcular posición con offset
            int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
            int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
            
            if (flipX) {
                // Dibujar volteado horizontalmente
                g.drawImage(animaciones.getImagenActual(),
                    drawX + w, drawY,
                    -w, h, null);
            } else {
                // Dibujar normal
                g.drawImage(animaciones.getImagenActual(),
                    drawX, drawY,
                    w, h, null);
            }
        }
        
        // Para debugging
        // drawHitBox(g, xLvlOffset, yLvlOffset);
    }
}