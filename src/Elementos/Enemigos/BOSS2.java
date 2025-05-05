package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import Elementos.Bala;
import Elementos.Enemigo;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.MetodoAyuda;
import Utilz.Animaciones;
import Elementos.Administradores.AdministradorEnemigos;

// Zefir, el jefe del segundo nivel (FANTASMA)
// Basado en Cesio (Cs) kriptón (Kr) Fosforo (P32)
public class BOSS2 extends Enemigo {

    // Estado de activación
    private boolean activado = false;
    
    // Constantes específicas
    private static final int ANCHO_DEFAULT = 130;
    private static final int ALTO_DEFAULT = 130;
    private static final int VIDA_DEFAULT = 250;
    
    // Estados del jefe
    private static final int FASE_NORMAL = 0;  // Fase inicial
    private static final int FASE_AVANZADA = 1; // Fase de invocación
    private static final int FASE_FINAL = 2;   // Fase combinada
    
    // Animación de teletransporte
    public static final int TELETRANSPORTE = 5;
    
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
    
    // Para teletransporte
    private boolean teletransportandose = false;
    private int teletransporteCooldown = 0;
    private int teletransporteMaxCooldown = 300; // 5 segundos a 60 FPS
    private int teletransporteAnimacionDuracion = 30;
    private int teletransporteAnimacionTick = 0;
    
    // Para vuelo
    private float velocidadY = 0;
    private float amplitudOscilacion = 1.0f * Juego.SCALE;
    private float frecuenciaOscilacion = 0.05f;
    private float tiempoVuelo = 0;
    
    // Para invocación
    private int invocacionCooldown = 0;
    private int invocacionMaxCooldown = 600; // 10 segundos a 60 FPS
    private int numEnemigosAInvocar = 2;
    private boolean invocando = false;
    
    // Referencia al administrador de enemigos para invocar Skelers
    private AdministradorEnemigos adminEnemigos;
    
    // Utilidades
    private Random random = new Random();

    public BOSS2(float x, float y, AdministradorEnemigos adminEnemigos) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
            
        this.adminEnemigos = adminEnemigos;
        
        // Inicializar propiedades
        inicializarEnemigo(25, 30, 80, 80, true, true);
        this.velocidadMovimiento = 0.7f * Juego.SCALE;
        this.puedeDisparar = true;
        this.disparoMaxCooldown = 120;
        this.rangoDeteccionJugador = 800 * Juego.SCALE;
        
        // No aplicar gravedad (es un fantasma que vuela)
        this.gravedad = 0;
        
