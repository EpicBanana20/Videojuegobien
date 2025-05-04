package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Jugador;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.MetodoAyuda;
import Utilz.Animaciones;

public class BOSS1 extends Enemigo {

    // Constantes específicas
    private static final int ANCHO_DEFAULT = 150;
    private static final int ALTO_DEFAULT = 150;
    private static final int VIDA_DEFAULT = 300;
    
    // Estados del jefe
    private static final int FASE_NORMAL = 0;
    private static final int FASE_ENOJADO = 1;
    private static final int FASE_FURIOSO = 2;
    
    // Estado actual
    private int faseActual = FASE_NORMAL;
    
    // Patrones de ataque
    private int patronAtaqueActual = 0;
    private int contadorPatron = 0;
    private int duracionPatron = 180; // 3 segundos a 60 FPS
    
    // Para disparos
    private boolean disparoEnProceso = false;
    private boolean disparoPendiente = false;
    private float anguloDisparo = 0;
    private int frameDisparo = 1;

    public BOSS1(float x, float y) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        // Inicializar propiedades
        inicializarEnemigo(25, 50, 100, 100, true, true);
        this.velocidadMovimiento = 0.7f * Juego.SCALE;
        this.puedeDisparar = true;
        this.disparoMaxCooldown = 45;
        this.rangoDeteccionJugador = 1000 * Juego.SCALE;
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    public void update() {
        super.update();
        
        // Si está muerto, no hacer nada más
        if (!activo) return;
        
        // Verificar cambios de fase según salud restante
        actualizarFase();
        actualizarPatron();
        
        if (faseActual >= FASE_ENOJADO) {
            // En fases avanzadas, alternar entre patrones y alejarse
            if (patronAtaqueActual != 3) {
                alejarseDelJugador();
            } else {
                // Patrón de disparo múltiple - quedarse quieto
                velocidadX = 0;
            }
        }

        // A partir de la segunda fase, alejarse constantemente del jugador
        if (disparoPendiente && disparoEnProceso && 
        animaciones.getAccionActual() == DISPARO && 
        animaciones.getAnimIndice() == frameDisparo && (velocidadX ==0)) {
        dispararSegunFase();
        disparoPendiente = false;
        }
        
        // Intentar detectar al jugador para disparar (independientemente de la fase)
        if (!disparoEnProceso && Juego.jugadorActual != null) {
            manejarDisparo(Juego.jugadorActual);
        }
    }


    protected void manejarDisparo(Jugador jugador) {
        if (!puedeDisparar || !activo) return;
        
        // Reducir cooldown si está activo
        if (disparoCooldown > 0) {
            disparoCooldown--;
            return;
        }
        
        // Solo disparar si está quieto (específico para el jefe)
        if (velocidadX != 0 && faseActual>=FASE_ENOJADO) return;
        
        // Verificar si el jugador está en rango
        if (puedeVerJugador(jugador)) {
            // Orientar el enemigo hacia el jugador
            orientarHaciaJugador(jugador);
            
            // Calcular ángulo y disparar
            float angulo = calcularAnguloHaciaJugador(jugador);
            disparar(angulo);
            disparoCooldown = disparoMaxCooldown;
        }
    }
    
    private void alejarseDelJugador() {
        if (Juego.jugadorActual == null) return;
        
        // Obtener posición del jugador y del jefe
        float jugadorX = Juego.jugadorActual.getXCenter();
        float jefeX = this.hitbox.x + this.hitbox.width/2;
        
        // Determinar dirección para alejarse (opuesta al jugador)
        boolean alejarseDerecha = jugadorX < jefeX;
        
        // Verificar si hay pared o precipicio en la dirección que queremos ir
        float distanciaCheck = alejarseDerecha ? checkOffset : -checkOffset;
        boolean hayPared = MetodoAyuda.hayParedAdelante(hitbox, Juego.NIVEL_ACTUAL_DATA, distanciaCheck);
        boolean haySuelo = MetodoAyuda.haySueloAdelante(hitbox, Juego.NIVEL_ACTUAL_DATA, distanciaCheck);
        
        if (hayPared || !haySuelo) {
            // Si hay pared o no hay suelo, quedarse quieto
            velocidadX = 0;
        } else {
            // Alejarse a velocidad según la fase
            movimientoHaciaIzquierda = !alejarseDerecha;
            velocidadX = alejarseDerecha ? velocidadMovimiento : -velocidadMovimiento;
        }
    }
    
