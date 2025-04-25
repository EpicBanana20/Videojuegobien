package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import Juegos.Juego;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;

public abstract class Enemigo extends Cascaron {
    protected Animaciones animaciones;
    protected BufferedImage[][] spritesEnemigo;
    protected int accionActual = 0; 
    protected int vida;
    protected int vidaMaxima;
    protected boolean activo = true;
    protected float xDrawOffset;
    protected float yDrawOffset;
    protected float velocidadX;
    protected float velocidadY;
    protected float velocidadAire = 0;
    protected boolean enAire = false;

    // Constantes para animaciones
    public static final int INACTIVO = 0;
    public static final int CORRER = 1;
    public static final int HERIDO = 2;
    public static final int DISPARO = 3;
   
    // Nuevas propiedades para comportamiento común
    protected boolean movimientoHaciaIzquierda = true;
    protected float velocidadMovimiento = 0.3f * Juego.SCALE;
    protected boolean patrullando = true;
    protected float checkOffset = 15 * Juego.SCALE;
    protected boolean invertirOrientacion = false;

    protected float gravedad = 0.03f * Juego.SCALE;
    
    public Enemigo(float x, float y, int width, int height, int vidaMaxima) {
        super(x, y, width, height);
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }
    
    public void update() {
        if (!activo) return;

        aplicarGravedad();
        if (!enAire && patrullando) {
            patrullar();
        }
        mover();
        // Actualizar animaciones
        if (animaciones != null) {
            animaciones.actualizarAnimacion();
            determinarAnimacion();
        }
    }
    
    protected void aplicarGravedad() {
        // Verificar primero si estamos en el suelo
        boolean enSuelo = MetodoAyuda.isEntityOnFloor(hitbox, Juego.NIVEL_ACTUAL_DATA);
        
        if (enSuelo) {
            enAire = false;
            velocidadAire = 0;
        } else {
            enAire = true;
            // Aplicar gravedad SOLO si estamos en el aire
            velocidadAire += gravedad;
            
            // Verificar si podemos movernos hacia abajo
            boolean movimientoExitoso = MetodoAyuda.CanMoveHere(
                hitbox.x, 
                hitbox.y + velocidadAire, 
                hitbox.width, 
                hitbox.height, 
                Juego.NIVEL_ACTUAL_DATA);
                
            if (movimientoExitoso) {
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
    
    protected void patrullar() {
        // Verificar si puede moverse en la dirección actual
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
        
        if (hayPared || !haySueloAdelante) {
            // Intentar cambiar dirección
            movimientoHaciaIzquierda = !movimientoHaciaIzquierda;
            
            // Verificar si puede moverse en la nueva dirección
            boolean hayParedEnNuevaDireccion = MetodoAyuda.hayParedAdelante(
                hitbox, 
                Juego.NIVEL_ACTUAL_DATA, 
                movimientoHaciaIzquierda ? -checkOffset : checkOffset
            );
            
            boolean haySueloEnNuevaDireccion = MetodoAyuda.haySueloAdelante(
                hitbox, 
                Juego.NIVEL_ACTUAL_DATA, 
                movimientoHaciaIzquierda ? -checkOffset : checkOffset
            );
            
            if (hayParedEnNuevaDireccion || !haySueloEnNuevaDireccion) {
                // No puede moverse en ninguna dirección, quieto
                velocidadX = 0;
                patrullando = false;
                if (animaciones != null) {
                    animaciones.setAccion(INACTIVO);
                }
            } else {
                // Puede moverse en la nueva dirección
                velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
            }
        }
    }
    
    protected void cambiarDireccion() {
        movimientoHaciaIzquierda = !movimientoHaciaIzquierda;
        velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
    }
    
    protected void mover() {
        boolean movimientoExitoso = MetodoAyuda.moverHorizontal(
            hitbox, 
            velocidadX, 
            Juego.NIVEL_ACTUAL_DATA
        );
        
        x = hitbox.x;
        
        if (!movimientoExitoso) {
            cambiarDireccion();
        }
    }
    
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        // Para animaciones
        if (animaciones != null) {
            renderizarConAnimacion(g, xLvlOffset, yLvlOffset);
        }
        // Para debugging
        drawHitBox(g, xLvlOffset, yLvlOffset);
    }
    

    
    public void recibirDaño(int cantidad) {
        if (!activo) return;
        
        vida -= cantidad;
        
        // Cambiar a animación de herido temporalmente
        if (animaciones != null && vida > 0) {
            animaciones.setAccion(HERIDO);
            animaciones.resetearAnimacion();
        }
        
        if (vida <= 0) {
            vida = 0;
            morir();
        }
    }
    
    protected void morir() {
        activo = false;
        if (animaciones != null) {
            animaciones.setAccion(HERIDO);
            animaciones.resetearAnimacion();
        }
    }
    
    protected void drawHitBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.PINK);
        g.drawRect(
            (int) (hitbox.x - xLvlOffset),
            (int) (hitbox.y - yLvlOffset),
            (int) hitbox.width,
            (int) hitbox.height
        );
    }

    // Método para inicializar configuración común
    protected void inicializarEnemigo(float xOffset, float yOffset, 
                                    float hitboxWidth, float hitboxHeight,
                                    boolean iniciarHaciaIzquierda,
                                    boolean invertirOrientacionSprite) {
        this.xDrawOffset = xOffset * Juego.SCALE;
        this.yDrawOffset = yOffset * Juego.SCALE;
        this.invertirOrientacion = invertirOrientacionSprite;
        
        initHitBox(x, y, hitboxWidth * Juego.SCALE, hitboxHeight * Juego.SCALE);
        
        this.movimientoHaciaIzquierda = iniciarHaciaIzquierda;
        this.velocidadX = iniciarHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
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
        if (velocidadX != 0) {
            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
        }
    }

    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {}
    protected abstract void cargarAnimaciones();
    protected abstract void determinarAnimacion();
    
    // GETTERS Y SETTERS
    public int getVida() {
        return vida;
    }
    
    public void setVida(int vida) {
        this.vida = vida;
    }
    
    public int getVidaMaxima() {
        return vidaMaxima;
    }
    
    public boolean estaActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public void setVelocidadX(float velocidadX) {
        this.velocidadX = velocidadX;
    }
    
    public float getVelocidadX() {
        return velocidadX;
    }
    
    public boolean estaEnAire() {
        return enAire;
    }
    
    public void setEnAire(boolean enAire) {
        this.enAire = enAire;
    }
}