        // Cargar animaciones
        cargarAnimaciones();
    }
    
    @Override
    public void update() {
        // Verificar si ya está activado
        if (!activado && Juego.jugadorActual != null) {
            // Verificar si el jugador está dentro del rango de detección
            if (puedeVerJugador(Juego.jugadorActual)) {
                activado = true;
                System.out.println("¡Zefir ha sido despertado!");
            } else {
                // Si aún no está activado, solo actualizar animación básica
                if (animaciones != null) {
                    animaciones.actualizarAnimacion();
                }
                return;
            }
        }
        
        // Si no está activado, no hacer nada más
        if (!activado) return;
        
        // Skip normal update if teleporting
        if (teletransportandose) {
            actualizarTeletransporte();
            // Still update animations
            animaciones.actualizarAnimacion();
            return;
        }
        
        // Instead of super.update() we'll customize the behavior
        if (!activo) {
            if (animaciones != null) {
                animaciones.actualizarAnimacion();
                
                // Si terminó la animación de muerte, marcarla como completada
                if (animaciones.getAccionActual() == MUERTE && animaciones.esUltimoFrame()) {
                    animacionMuerteTerminada = true;
                }
            }
            return;
        }
        
        // No usamos gravedad ya que vuela
        // Pero sí aplicamos vuelo ondulante
        aplicarVuelo();
        
        // Movimiento horizontal sigue la lógica normal si no está invocando
        if (!invocando) {
            mover();
        }
        
        // Verificar cambios de fase según salud restante
        actualizarFase();
        actualizarPatron();
        
        // Gestionar cooldowns
        gestionarCooldowns();
        
        // Intentar teletransportarse
        intentarTeletransporte();
        
        // Intentar invocar enemigos en fases avanzadas
        if (faseActual >= FASE_AVANZADA) {
            intentarInvocarEnemigos();
        }
        
        // Gestionar disparos
        if (disparoPendiente && disparoEnProceso && 
            animaciones.getAccionActual() == DISPARO && 
            animaciones.getAnimIndice() == frameDisparo) {
            dispararSegunFase();
            disparoPendiente = false;
        }
        
        // Intentar detectar al jugador para disparar
        if (!disparoEnProceso && !invocando && Juego.jugadorActual != null) {
            manejarDisparo(Juego.jugadorActual);
        }
        
        // Actualizar balas
        updateBalas();
        
        // Actualizar animaciones
        if (animaciones != null) {
            animaciones.actualizarAnimacion();
            determinarAnimacion();
        }
    }
    
    private void actualizarTeletransporte() {
        teletransporteAnimacionTick++;
        
        // Mitad del tiempo de animación: cambiamos la posición
        if (teletransporteAnimacionTick == teletransporteAnimacionDuracion / 2) {
            teletransportarANuevaPosicion();
        }
        
        // Fin de la animación: volvemos al estado normal
        if (teletransporteAnimacionTick >= teletransporteAnimacionDuracion) {
            teletransportandose = false;
            teletransporteAnimacionTick = 0;
            teletransporteCooldown = teletransporteMaxCooldown;
        }
    }
    
    private void teletransportarANuevaPosicion() {
        if (Juego.jugadorActual == null) return;
        
        // Obtener bordes del nivel para evitar teletransporte fuera de los límites
        int margen = 150;
        int minX = margen;
        int maxX = Juego.NIVEL_ACTUAL_ANCHO - margen - (int)hitbox.width;
        int minY = margen;
        int maxY = Juego.NIVEL_ACTUAL_ALTO - margen - (int)hitbox.height;
        
        // Intentar hasta 10 veces encontrar una posición válida
        int intentos = 0;
        float nuevaX = hitbox.x;
        float nuevaY = hitbox.y;
        boolean posicionValida = false;
        
        while (!posicionValida && intentos < 10) {
            intentos++;
            
            // Siempre teletransportarse cerca del jugador
            float jugadorX = Juego.jugadorActual.getXCenter();
            float jugadorY = Juego.jugadorActual.getYCenter();
            
            // Distancia de aparición (entre 100 y 300 píxeles del jugador)
            float distancia = (100 + random.nextInt(200)) * Juego.SCALE;
            float angulo = (float)(random.nextFloat() * Math.PI * 2); // Ángulo aleatorio
            
            // Calcular nueva posición relativa al jugador
            nuevaX = jugadorX + (float)Math.cos(angulo) * distancia - hitbox.width/2;
            nuevaY = jugadorY + (float)Math.sin(angulo) * distancia - hitbox.height/2;
            
            // Asegurar que está dentro de los límites
            nuevaX = Math.max(minX, Math.min(maxX, nuevaX));
            nuevaY = Math.max(minY, Math.min(maxY, nuevaY));
            
            // Verificar si la nueva posición es válida (no hay bloques sólidos)
            posicionValida = MetodoAyuda.CanMoveHere(
                nuevaX, nuevaY, hitbox.width, hitbox.height, Juego.NIVEL_ACTUAL_DATA);
        }
        
        // Si encontramos una posición válida, teletransportarse
        if (posicionValida) {
            hitbox.x = nuevaX;
            hitbox.y = nuevaY;
            // Actualizar las coordenadas x, y para mantener consistencia
            x = hitbox.x;
            y = hitbox.y;
        } else {
            // Si no encontramos posición válida después de varios intentos,
            // quedarnos donde estamos
            System.out.println("Zefir no pudo encontrar un lugar válido para teletransportarse");
        }
    }
    
    private void aplicarVuelo() {
        tiempoVuelo += frecuenciaOscilacion;
        velocidadY = (float)Math.sin(tiempoVuelo) * amplitudOscilacion;
        
        // Verificar si podemos movernos verticalmente
        if (MetodoAyuda.CanMoveHere(
                hitbox.x,
                hitbox.y + velocidadY,
                hitbox.width,
                hitbox.height,
                Juego.NIVEL_ACTUAL_DATA)) {
            
            hitbox.y += velocidadY;
            y = hitbox.y;
        }
    }
    
    private void actualizarFase() {
        int porcentajeVida = (vida * 100) / vidaMaxima;
        
        if (porcentajeVida < 30 && faseActual != FASE_FINAL) {
            cambiarFase(FASE_FINAL);
        } else if (porcentajeVida < 65 && faseActual != FASE_AVANZADA && faseActual != FASE_FINAL) {
            cambiarFase(FASE_AVANZADA);
        }
    }
    
    private void cambiarFase(int nuevaFase) {
        this.faseActual = nuevaFase;
        
        // Cambiar propiedades según la fase
        switch (faseActual) {
            case FASE_NORMAL:
                this.velocidadMovimiento = 0.7f * Juego.SCALE;
                this.disparoMaxCooldown = 120;
                this.amplitudOscilacion = 1.0f * Juego.SCALE;
                this.frecuenciaOscilacion = 0.05f;
                this.teletransporteMaxCooldown = 300;
                this.numEnemigosAInvocar = 2;
                break;
                
            case FASE_AVANZADA:
                this.velocidadMovimiento = 1.3f * Juego.SCALE;
                this.disparoMaxCooldown = 90;
                this.amplitudOscilacion = 1.5f * Juego.SCALE;
                this.frecuenciaOscilacion = 0.07f;
                this.teletransporteMaxCooldown = 240;
                this.numEnemigosAInvocar = 3;
                break;
                
            case FASE_FINAL:
                this.velocidadMovimiento = 1.8f * Juego.SCALE;
                this.disparoMaxCooldown = 60;
                this.amplitudOscilacion = 2.0f * Juego.SCALE;
                this.frecuenciaOscilacion = 0.1f;
                this.teletransporteMaxCooldown = 180;
                this.numEnemigosAInvocar = 5;
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
        
        // Configurar comportamiento según el patrón
        switch (patronAtaqueActual) {
            case 0: // Patrón: Moverse horizontalmente
                setPatrullando(true);
                break;
                
            case 1: // Patrón: Quedarse quieto y disparar
                setPatrullando(false);
                velocidadX = 0;
                break;
                
            case 2: // Patrón: Movimiento errático (solo en fase AVANZADA+)
                setPatrullando(true);
                cambiarDireccion(); // Cambiar dirección aleatoriamente
                velocidadX = movimientoHaciaIzquierda ? 
                    -velocidadMovimiento * 1.5f : velocidadMovimiento * 1.5f;
                break;
                
            case 3: // Patrón: Invocar enemigos y disparar (solo en fase FINAL)
                setPatrullando(false);
                velocidadX = 0;
                break;
        }
    }
    
    private void gestionarCooldowns() {
        // Gestionar cooldown de teletransporte
        if (teletransporteCooldown > 0) {
            teletransporteCooldown--;
        }
        
        // Gestionar cooldown de invocación
        if (invocacionCooldown > 0) {
            invocacionCooldown--;
        }
        
        // Gestionar cooldown de disparo
        if (disparoCooldown > 0) {
            disparoCooldown--;
        }
    }
    
    private void intentarTeletransporte() {
        // Solo intentar si no estamos ya teletransportándonos y el cooldown está listo
        if (!teletransportandose && teletransporteCooldown <= 0) {
            // Probabilidad basada en la fase
            float probabilidad = 0.005f; // 0.5% por update en fase normal
            
            if (faseActual == FASE_AVANZADA) {
                probabilidad = 0.01f; // 1% en fase avanzada
            } else if (faseActual == FASE_FINAL) {
                probabilidad = 0.015f; // 1.5% en fase final
            }
            
            // En la fase final, también teletransportarse cuando la salud es baja
            if (faseActual == FASE_FINAL && vida < vidaMaxima * 0.2f) {
                probabilidad *= 2; // Doble probabilidad cuando está muy dañado
            }
            
            // Intentar teletransporte
            if (random.nextFloat() < probabilidad) {
                iniciarTeletransporte();
            }
        }
    }
    
    private void iniciarTeletransporte() {
        teletransportandose = true;
        teletransporteAnimacionTick = 0;
        // Usar la animación específica de teletransporte
        animaciones.setAccion(TELETRANSPORTE);
        animaciones.resetearAnimacion();
    }
    
    private void intentarInvocarEnemigos() {
        // Solo intentar si no estamos ya invocando y el cooldown está listo
        if (!invocando && invocacionCooldown <= 0 && faseActual >= FASE_AVANZADA) {
            invocando = true;
            animaciones.setAccion(DISPARO); // Usar la animación de disparo para invocar
            animaciones.resetearAnimacion();
            
            // Desactivar movimiento durante la invocación
            float velocidadOriginal = velocidadX;
            velocidadX = 0;
            
            // Programar un timer para restaurar la velocidad
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        velocidadX = velocidadOriginal;
                        invocarEnemigos();
                        invocando = false;
                        invocacionCooldown = invocacionMaxCooldown;
                    }
                },
                animaciones.getNumFramesPorAnimacion(DISPARO) * (animaciones.getAnimVelocidad() * 16)
            );
        }
    }
    
    private void invocarEnemigos() {
        // Verificar que tenemos acceso al administrador de enemigos
        if (adminEnemigos == null || Juego.jugadorActual == null) return;
        
        // Posición para la invocación
        float origenX = hitbox.x + hitbox.width/2;
        float origenY = hitbox.y + hitbox.height/2;
        
        // Invocar enemigos Skeler
        for (int i = 0; i < numEnemigosAInvocar; i++) {
            // Calcular posición aleatoria cercana
            float offsetX = (float) ((Math.random() * 200 - 100) * Juego.SCALE);
            
            // Invocar al enemigo usando la referencia directa
            adminEnemigos.crearEnemigoSkeler(
                origenX + offsetX, 
                origenY);
        }
        
        System.out.println("¡Zefir ha invocado " + numEnemigosAInvocar + " Skelers!");
    }
    
    @Override
    protected void disparar(float angulo) {
        disparoEnProceso = true;
        disparoPendiente = true;
        anguloDisparo = angulo;
        animaciones.setAccion(DISPARO);
        animaciones.resetearAnimacion();
    }
    
    private void dispararSegunFase() {
        switch (faseActual) {
            case FASE_NORMAL:
                dispararAbanico(3, 30); // 3 balas en un arco de 30 grados
                break;
                
            case FASE_AVANZADA:
                dispararAbanico(5, 60); // 5 balas en un arco de 60 grados
                break;
                
            case FASE_FINAL:
                dispararAbanico(7, 90); // 7 balas en un arco de 90 grados
                break;
        }
    }
    
    private void dispararAbanico(int numBalas, float anguloTotal) {
        // Convertir a radianes
        float anguloTotalRad = (float) Math.toRadians(anguloTotal);
        
        // Calcular el paso entre cada bala
        float paso = anguloTotalRad / (numBalas - 1);
        
        // Calcular el ángulo inicial (centrado en anguloDisparo)
        float anguloInicial = anguloDisparo - (anguloTotalRad / 2);
        
        // Disparar las balas
        for (int i = 0; i < numBalas; i++) {
            float currentAngle = anguloInicial + (paso * i);
            crearBala(currentAngle, 5 + faseActual, 1.5f + (faseActual * 0.3f));
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
            LoadSave.BULLET_ZEFIR, // Podría ser reemplazado por un sprite específico para Zefir
            daño,
            velocidad
        );
        
        adminBalas.agregarBala(nuevaBala);
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Cargar sprite sheet para el jefe
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/Zefir 160x160.png");
        
        // 6 acciones: INACTIVO, CORRER, HERIDO, DISPARO, MUERTE, TELETRANSPORTE
        spritesEnemigo = new BufferedImage[6][12];
        
        // Ancho y alto de cada frame
        int frameWidth = 160;
        int frameHeight = 160;
        
        // Configurar número de frames por acción
        int[] framesEnFila = {6, 6, 2, 12, 6, 9}; // Ajustar según tus animaciones
        
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
        animaciones.setNumFramesPorAnimacion(INACTIVO, framesEnFila[0]);
        animaciones.setNumFramesPorAnimacion(CORRER, framesEnFila[1]);
        animaciones.setNumFramesPorAnimacion(HERIDO, framesEnFila[2]);
        animaciones.setNumFramesPorAnimacion(DISPARO, framesEnFila[3]);
        animaciones.setNumFramesPorAnimacion(MUERTE, framesEnFila[4]);
        animaciones.setNumFramesPorAnimacion(TELETRANSPORTE, framesEnFila[5]);
        
        // Animación inicial
        animaciones.setAccion(INACTIVO);
    }
    
    @Override
    protected void determinarAnimacion() {
        // Si está muriendo, mantener animación de muerte
        if (animaciones.getAccionActual() == MUERTE) {
            return;
        }
        
        // Si estamos teletransportándonos, mantener esa animación
        if (teletransportandose && animaciones.getAccionActual() == TELETRANSPORTE) {
            return;
        }
        
        // Si estamos disparando y no ha terminado, mantener esa animación
        if (disparoEnProceso && !animaciones.esUltimoFrame()) {
            return;
        } else if (disparoEnProceso && animaciones.esUltimoFrame()) {
            disparoEnProceso = false;
        }
        
        // Si estamos invocando y no ha terminado, mantener esa animación
        if (invocando && !animaciones.esUltimoFrame()) {
            return;
        }
        
        // Si recibimos daño y no ha terminado la animación, mantener
        if (animaciones.getAccionActual() == HERIDO && !animaciones.esUltimoFrame()) {
            return;
        }
        
        // Determinar según movimiento
        int nuevaAnimacion;
        
        if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        } else {
            nuevaAnimacion = INACTIVO;
        }
        
        animaciones.setAccion(nuevaAnimacion);
    }
    
    // Método para obtener acceso al administrador de enemigos (útil para pruebas)
    public void setAdminEnemigos(AdministradorEnemigos adminEnemigos) {
        this.adminEnemigos = adminEnemigos;
    }
    
    @Override
    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo && animaciones.getAccionActual() != MUERTE) return;
        
        // Si no está activado, usar animación de inactivo
        if (!activado && animaciones.getAccionActual() != INACTIVO) {
            animaciones.setAccion(INACTIVO);
        }
        
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
        
        // Efecto de transparencia para el teletransporte
        if (teletransportandose) {
            float alpha = 0.5f;
            if (teletransporteAnimacionTick < teletransporteAnimacionDuracion / 2) {
                // Desvaneciendo
                alpha = 1.0f - (teletransporteAnimacionTick / (float)(teletransporteAnimacionDuracion / 2));
            } else {
                // Apareciendo
                alpha = (teletransporteAnimacionTick - teletransporteAnimacionDuracion / 2) / 
                        (float)(teletransporteAnimacionDuracion / 2);
            }
            
            // Aplicar transparencia (en un juego real usarías AlphaComposite)
            // Aquí solo simulamos el efecto
            if (alpha < 0.3f) {
                return; // No dibujar si es muy transparente
            }
        }
        
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
}