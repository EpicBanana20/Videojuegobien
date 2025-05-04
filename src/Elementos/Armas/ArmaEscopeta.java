package Elementos.Armas;

import Elementos.Arma;
import Elementos.Bala;
import Elementos.Administradores.AdministradorBalas;
import Juegos.Juego;
import Utilz.LoadSave;
import Elementos.AimController;

public class ArmaEscopeta extends Arma {
    private int municionActual = 8;
    private int capacidadCargador = 8;
    private boolean recargando = false;
    private int tiempoRecargaCompleta = 180; // 3 segundos
    private int contadorRecargaCompleta = 0;
    private int armaCooldown = 60; // 1 segundo entre disparos
    private int contadorRecarga = 0;
    
    // Características únicas de la escopeta
    private int numPerdigones = 6; // Número de balas por disparo
    private float dispersion = 0.3f; // Ángulo de dispersión en radianes
    
    public ArmaEscopeta(AdministradorBalas adminBalas) {
        super("armas/escopeta.png", 30 * Juego.SCALE, 3.0f, adminBalas);
        this.nombre = "Escopeta";
        this.tipoDaño = "fuego"; // Hace más daño al BOSS1
    }
    
    @Override
    public void disparar() {
        if (contadorRecarga <= 0 && municionActual > 0 && !recargando) {
            // Calcular posición base del disparo
            float[] posicionDisparo = new float[2];
            float distanciaCañon = 20 * Juego.SCALE;
            
            AimController.getPositionAtDistance(
                x, y,
                distanciaCañon,
                rotacion,
                posicionDisparo
            );
            
            // Disparar múltiples perdigones con dispersión
            for (int i = 0; i < numPerdigones; i++) {
                // Calcular ángulo con dispersión aleatoria
                float anguloDispersion = rotacion + (float)(Math.random() - 0.5) * dispersion;
                
                // Crear cada perdigón
                Bala perdigon = new Bala(
                    posicionDisparo[0], 
                    posicionDisparo[1], 
                    anguloDispersion,
                    LoadSave.BULLET_MACHINEGUN, // Usar sprite temporal
                    3, // Menos daño por perdigón
                    3.5f, // Velocidad alta
                    "fuego"
                );
                
                adminBalas.agregarBala(perdigon);
            }
            
            // Consumir munición y establecer cooldown
            municionActual--;
            contadorRecarga = armaCooldown;
            
            System.out.println("¡Escopetazo! Munición: " + municionActual);
        } else if (municionActual <= 0 && !recargando) {
            iniciarRecarga();
        }
    }
    
    @Override
    public void update(float playerX, float playerY, AimController aimController) {
        super.update(playerX, playerY, aimController);
        
        if (contadorRecarga > 0) {
            contadorRecarga--;
        }
        
        if (recargando) {
            contadorRecargaCompleta--;
            if (contadorRecargaCompleta <= 0) {
                completarRecarga();
            }
        }
    }
    
    private void iniciarRecarga() {
        if (!recargando && municionActual < capacidadCargador) {
            recargando = true;
            contadorRecargaCompleta = tiempoRecargaCompleta;
            System.out.println("Recargando escopeta...");
        }
    }
    
    private void completarRecarga() {
        municionActual = capacidadCargador;
        recargando = false;
        System.out.println("¡Escopeta recargada!");
    }
}