    private void actualizarFase() {
        int porcentajeVida = (vida * 100) / vidaMaxima;
        
        if (porcentajeVida < 30 && faseActual != FASE_FURIOSO) {
            cambiarFase(FASE_FURIOSO);
        } else if (porcentajeVida < 60 && faseActual != FASE_ENOJADO && faseActual != FASE_FURIOSO) {
            cambiarFase(FASE_ENOJADO);
        }
    }
    
    private void cambiarFase(int nuevaFase) {
        this.faseActual = nuevaFase;
        
        // Cambiar propiedades según la fase
        switch (faseActual) {
            case FASE_NORMAL:
                this.velocidadMovimiento = 0.7f * Juego.SCALE;
                this.disparoMaxCooldown = 45;
                break;
            case FASE_ENOJADO:
                this.velocidadMovimiento = 3f * Juego.SCALE;
                this.disparoMaxCooldown = 45;
                System.out.println("¡El jefe está enojado! Comienza a alejarse");
                break;
            case FASE_FURIOSO:
                this.velocidadMovimiento = 5f * Juego.SCALE;
                this.disparoMaxCooldown = 30;
                System.out.println("¡El jefe está FURIOSO! Se aleja más rápido");
                break;
        }
    }
    
    private void actualizarPatron() {
        contadorPatron++;
        
        if (contadorPatron >= duracionPatron) {
            contadorPatron = 0;
            cambiarPatron();
        }
    }
    
    private void cambiarPatron() {
        // Diferentes patrones según la fase
        int numPatrones = 2 + faseActual; // Más patrones en fases avanzadas
        
        int nuevoPatron = (patronAtaqueActual + 1) % numPatrones;
        patronAtaqueActual = nuevoPatron;
        
        System.out.println("Jefe cambia a patrón: " + patronAtaqueActual);
        
        // Configurar comportamiento según el patrón
        switch (patronAtaqueActual) {
            case 0: // Patrón: Moverse de lado a lado
                setPatrullando(true);
                break;
            case 1: // Patrón: Quedarse quieto y disparar
                setPatrullando(false);
                velocidadX = 0;
                break;
            case 2: // Patrón: Movimiento rápido (solo en fase ENOJADO+)
                setPatrullando(true);
                velocidadX = movimientoHaciaIzquierda ? 
                    -velocidadMovimiento * 1.5f : velocidadMovimiento * 1.5f;
                break;
            case 3: // Patrón: Disparo múltiple (solo en fase FURIOSO)
                setPatrullando(false);
                velocidadX = 0;
                break;
        }
    }
    
    private void dispararSegunFase() {
        switch (faseActual) {
            case FASE_NORMAL:
                dispararBalaSimple();
                break;
            case FASE_ENOJADO:
                dispararBalaDoble();
                break;
            case FASE_FURIOSO:
                if (patronAtaqueActual == 3) {
                    dispararBalaMultiple(); // 5 balas en abanico
                } else {
                    dispararBalaTriple(); // 3 balas
                }
                break;
        }
    }
    
    private void dispararBalaSimple() {
        crearBala(anguloDisparo, 5, 2.0f);
    }
    
    private void dispararBalaDoble() {
        crearBala(anguloDisparo - 0.1f, 5, 2.0f);
        crearBala(anguloDisparo + 0.1f, 5, 2.0f);
    }
    
    private void dispararBalaTriple() {
        crearBala(anguloDisparo, 7, 1.5f);
        crearBala(anguloDisparo - 0.3f, 7, 1.5f);
        crearBala(anguloDisparo + 0.3f, 7, 1.5f);
    }
    
    private void dispararBalaMultiple() {
        for (int i = -2; i <= 2; i++) {
            crearBala(anguloDisparo + (i * 0.2f), 8, 2.0f);
        }
    }
    
