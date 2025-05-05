package Elementos.Enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Elementos.Enemigo;
import Elementos.Jugador;
import Juegos.Juego;
import Utilz.LoadSave;
import Utilz.Animaciones;

public class EnemigoDistut extends Enemigo{
    // Constantes específicas de este tipo de enemigo
    private static final int ANCHO_DEFAULT = 96;
    private static final int ALTO_DEFAULT = 72;
    private static final int VIDA_DEFAULT = 50;

    private boolean disparoEnProceso = false;
    private int frameDisparo = 1; // El disparo ocurrirá en el tercer frame (0,1,2,3)
    private boolean disparoPendiente = false;
    private Elementos.Administradores.AdministradorEnemigos adminEnemigos;
    private int numEnemigosGenerar = 2; // Número de enemigos a generar por disparo
    
    // Ajuste específico para este enemigo
    private int ajuste = -40;
    
    // Control de enemigos invocados
    private ArrayList<Enemigo> enemigosInvocados = new ArrayList<>();
    private static final int MAX_ENEMIGOS_INVOCADOS = 8;
    
    // Estados especiales
    private boolean enModoHuida = false;
    private static final float DISTANCIA_SEGURA = 600 * Juego.SCALE; // Distancia segura del jugador
    private static final float VELOCIDAD_HUIDA = 1.2f * Juego.SCALE; // Más rápido al huir
    
    public EnemigoDistut(float x, float y, Elementos.Administradores.AdministradorEnemigos adminEnemigos) {
        super(x, y, 
            (int)(ANCHO_DEFAULT * Juego.SCALE), 
            (int)(ALTO_DEFAULT * Juego.SCALE), 
            VIDA_DEFAULT);
        
        this.adminEnemigos = adminEnemigos;
        
        // El resto del constructor igual...
        inicializarEnemigo(24, 20, 72, 50, true, true);
        this.velocidadMovimiento = 0.5f * Juego.SCALE;
        this.velocidadX = -velocidadMovimiento;
        this.checkOffset = 20 * Juego.SCALE;
        this.puedeDisparar = true;
        this.disparoMaxCooldown = 60;
        this.rangoDeteccionJugador = 400 * Juego.SCALE;
        
        cargarAnimaciones();
    }
    
    @Override
    protected void determinarAnimacion() {
        // Si estamos disparando, priorizar esa animación
        if (disparoEnProceso) {
            if (!animaciones.esUltimoFrame()) {
                return;
            } else {
                disparoEnProceso = false;
            }
        }
        
        // Si recibimos daño, mostrar animación de daño
        if (animaciones.getAccionActual() == HERIDO && !animaciones.esUltimoFrame()) {
            return;
        }
        
        // Determinar animación por estado
        int nuevaAnimacion;
        
        if (enAire) {
            nuevaAnimacion = CORRER;
        } else if (velocidadX != 0) {
            nuevaAnimacion = CORRER;
        } else {
            nuevaAnimacion = INACTIVO; // Cuando está quieto
        }
        
        animaciones.setAccion(nuevaAnimacion);
    }
    
    @Override
    protected void cargarAnimaciones() {
        // Cargar la hoja de sprites del enemigo verde
        BufferedImage img = LoadSave.GetSpriteAtlas("enemigos/DISTUT180x180.png");
        
        spritesEnemigo = new BufferedImage[4][6]; // 3 acciones, máximo 6 frames
        
        // Ancho y alto de cada frame del sprite 
        int frameWidth = 180;
        int frameHeight = 180;
        int framesEnFila = 0;
        // Extraer cada frame de la hoja de sprites
        for (int j = 0; j < spritesEnemigo.length; j++) {
            switch (j) {
                case 0 -> framesEnFila = 6; // Inactivo/Idle
                case 1 -> framesEnFila = 6; // Correr/Moverse
                case 2 -> framesEnFila = 2; // Daño/Herido
                case 3 -> framesEnFila = 6; // Disparo
            }
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
        animaciones.setNumFramesPorAnimacion(DISPARO, 6);   // 2 frames para disparo
        
        // Establecer animación inicial
        animaciones.setAccion(CORRER);  // Comenzamos en animación de correr ya que estará en movimiento
    }
    
    // Sobrescribir renderizado para aplicar el ajuste específico
    @Override
    protected void renderizarConAnimacion(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (!activo) return;
        
        int drawX = (int) (hitbox.x - xDrawOffset) - xLvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffset) - yLvlOffset;
        
        if (movimientoHaciaIzquierda) {
            // Dibujar volteado horizontalmente con ajuste
            g.drawImage(animaciones.getImagenActual(),
                drawX + w - ajuste, drawY,
                -w, h, null);
        } else {
            // Dibujar normal
            g.drawImage(animaciones.getImagenActual(),
                drawX, drawY,
                w, h, null);
        }
    }

