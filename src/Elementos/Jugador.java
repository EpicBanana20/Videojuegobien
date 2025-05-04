package Elementos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Elementos.Administradores.AdministradorBalas;
import Elementos.Quimica.SistemaQuimico;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;
import Utilz.MetodoAyuda;

import static Utilz.Constantes.ConstanteJugador.*;
import static Utilz.MetodoAyuda.*;

public class Jugador extends Cascaron {
    // Reemplazamos las variables de animación por una instancia de Animaciones
    private Animaciones animaciones;
    private BufferedImage[][] spritesJugador;
    
    private boolean mirandoIzquierda = false;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean left, right, down, up, jump;
    private float playerSpeed;
    private int[][] lvlData;
    private float xDrawOffset = 15 * Juego.SCALE;
    private float yDrawOffset = 20 * Juego.SCALE;

    private float vidaActual;
    private float vidaMaxima;
    private boolean muerto = false;
    private boolean invulnerable = false;
    private int invulnerabilidadTimer = 0;
    private static final int INVULNERABILIDAD_DURACION = 60; // 1 segundo
    private int tiempoMuerte = 0;
    private static final int TIEMPO_RESPAWN = 180; // 3 segundos

    ///// graveda y salto
    private float airSpeed = -1f;
    private float gravity = 0.02f * Juego.SCALE; // Aumentamos ligeramente la gravedad
    private float jumpSpeed = -2.23f * Juego.SCALE; // Hacemos el salto un poco más potente
    private boolean inAir = false;
    
    // Variable para controlar si el jugador quiere bajar a través de plataformas
    private boolean quiereBajarPlataforma = false;
    // Cooldown para evitar volver a subir a la plataforma inmediatamente
    private int bajarPlataformaCooldown = 0;
    private static final int MAX_BAJAR_COOLDOWN = 20;
    
    // Para debug
    private boolean sobreUnaPlataforma = false;

    // Apuntado
    private AimController aimController;
    private int currentMouseX, currentMouseY;

    // Armas
    private Arma armaActual;
    private ArrayList<Arma> inventarioArmas = new ArrayList<>();
    private int indiceArmaActual = 0;

    //Balas
    private AdministradorBalas adminBalasCentral;
    private SistemaQuimico sistemaQuimico;

    private Personaje personaje;
    public Jugador(float x, float y, int w, int h, Personaje.TipoPersonaje tipoPersonaje) {
        super(x, y, w, h);
        this.personaje = new Personaje(tipoPersonaje);
        this.playerSpeed = personaje.getVelocidad();
        this.vidaMaxima = personaje.getVidaMaxima();
        this.vidaActual = vidaMaxima;

        loadAnimation();
        initHitBox(x, y, 20 * Juego.SCALE, 27 * Juego.SCALE);
        aimController = new AimController(200* Juego.SCALE);
        adminBalasCentral = new AdministradorBalas();
        inicializarArmas();
        sistemaQuimico = new SistemaQuimico();
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
        if (muerto) return;
        
        if (invulnerable) {
            invulnerabilidadTimer--;
            if (invulnerabilidadTimer <= 0) {
                invulnerable = false;
            }
        }
        // Verificar si estamos sobre una plataforma atravesable
        sobreUnaPlataforma = isEntityOnPlatform(hitbox, lvlData);
        
        // Actualizar cooldown para bajar plataformas
        if (bajarPlataformaCooldown > 0) {
            bajarPlataformaCooldown--;
        }
        
        // Si presiona S estando sobre una plataforma, iniciar caída
        if (down && !inAir && sobreUnaPlataforma && bajarPlataformaCooldown == 0) {
            // Activar caída a través de la plataforma
            inAir = true;
            quiereBajarPlataforma = true;
            bajarPlataformaCooldown = MAX_BAJAR_COOLDOWN;
            // Forzar una velocidad inicial significativa para atravesar la plataforma
            airSpeed = 2.0f;
            // Mover ligeramente hacia abajo para salir de la colisión
            hitbox.y += 1;
        }
        
        actuPosicion();
        
        // Actualizamos las animaciones usando nuestra nueva clase
        animaciones.actualizarAnimacion();
        
        // Actualizamos qué animación mostrar basado en el estado del jugador
        determinarAnimacion();
        personaje.update();

        if (personaje.necesitaCambioSprite()) {
            loadAnimation();
        }

        armaActual.setModificadorCadencia(personaje.getModificadorCadencia());
        
        aimController.update(getXCenter() - xlvlOffset, getYCenter() - yLvlOffset, currentMouseX, currentMouseY);
        armaActual.update(getXCenter(), getYCenter(), aimController);
        adminBalasCentral.update();

        if(armaActual instanceof Elementos.Armas.ArmaLaser) {
            Elementos.Armas.ArmaLaser laser = (Elementos.Armas.ArmaLaser) armaActual;
            if(attacking) {
                laser.disparar();
            } else {
                laser.detenerDisparo();
            }
        } else if(attacking) {
            armaActual.disparar();
        }
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
        adminBalasCentral.render(g, xlvlOffset, yLvlOffset);
    }
    