    private void crearBala(float angulo, int daño, float velocidad) {
        float origenX = hitbox.x + hitbox.width/2;
        float origenY = hitbox.y + hitbox.height/2;
        
        // Ajustar origen según dirección
        if (movimientoHaciaIzquierda) {
            origenX -= 25 * Juego.SCALE;
        } else {
            origenX += 25 * Juego.SCALE;
        }
        
        Bala nuevaBala = new Bala(
            origenX, 
            origenY, 
            angulo, 
            LoadSave.BULLET_BOSS1, // Puedes crear un sprite específico para el jefe
            daño,
            velocidad
        );
        
        adminBalas.agregarBala(nuevaBala);
    }
    
    @Override
    protected void disparar(float angulo) {
        disparoEnProceso = true;
        disparoPendiente = true;
        anguloDisparo = angulo;
        animaciones.setAccion(DISPARO);
        animaciones.setAccion(DISPARO);
        
        animaciones.resetearAnimacion();
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Este método debe implementarse para cargar las animaciones
        // del sprite de tu jefe
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/BOSS1 64x64.png");
        
        // 5 acciones: INACTIVO, CORRER, HERIDO, DISPARO, MUERTE
        spritesEnemigo = new BufferedImage[5][7];
        
        // Ancho y alto de cada frame
        int frameWidth = 64; // Ajustar según tu sprite
        int frameHeight = 64;
        
        // Configurar número de frames por acción según tu sprite
        int[] framesEnFila = {4, 7, 2, 4, 2}; // Ajustar según tus animaciones
        
        // Extraer frames
        for (int j = 0; j < spritesEnemigo.length; j++) {
            for (int i = 0; i < framesEnFila[j]; i++) {
                spritesEnemigo[j][i] = img.getSubimage(
                    i * frameWidth, j * frameHeight, frameWidth, frameHeight
                );
            }
        }
        
        // Crear animaciones
        animaciones = new Animaciones(spritesEnemigo);
        
        // Número de frames por animación
        animaciones.setNumFramesPorAnimacion(INACTIVO, framesEnFila[0]);     // INACTIVO
        animaciones.setNumFramesPorAnimacion(CORRER, framesEnFila[1]);   // CORRER
        animaciones.setNumFramesPorAnimacion(HERIDO, framesEnFila[2]);   // HERIDO
        animaciones.setNumFramesPorAnimacion(DISPARO, framesEnFila[3]); // DISPARO
        animaciones.setNumFramesPorAnimacion(MUERTE, framesEnFila[4]);    // MUERTE
        
        // Animación inicial
        animaciones.setAccion(INACTIVO);
    }
    
    @Override
    protected void determinarAnimacion() {
        // Si está muriendo, mantener animación de muerte
        if (animaciones.getAccionActual() == MUERTE) {
            return;
        }
        
        // Si estamos disparando y no ha terminado, mantener esa animación
        if (disparoEnProceso && !animaciones.esUltimoFrame()) {
            return;
        } else if (disparoEnProceso && animaciones.esUltimoFrame()) {
            disparoEnProceso = false;
        }
        
        // Determinar según movimiento (sin considerar HERIDO)
        int nuevaAnimacion;
        
        if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        } else {
            nuevaAnimacion = INACTIVO;
        }
        
        animaciones.setAccion(nuevaAnimacion);
    }
    
    @Override
    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo && animaciones.getAccionActual() != MUERTE) return;
        
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
        
        // Dibujar según dirección
        if (movimientoHaciaIzquierda) {
            g.drawImage(animaciones.getImagenActual(),
                drawX + w, drawY,
                -w, h, null);
        } else {
            g.drawImage(animaciones.getImagenActual(),
                drawX, drawY,
                w, h, null);
        }
    }

    @Override
    protected float obtenerMultiplicadorDaño(String tipoDaño) {
        if (tipoDaño == null) return 1.0f;
        
        switch (tipoDaño) {
            case "fuego":
                return 1.5f; // Débil al fuego
            case "hielo":
                return 0.5f; // Resistente al hielo
            default:
                return 1.0f;
        }
    }

    @Override
    public void recibirDaño(int cantidad, String tipoDaño) {
        if (!activo)
            return;

        float multiplicador = obtenerMultiplicadorDaño(tipoDaño);
        int dañoFinal = (int)(cantidad * multiplicador);

        vida -= dañoFinal;

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
}