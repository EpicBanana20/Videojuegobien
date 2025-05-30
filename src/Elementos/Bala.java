package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;
import java.awt.geom.AffineTransform;

public class Bala extends Cascaron {
    // Propiedades específicas de la bala
    private float velocidadX, velocidadY;
    private float velocidad = 2.0f * Juego.SCALE; // Velocidad base de la bala
    private boolean activa = true; // Indica si la bala está activa o debe eliminarse
    private int daño = 6; // Daño que causa la bala
    private float angulo; // Ángulo de disparo
    private String tipoDaño;
    
    // Propiedades para la animación
    private Animaciones animacionBala;
    private BufferedImage[][] spritesBala;
    private static final int ANIMACION_INICIO_BALA = 0; // Índice para la animación principal
    private static final int ANIMACION_BALA = 1; 
    private static final int ANIMACION_IMPACTO = 2; // Índice para la animación de impacto
    private boolean enImpacto = false; // Indica si la bala está en fase de impacto
    
    // Tamaños para visualización
    private int anchoSprite, altoSprite;
    private String tipoSprite = LoadSave.BULLET_MACHINEGUN; // Ruta por defecto del sprite de la bala

    //Constructor bala enemigos
    public Bala(float x, float y, float angulo, String tipoSprite, int daño, float velocidad) {
        super(x, y, (int) (8 * Juego.SCALE), (int) (8 * Juego.SCALE));
        
        this.tipoSprite = tipoSprite;
        this.daño = daño;
        this.velocidad = velocidad * Juego.SCALE;

        this.velocidadX = (float) Math.cos(angulo) * this.velocidad;
        this.velocidadY = (float) Math.sin(angulo) * this.velocidad;
        this.angulo = angulo;
        
        initHitBox(x, y, 6 * Juego.SCALE, 6 * Juego.SCALE);
        anchoSprite = (int)(16 * Juego.SCALE);
        altoSprite = (int)(16 * Juego.SCALE);
        cargarAnimaciones();
    }

    // Constructor para el jugador (con offset)
    public Bala(float x, float y, float angulo, String tipoSprite, int daño, float velocidad, String tipoDaño) {
        super(x, y, (int) (8 * Juego.SCALE), (int) (8 * Juego.SCALE));
        
        this.tipoSprite = tipoSprite;
        this.daño = daño;
        this.tipoDaño = tipoDaño;
        this.velocidad = velocidad * Juego.SCALE;
        
        this.velocidadX = (float) Math.cos(angulo) * this.velocidad;
        this.velocidadY = (float) Math.sin(angulo) * this.velocidad;
        this.angulo = angulo;
        
        initHitBox(x, y, 6 * Juego.SCALE, 6 * Juego.SCALE);
        anchoSprite = (int)(16 * Juego.SCALE);
        altoSprite = (int)(16 * Juego.SCALE);
        cargarAnimaciones();
    }
    
    private void cargarAnimaciones() {
        // Cargamos el atlas de sprites para las balas
        BufferedImage img = LoadSave.GetSpriteAtlas(tipoSprite);
        
        // Suponemos que la hoja tiene 2 animaciones (bala normal e impacto)
        // con 4 frames cada una
        spritesBala = new BufferedImage[3][4];
        
        // Extraer cada frame (asumimos que cada frame es 16x16 píxeles)
        for (int j = 0; j < spritesBala.length; j++) {
            for (int i = 0; i < spritesBala[j].length; i++) {
                spritesBala[j][i] = img.getSubimage(i * 17, j * 16, 17, 16);
            }
        }
        
        // Crear la instancia de animación
        animacionBala = new Animaciones(spritesBala);
        
        // Configurar cada animación (número de frames)
        animacionBala.setNumFramesPorAnimacion(ANIMACION_INICIO_BALA, 4);
        animacionBala.setNumFramesPorAnimacion(ANIMACION_BALA, 1);
        animacionBala.setNumFramesPorAnimacion(ANIMACION_IMPACTO, 4);
        
        // Hacer que la animación de impacto sea más rápida
        animacionBala.setAnimVelocidad(10);
    }

