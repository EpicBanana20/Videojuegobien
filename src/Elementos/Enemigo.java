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
   
    protected float gravedad = 0.04f * Juego.SCALE;
    
    public Enemigo(float x, float y, int width, int height, int vidaMaxima) {
        super(x, y, width, height);
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }
    
    public void update() {
        if (!activo) return;

        aplicarGravedad();
        mover();
        
        // Actualizar animaciones
        if (animaciones != null) {
            animaciones.actualizarAnimacion();
            determinarAnimacion();
        }
    }
    
   
    
    protected void mover() {
        // Comportamiento básico de movimiento horizontal utilizando el método centralizado
        if (velocidadX != 0) {
            MetodoAyuda.moverHorizontal(hitbox, velocidadX, Juego.NIVEL_ACTUAL_DATA);
            x = hitbox.x;
        }
    }
    
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
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

    protected abstract void cargarAnimaciones();
    protected abstract void determinarAnimacion();
    protected void aplicarGravedad() {}
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