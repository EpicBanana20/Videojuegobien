package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import Juegos.Juego;
import Utilz.Animaciones;

public abstract class Enemigo extends Cascaron {
    // Constantes para animaciones (cada tipo de enemigo puede definir sus propias)
    public static final int INACTIVO = 0;
    public static final int CORRER = 1;
    public static final int HERIDO = 2;
    
    // Propiedades básicas del enemigo
    protected int vida;
    protected int vidaMaxima;
    protected boolean activo = true;
    
    // Propiedades para movimiento
    protected float velocidadX;
    protected float velocidadY;
    protected boolean enAire = false;
    protected float gravedad = 0.015f * Juego.SCALE;
    protected float velocidadAire = 0;
    
    // Propiedades para animación
    protected Animaciones animaciones;
    protected BufferedImage[][] spritesEnemigo;
    protected int accionActual = 0; // Estado actual (por ejemplo: 0=inactivo, 1=correr, etc.)
    
    // Offset para dibujar correctamente el sprite
    protected float xDrawOffset;
    protected float yDrawOffset;
    
    public Enemigo(float x, float y, int width, int height, int vidaMaxima) {
        super(x, y, width, height);
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }
    
    public void update() {
        if (!activo) return;
        
        // Actualizar la física (gravedad, colisiones, etc.)
        aplicarGravedad();
        mover();
        
        // Actualizar animaciones
        if (animaciones != null) {
            animaciones.actualizarAnimacion();
            determinarAnimacion();
        }
    }
    
    protected void aplicarGravedad() {
        // Implementación básica de gravedad
        if (enAire) {
            velocidadAire += gravedad;
            hitbox.y += velocidadAire;
            y = hitbox.y;
            
            // Aquí se comprobaría colisión con el suelo
            // Si toca el suelo, velocidadAire = 0 y enAire = false
        }
    }
    
    protected void mover() {
        // Comportamiento básico de movimiento
        hitbox.x += velocidadX;
        x = hitbox.x;
    }
    
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo && animaciones == null) return;
        
        // Dibujar enemigo (si tiene animaciones)
        if (animaciones != null) {
            g.drawImage(animaciones.getImagenActual(),
                (int) (hitbox.x - xDrawOffset) - xLvlOffset,
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                w, h, null);
        } else {
            // Dibujo temporal si no hay animaciones
            g.setColor(Color.RED);
            g.fillRect(
                (int) (hitbox.x - xLvlOffset),
                (int) (hitbox.y - yLvlOffset),
                (int) hitbox.width,
                (int) hitbox.height
            );
        }
        
        // Para debugging
        // drawHitBox(g, xLvlOffset, yLvlOffset);
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
        System.out.println("Enemigo derrotado!");
        // Podríamos usar la animación de herido como animación de muerte
        // o crear una nueva animación específica si se añade al sprite sheet
        if (animaciones != null) {
            animaciones.setAccion(HERIDO);
            animaciones.resetearAnimacion();
        }
    }
    
    // Método abstracto para que cada tipo de enemigo implemente su propia lógica de animación
    protected abstract void determinarAnimacion();
    
    // Método abstracto para que cada tipo de enemigo cargue sus propias animaciones
    protected abstract void cargarAnimaciones();
    
    protected void drawHitBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.PINK);
        g.drawRect(
            (int) (hitbox.x - xLvlOffset),
            (int) (hitbox.y - yLvlOffset),
            (int) hitbox.width,
            (int) hitbox.height
        );
    }
    
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
}