    public void update() {
        if (!enImpacto) {
            hitbox.x += velocidadX;
            hitbox.y += velocidadY;
            x = hitbox.x;
            y = hitbox.y;
        }
        
        // Actualizar animación
        animacionBala.actualizarAnimacion();
        
        // NUEVO: Detectar cuando termina la animación de inicio
        if (animacionBala.getAccionActual() == ANIMACION_INICIO_BALA && 
            animacionBala.esUltimoFrame()) {
            animacionBala.setAccion(ANIMACION_BALA);
        }
        
        // Código existente para detectar fin de impacto
        if (enImpacto && animacionBala.esUltimoFrame()) {
            desactivar();
        }
    }

    public void render(Graphics g, int xLvlOffset, int ylvlOffset) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Guardar transformación original
        AffineTransform originalTransform = g2d.getTransform();
        
        try {
            // Posición centrada (sin offset aplicado aún)
            float centerX = hitbox.x + hitbox.width/2;
            float centerY = hitbox.y + hitbox.height/2;
            
            // Aplicar transformación
            g2d.translate(centerX - xLvlOffset, centerY - ylvlOffset);
            g2d.rotate(angulo);
            
            // Dibujar imagen centrada en su origen
            g2d.drawImage(
                animacionBala.getImagenActual(),
                -anchoSprite/2,
                -altoSprite/2,
                anchoSprite,
                altoSprite,
                null
            );
        } finally {
            // Restaurar transformación original
            g2d.setTransform(originalTransform);
        }
        
        // Para debugging
        // drawHitBox(g, xLvlOffset, ylvlOffset);
    }
    
    // Método adicional para dibujar el hitbox con offset
    protected void drawHitBox(Graphics g, int xLvlOffset, int ylvlOffset) {
        g.setColor(Color.PINK);
        g.drawRect(
            (int)(hitbox.x - xLvlOffset), 
            (int)(hitbox.y - ylvlOffset),
            (int)hitbox.width, 
            (int)hitbox.height
        );         
    }

    // Comprueba si la bala está fuera de los límites del nivel
    public boolean fueraDeLimites(int nivelAncho, int nivelAlto) {
        boolean fuera = hitbox.x < 0 || hitbox.x > nivelAncho ||
                hitbox.y < 0 || hitbox.y > nivelAlto;

        if (fuera) {
            desactivar();
        }
        return fuera;
    }

    public boolean colisionConBloque() {
        // Verificar colisión en el centro de la bala
        if (MetodoAyuda.isSolido(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height / 2, Juego.NIVEL_ACTUAL_DATA)) {
            iniciarImpacto();
            return true;
        } else {
            iniciarRecorrido();
        }
        return false;
    }

    public void colisionConJugador(boolean jugadorInvulnerable) {
        // Solo iniciar el impacto si el jugador no es invulnerable
        if (!jugadorInvulnerable) {
            iniciarImpacto();
        }
        // Si es invulnerable (en dodgeroll), no hacemos nada
        // y la bala continúa su trayectoria
    }
    
    // Método para iniciar la animación de impacto
    private void iniciarImpacto() {
        if (!enImpacto) {
            enImpacto = true;
            animacionBala.setAccion(ANIMACION_IMPACTO);
            animacionBala.resetearAnimacion();
        }
    }

    private void iniciarRecorrido() {
        animacionBala.setAccion(ANIMACION_BALA);
    }

    // Desactiva la bala (cuando golpea algo o sale de los límites)
    public void desactivar() {
        activa = false;
    }

    // GETTERS Y SETTERS
    public boolean estaActiva() {
        return activa;
    }

    public int getDaño() {
        return daño;
    }

    public String getTipoDaño() {
        return tipoDaño;
    }
}