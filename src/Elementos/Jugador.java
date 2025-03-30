package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;

import static Utilz.Constantes.ConstanteJugador.*;
import static Utilz.MetodoAyuda.*;

public class Jugador extends Cascaron {
    // Reemplazamos las variables de animación por una instancia de Animaciones
    private Animaciones animaciones;
    private BufferedImage[][] spritesJugador; // Mantenemos esto para cargar los sprites
    
    // Dejamos el resto de variables como estaban
    private boolean mirandoIzquierda = false;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean left, right, down, up, jump;
    private float playerSpeed = 1.5f;
    private int[][] lvlData;
    private float xDrawOffset = 15 * Juego.SCALE;
    private float yDrawOffset = 20 * Juego.SCALE;

    ///// graveda y salto
    private float airSpeed = -1f;
    private float gravity = 0.02f * Juego.SCALE; // Aumentamos ligeramente la gravedad
    private float jumpSpeed = -1.8f * Juego.SCALE; // Hacemos el salto un poco más potente
    private boolean inAir = false;

    // Apuntado
    private AimController aimController;
    private int currentMouseX, currentMouseY;

    // Armas
    private Elementos.Armas.ArmaMercurio armaActual;

    public Jugador(float x, float y, int w, int h) {
        super(x, y, w, h);
        loadAnimation();  // Cargamos las animaciones
        initHitBox(x, y, 20 * Juego.SCALE, 27 * Juego.SCALE);
        aimController = new AimController(200* Juego.SCALE);
        armaActual = new Elementos.Armas.ArmaMercurio();
    }

    // FUNCIONES DE MOUSE
    public void updateMouseInfo(int mouseX, int mouseY) {
        this.currentMouseX = mouseX;
        this.currentMouseY = mouseY;
    }
    
    public void renderAim(Graphics g, int xlvlOffset, int ylvlOffset) {
        // Obtener las coordenadas del apuntador
        float aimX = AimController.getAimedX();
        float aimY = AimController.getAimedY();
        
        // Dibujar un círculo en la posición del apuntador
        int cursorSize = 10; // Tamaño del círculo
        g.setColor(Color.RED);
        g.fillOval((int)aimX - cursorSize/2, (int)aimY - cursorSize/2, cursorSize, cursorSize);
        
    }

    public void update(int xlvlOffset, int yLvlOffset) {
        actuPosicion();
        
        // Actualizamos las animaciones usando nuestra nueva clase
        animaciones.actualizarAnimacion();
        
        // Actualizamos qué animación mostrar basado en el estado del jugador
        determinarAnimacion();
        
        aimController.update(getXCenter() - xlvlOffset, getYCenter() - yLvlOffset, currentMouseX, currentMouseY);
        armaActual.update(getXCenter(), getYCenter(), aimController);
        
        if(attacking)
            armaActual.disparar();
    }


    // Nuevo método para determinar qué animación mostrar
    private void determinarAnimacion() {
            int nuevaAnimacion = INACTIVO; // Por defecto, estamos inactivos
    if (inAir) {
            if (airSpeed < 0) {
                nuevaAnimacion = SALTAR;
            } else {
                nuevaAnimacion = CAYENDO;
            }
        }
        else if (moving) {
            nuevaAnimacion = CORRER;
        }
        
        // Configuramos la nueva acción en el objeto de animaciones
        animaciones.setAccion(nuevaAnimacion);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        inAir = !isEntityOnFloor(hitbox, lvlData);
    }

    public void render(Graphics g, int xlvlOffset, int yLvlOffset) {
        BufferedImage currentImage = animaciones.getImagenActual();
        if (mirandoIzquierda) {
            // Dibujar volteado horizontalmente
            g.drawImage(currentImage,
                (int) (hitbox.x - xDrawOffset + w) - xlvlOffset, 
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                -w, h, null);
        } else {
            // Dibujar normal
            g.drawImage(currentImage,
                (int) (hitbox.x - xDrawOffset) - xlvlOffset, 
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                w, h, null);
        }
        drawHitBox(g, xlvlOffset, yLvlOffset);
        renderAim(g, xlvlOffset, yLvlOffset);
        armaActual.render(g, xlvlOffset, yLvlOffset);
    }
    
    public void resetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        hitbox.x = x;
        hitbox.y = y;
        // Reiniciar otras propiedades si es necesario
        resetDirBooleans();
        inAir = false;
        airSpeed = 0;
    }

    private void actuPosicion() {
        moving = false;
        
        // Verificar primero si estamos en el suelo
        boolean enSuelo = isEntityOnFloor(hitbox, lvlData);
        if (enSuelo) {
            inAir = false;
            airSpeed = 0;  // Asegurarnos de resetear la velocidad de aire cuando toca el suelo
        } else {
            inAir = true;
        }
        
        // Procesar salto
        if (jump && !inAir) {
            jump();
        }
        
        // Si no hay movimiento y no estamos en el aire, salir temprano
        if (!left && !right && !inAir)
            return;
    
        // Procesar movimiento horizontal
        float xSpeed = 0;
        if (left) {
            xSpeed -= playerSpeed;
            mirandoIzquierda = true;
        }
        if (right) {
            xSpeed += playerSpeed;
            mirandoIzquierda = false;
        }
        
        // Aplicar movimiento horizontal
        if (xSpeed != 0) {
            MetodoAyuda.moverHorizontal(hitbox, xSpeed, lvlData);
            moving = true;
        }
        
        // Aplicar gravedad SOLO si estamos en el aire
        if (inAir) {
            airSpeed += gravity;
            
            // Verificar si podemos movernos hacia abajo
            if (CanMoveHere(
                    hitbox.x, 
                    hitbox.y + airSpeed, 
                    hitbox.width, 
                    hitbox.height, 
                    lvlData)) {
                
                hitbox.y += airSpeed;
            } else {
                // Si hay colisión, ajustar posición y resetear velocidad aire
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                
                // Resetear valores
                if (airSpeed > 0) {
                    // Tocando el suelo
                    airSpeed = 0;
                    inAir = false;
                } else {
                    // Chocando con el techo
                    airSpeed = 0.1f;
                }
            }
        }
    }
    
    private void jump() {
        if(inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void loadAnimation() {
        // Cargamos los sprites como antes
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        spritesJugador = new BufferedImage[7][7];
        for (int i = 0; i < spritesJugador.length; i++)
            for (int j = 0; j < spritesJugador[i].length; j++)
                spritesJugador[i][j] = img.getSubimage(j * 32, i * 32, 32, 32);

        // Creamos la instancia de Animaciones con los sprites cargados
        animaciones = new Animaciones(spritesJugador);
        
        // Configuramos manualmente la cantidad correcta de frames para cada animación
        // Aplicamos directamente la configuración para cada tipo de animación
        animaciones.setNumFramesPorAnimacion(INACTIVO, GetNoSprite(INACTIVO));
        animaciones.setNumFramesPorAnimacion(CORRER, GetNoSprite(CORRER));
        animaciones.setNumFramesPorAnimacion(SALTAR, GetNoSprite(SALTAR));
        animaciones.setNumFramesPorAnimacion(CAYENDO, GetNoSprite(CAYENDO));
    }

    //GETTERS Y SETTERS
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void resetDirBooleans() {
        left = right = up = down = false;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
    
    public boolean isAttacking() {
        return attacking;
    }

    public float getXCenter() {
        return hitbox.x + hitbox.width / 2f;
    }

    public float getYCenter() {
        return hitbox.y + hitbox.height / 2f;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public Arma getArmaActual() {
        return armaActual;
    }
}