    @Override
    protected void disparar(float angulo) {
        // En lugar de crear la bala inmediatamente, iniciamos la animación
        disparoEnProceso = true;
        disparoPendiente = true;
        animaciones.setAccion(DISPARO);
        animaciones.resetearAnimacion();
        
        // Detenemos temporalmente el movimiento durante el disparo
        float velocidadOriginal = velocidadX;
        velocidadX = 0;
        
        // Programamos un timer para restaurar la velocidad cuando termine la animación
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    velocidadX = velocidadOriginal;
                }
            },
            animaciones.getNumFramesPorAnimacion(DISPARO) * (animaciones.getAnimVelocidad() * 16)
        );
    }

    @Override
    protected void manejarDisparo(Jugador jugador) {
        if (!puedeDisparar || !activo) return;
        
        // Si estamos en modo huida o al máximo de invocaciones, no disparar
        if (enModoHuida || enemigosInvocados.size() >= MAX_ENEMIGOS_INVOCADOS) {
            return;
        }
        
        // Reducir cooldown si está activo
        if (disparoCooldown > 0) {
            disparoCooldown--;
            return;
        }
        
        // Verificar si el jugador está en rango
        if (puedeVerJugador(jugador)) {
            // Detener movimiento temporalmente para disparar
            patrullando = false;
            velocidadX = 0;
            
            // Orientar hacia el jugador
            float jugadorX = jugador.getXCenter();
            float enemigoX = hitbox.x + hitbox.width/2;
            movimientoHaciaIzquierda = jugadorX < enemigoX;
            
            // Disparar
            float angulo = calcularAnguloHaciaJugador(jugador);
            disparar(angulo);
            disparoCooldown = disparoMaxCooldown;
            
            // Reanudar movimiento después de un tiempo, pero verificando si es seguro
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // Solo reanudar patrulla y movimiento si es seguro
                        patrullando = true;
                        
                        if (esSeguroMoverse()) {
                            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
                        } else {
                            // No es seguro moverse, mantener quieto pero seguir orientado al jugador
                            velocidadX = 0;
                        }
                    }
                },
                1000 // Reanudar después de 1 segundo
            );
        }
    }

    @Override
    public void update() {
        super.update();
        
        // Limpiar la lista de enemigos muertos
        limpiarEnemigosInvocadosMuertos();
        
        // Actualizar el estado de huida
        actualizarModoHuida();
        
        // Si estamos en modo huida, manejar el comportamiento de huida
        if (enModoHuida && Juego.jugadorActual != null) {
            manejarHuida(Juego.jugadorActual);
        }
        
        // Verificar si es momento de crear la bala durante la animación
        if (disparoPendiente && disparoEnProceso && 
            animaciones.getAccionActual() == DISPARO && 
            animaciones.getAnimIndice() == frameDisparo) {
            
            // Calcular posición de origen de la bala
            float origenX = hitbox.x + hitbox.width/2;
            float origenY = hitbox.y + hitbox.height/2;
            
            // Ajustar el origen según la dirección
            if (movimientoHaciaIzquierda) {
                origenX -= 20 * Juego.SCALE;
            } else {
                origenX += 20 * Juego.SCALE;
            }
            
            // Generar enemigos solo si no hemos alcanzado el límite
            if (puedeGenerarMasEnemigos()) {
                generarEnemigos(origenX, origenY);
            } else {
                System.out.println("Distut ha alcanzado el límite de invocaciones!");
            }
            
            // Ya disparamos, no repetir hasta la próxima animación
            disparoPendiente = false;
        }
        
        // Intentar detectar al jugador y disparar (solo si no estamos en modo huida)
        if (!disparoEnProceso && !enModoHuida && Juego.jugadorActual != null) {
            manejarDisparo(Juego.jugadorActual);
        }
    }
    
    // Método para actualizar el modo huida
    private void actualizarModoHuida() {
        limpiarEnemigosInvocadosMuertos();
        
        // Entrar en modo huida si tenemos el máximo de enemigos
        if (enemigosInvocados.size() >= MAX_ENEMIGOS_INVOCADOS && !enModoHuida) {
            enModoHuida = true;
            patrullando = false;
        } 
        // Salir del modo huida si tenemos menos del máximo
        else if (enemigosInvocados.size() < MAX_ENEMIGOS_INVOCADOS && enModoHuida) {
            enModoHuida = false;
            patrullando = true;
            // Restaurar velocidad normal de patrulla
            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
        }
    }
    
    // Método para manejar el comportamiento de huida
    private void manejarHuida(Jugador jugador) {
        if (!activo) return;
        
        float jugadorX = jugador.getXCenter();
        float enemigoX = hitbox.x + hitbox.width/2;
        float distancia = Math.abs(jugadorX - enemigoX);
        
        // Si estamos suficientemente lejos, volver a patrullar
        if (distancia >= DISTANCIA_SEGURA) {
            enModoHuida = false;
            patrullando = true;
            velocidadX = movimientoHaciaIzquierda ? -velocidadMovimiento : velocidadMovimiento;
            return;
        }
        
        // Si no estamos lo suficientemente lejos, intentar huir
        // Primero, determinar la dirección de huida ideal
        boolean deberiaIrDerecha = jugadorX < enemigoX;
        
        // Intentar moverse en la dirección ideal para huir
        if (deberiaIrDerecha) {
            movimientoHaciaIzquierda = false;
            velocidadX = VELOCIDAD_HUIDA;
        } else {
            movimientoHaciaIzquierda = true;
            velocidadX = -VELOCIDAD_HUIDA;
        }
        
        // Verificar si podemos movernos en esa dirección
        if (!esSeguroMoverse()) {
            // No podemos huir en la dirección ideal, intentar la opuesta
            movimientoHaciaIzquierda = !movimientoHaciaIzquierda;
            velocidadX = -velocidadX;
            
            // Si tampoco podemos ir en la dirección opuesta, estamos atrapados
            if (!esSeguroMoverse()) {
                // Atrapado: detenerse y mantener la animación de idle
                velocidadX = 0;
                
                // Mantener la orientación mirando hacia donde está el jugador
                movimientoHaciaIzquierda = jugadorX < enemigoX;
                
                // Asegurarse de que se muestre la animación de INACTIVO
                animaciones.setAccion(INACTIVO);
            }
        }
    }
    
    // Método para verificar si podemos generar más enemigos
    private boolean puedeGenerarMasEnemigos() {
        limpiarEnemigosInvocadosMuertos();
        return enemigosInvocados.size() < MAX_ENEMIGOS_INVOCADOS;
    }
    
    // Método para limpiar enemigos muertos de la lista
    private void limpiarEnemigosInvocadosMuertos() {
        enemigosInvocados.removeIf(enemigo -> !enemigo.estaActivo());
    }
    
    private void generarEnemigos(float origenX, float origenY) {
        if (adminEnemigos == null) return;
        
        // Generar enemigos pequeños
        for (int i = 0; i < numEnemigosGenerar; i++) {
            // Verificar nuevamente si podemos generar más enemigos
            if (!puedeGenerarMasEnemigos()) {
                break;
            }
            
            // Calcular posición aleatoria cercana
            float offsetX = (float) ((Math.random() * 100 - 50) * Juego.SCALE);
            float offsetY = (float) ((Math.random() * 20 - 40) * Juego.SCALE); // Aparecer encima
            
            // Crear un enemigo pequeño (usamos Skeler que es más pequeño)
            Enemigo nuevoEnemigo = adminEnemigos.crearEnemigoSkeler(origenX + offsetX, origenY + offsetY);
            
            // Añadir a la lista de enemigos invocados
            if (nuevoEnemigo != null) {
                enemigosInvocados.add(nuevoEnemigo);
            }
        }
    }
}