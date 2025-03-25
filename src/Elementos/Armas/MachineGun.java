package Elementos.Armas;

import Elementos.Arma;
import Elementos.Bala;
import Juegos.Juego;
import Elementos.AimController;

public class MachineGun extends Arma {
    // Cadencia en disparos por segundo
    private float cadenciaDisparo = 5.0f; // 5 disparos por segundo
    private int contadorRecarga = 0;
    
    // Sistema de munición
    private int municionActual = 30;
    private int capacidadCargador = 30;
    private boolean recargando = false;
    private int tiempoRecargaCompleta = 120; // 2 segundos a 60 FPS
    private int contadorRecargaCompleta = 0;
    
    // Convertimos la cadencia en tiempo entre disparos (en frames)
    private int armaCooldown;
    private static final int FRAMES_POR_SEGUNDO = 60;

    public MachineGun() {
        super("armas/machinegun.png", 30 * Juegos.Juego.SCALE, 3.0f);
        this.nombre = "MachineGun";
        this.armaCooldown = Math.round(FRAMES_POR_SEGUNDO / cadenciaDisparo);
    }
    
    @Override
    public void disparar() {
        // Verificar si podemos disparar (no en cooldown Y tenemos munición)
        if(contadorRecarga <= 0 && municionActual > 0 && !recargando) {
            System.out.println("¡Disparando ametralladora! Munición restante: " + (municionActual-1));
            
            // Calcular la posición exacta del origen de la bala
            float[] posicionDisparo = new float[2];
            float distanciaCañon = 20 * Juego.SCALE;
            
            AimController.getPositionAtDistance(
                x, y,
                distanciaCañon,
                rotacion,
                posicionDisparo
            );
            
            // Crear una nueva bala
            Bala nuevaBala = new Bala(
                posicionDisparo[0], 
                posicionDisparo[1], 
                rotacion
            );
            
            // Añadir la bala al administrador
            adminBalas.agregarBala(nuevaBala);
            
            // Consumir munición
            municionActual--;
            
            // Reiniciar contador de cooldown
            contadorRecarga = armaCooldown;
        } else if (municionActual <= 0 && !recargando) {
            // Auto-recarga cuando nos quedamos sin munición
            iniciarRecarga();
        }
    }
    
    @Override
    public void update(float playerX, float playerY, AimController aimController) {
        super.update(playerX, playerY, aimController);
        
        // Actualizar contador de cooldown
        if(contadorRecarga > 0) {
            contadorRecarga--;
        }
        
        // Manejar la recarga
        if(recargando) {
            contadorRecargaCompleta--;
            if(contadorRecargaCompleta <= 0) {
                completarRecarga();
            }
        }
    }
    
    // Método para iniciar la recarga manual
    public void iniciarRecarga() {
        if(!recargando && municionActual < capacidadCargador) {
            recargando = true;
            contadorRecargaCompleta = tiempoRecargaCompleta;
            System.out.println("Recargando ametralladora...");
        }
    }
    
    // Método para completar la recarga
    private void completarRecarga() {
        municionActual = capacidadCargador;
        recargando = false;
        System.out.println("¡Recarga completa! Munición: " + municionActual);
    }
    
    // Getters
    public float getCadenciaDisparo() {
        return cadenciaDisparo;
    }
    
    public int getMunicionActual() {
        return municionActual;
    }
    
    public boolean estaRecargando() {
        return recargando;
    }
    
    public int getCapacidadCargador() {
        return capacidadCargador;
    }
}