    public void resetPosition(float x, float y) {
        this.x = x;
        this.y = y;
        hitbox.x = x;
        hitbox.y = y;
        resetDirBooleans();
        inAir = false;
        airSpeed = 0;
    }


    public void cambiarArma() {
        indiceArmaActual = (indiceArmaActual + 1) % inventarioArmas.size();
        armaActual = inventarioArmas.get(indiceArmaActual);
        System.out.println("Cambiado a: " + armaActual.getNombre());
    }

    public void inicializarArmas() {
        inventarioArmas.clear();
        
        // Añadir todas las armas disponibles
        inventarioArmas.add(new Elementos.Armas.ArmaMercurio(adminBalasCentral));
        inventarioArmas.add(new Elementos.Armas.MachineGun(adminBalasCentral));
        inventarioArmas.add(new Elementos.Armas.ArmaEscopeta(adminBalasCentral));
        inventarioArmas.add(new Elementos.Armas.ArmaLaser(adminBalasCentral));
        inventarioArmas.add(new Elementos.Armas.ArmaFrancotirador(adminBalasCentral));
        
        if (!inventarioArmas.isEmpty()) {
            indiceArmaActual = 0;
            armaActual = inventarioArmas.get(0);
        }
    }

    private void actuPosicion() {
        moving = false;
        
        // Verificar primero si estamos en el suelo (pero no usando isEntityOnFloor)
        // Para plataformas atravesables, tenemos una lógica diferente
        boolean enSuelo = false;
        
        if (quiereBajarPlataforma) {
            // Si queremos bajar, no consideramos plataformas atravesables como suelo
            enSuelo = isEntityOnFloor(hitbox, lvlData, true);
        } else {
            // Caso normal, considerar tanto suelo normal como plataformas
            enSuelo = isEntityOnFloor(hitbox, lvlData, false) || isEntityOnPlatform(hitbox, lvlData);
        }
        
        if (enSuelo && !inAir) {
            // Estamos en suelo firme
            airSpeed = 0;
        } else {
            // Estamos en el aire o queremos estarlo
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
        
        int tileY = (int)(hitbox.y / Juego.TILES_SIZE);
        
        // Aplicar movimiento horizontal
        if (xSpeed != 0) {
            MetodoAyuda.moverHorizontal(hitbox, xSpeed, lvlData);
            moving = true;
            // Verificar si estábamos sobre una plataforma y ahora no
            if (sobreUnaPlataforma && !inAir) {
                    hitbox.y = tileY * Juego.TILES_SIZE + 7;
                    inAir = false;
            }
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
                // Si hay colisión y queremos bajar, verificar si es una plataforma atravesable
                if (quiereBajarPlataforma) {
                    tileY = (int)((hitbox.y + hitbox.height) / Juego.TILES_SIZE);
                    int xIndex1 = (int)(hitbox.x / Juego.TILES_SIZE);
                    int xIndex2 = (int)((hitbox.x + hitbox.width) / Juego.TILES_SIZE);
                    
                    boolean sobrePlataforma = false;
                    
                    // Verificar si estamos colisionando con una plataforma atravesable
                    if (tileY < lvlData.length) {
                        if (xIndex1 < lvlData[0].length) {
                            sobrePlataforma = sobrePlataforma || esPlataformaAtravesable(lvlData[tileY][xIndex1]);
                        }
                        if (xIndex2 < lvlData[0].length) {
                            sobrePlataforma = sobrePlataforma || esPlataformaAtravesable(lvlData[tileY][xIndex2]);
                        }
                    }
                    
                    if (sobrePlataforma) {
                        // Si estamos colisionando con una plataforma atravesable y queremos bajar,
                        // movemos el jugador un poco más hacia abajo para salir de la colisión
                        hitbox.y += Math.max(1, airSpeed);
                        System.out.println("Atravesando plataforma...");
                    } else {
                        // Si no es una plataforma atravesable, nos detenemos como normalmente
                        hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed, true);
                        airSpeed = 0;
                        inAir = false;
                        quiereBajarPlataforma = false;
                    }
                } else {
                    // Comportamiento normal para colisiones cuando no queremos bajar
                    hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed, false);
                    
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
        
        // Actualizar la posición principal después de todos los cálculos
        x = hitbox.x;
        y = hitbox.y;
    }
    
    private void jump() {
        if(inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void loadAnimation() {
        // Cargamos los sprites como antes
        BufferedImage img = LoadSave.GetSpriteAtlas(personaje.getSpriteAtlas());
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

    public void recibirDaño(float cantidad) {
        if (invulnerable || muerto) return;
        
        vidaActual -= cantidad;
        invulnerable = true;
        invulnerabilidadTimer = INVULNERABILIDAD_DURACION;
        
        if (vidaActual <= 0) {
            vidaActual = 0;
            morir();
        }
    }

    private void morir() {
        muerto = true;
        resetDirBooleans();
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

    public SistemaQuimico getSistemaQuimico() {
        return sistemaQuimico;
    }

    public void usarHabilidadEspecial() {
        personaje.usarHabilidadEspecial();
    }

    public float getVidaActual() { return vidaActual; }
    public float getVidaMaxima() { return vidaMaxima; }
    public boolean estaMuerto() { return muerto; }

}