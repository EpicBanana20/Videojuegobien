package Elementos.Enemigos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.MetodoAyuda;

public class EnemigoEnergy extends Enemigo {
    // Constantes específicas
    private static final int ANCHO_DEFAULT = 64;
    private static final int ALTO_DEFAULT = 64;
    private static final int VIDA_DEFAULT = 30;
    
    // Propiedades para el comportamiento
    private boolean movimientoHaciaIzquierda = false;
    private float velocidadMovimiento = 0.3f * Juego.SCALE;
    private boolean patrullando = true;
    
    // Sprite único
    private BufferedImage sprite;
    
    public EnemigoEnergy(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Ajustar el offset para dibujar correctamente
        this.xDrawOffset = 8 * Juego.SCALE;
        this.yDrawOffset = 8 * Juego.SCALE;
        
        // Configurar hitbox específico
        initHitBox(x, y, 48 * Juego.SCALE, 48 * Juego.SCALE);
        
        // Inicializar velocidad (negativa para moverse a la izquierda al inicio)
        this.velocidadX = -velocidadMovimiento;
        movimientoHaciaIzquierda = true;
        
        // Cargar el sprite único
        cargarSprite();
    }
    
    private void cargarSprite() {
        // Carga tu sprite - reemplaza "enemigos/mi_enemigo.png" con la ruta a tu imagen
        sprite = LoadSave.GetSpriteAtlas("enemigos/Energy.png");
        
        // Si no encuentras el sprite, usar un cuadrado de color como alternativa
        if (sprite == null) {
            System.out.println("Sprite no encontrado, usando placeholder");
            sprite = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics g = sprite.getGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 64, 64);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, 63, 63);
            g.dispose();
        }
    }
    
    @Override
    public void update() {
        if (!activo) return;
        
        aplicarGravedad();
        if (!enAire && patrullando) {
            patrullar();
        }
        mover();
    }
    
    @Override
    protected void aplicarGravedad() {
        // Verificar primero si estamos en el suelo
        boolean enSuelo = MetodoAyuda.isEntityOnFloor(hitbox, Juego.NIVEL_ACTUAL_DATA);
        
        if (enSuelo) {
            enAire = false;
            velocidadAire = 0;
        } else {
            enAire = true;
        }
        
        // Aplicar gravedad SOLO si estamos en el aire
        if (enAire) {
            velocidadAire += gravedad;
            
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
        // Usar los métodos centralizados para comprobar pared y suelo
        float checkOffset = 15 * Juego.SCALE;
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
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        // Dibujar enemigo con posible flip horizontal
        if (sprite != null) {
            // Calcular posición con offset
            int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
            int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
            
            if (movimientoHaciaIzquierda) {
                // Dibujar volteado horizontalmente
                g.drawImage(sprite,
                    drawX + w, drawY,
                    -w, h, null);
            } else {
                // Dibujar normal
                g.drawImage(sprite,
                    drawX, drawY,
                    w, h, null);
            }
        }
        
        // Para debugging (descomentar si necesitas ver el hitbox)
        drawHitBox(g, xLvlOffset, yLvlOffset);
    }
    
    // Estos métodos son requeridos por la clase abstracta Enemigo
    @Override
    protected void determinarAnimacion() {
        // No hacemos nada aquí ya que no usamos animaciones
    }
    
    @Override
    protected void cargarAnimaciones() {
        // No hacemos nada aquí ya que no usamos animaciones
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
}