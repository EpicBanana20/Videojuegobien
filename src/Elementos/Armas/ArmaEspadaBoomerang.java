package Elementos.Armas;

import Elementos.Arma;
import Elementos.Enemigo;
import Elementos.Administradores.AdministradorBalas;
import Juegos.Juego;
import Elementos.AimController;

public class ArmaEspadaBoomerang extends Arma {
    private boolean espadaLanzada = false;
    private boolean regresando = false;
    private float posicionXInicial, posicionYInicial;
    private float posicionXObjetivo, posicionYObjetivo;
    private float velocidadEspada = 1.0f * Juego.SCALE;
    private float distanciaMaxima = 300.0f * Juego.SCALE;
    private float distanciaRecorrida = 0;
    private int dañoEspada = 20;
    
    private float dx, dy;
    private float jugadorX, jugadorY;
    private boolean colisionDetectada = false;
    private int tiempoEspera = 0;
    private int cooldownLanzamiento = 0;
    private final int COOLDOWN_MAX = 15;
    
    public ArmaEspadaBoomerang(AdministradorBalas adminBalas) {
        super("armas/ESPADA.png", 35 * Juego.SCALE, 0.2f, adminBalas);
        this.nombre = "Espada";
        this.tipoDaño = "";
    }
    
    // Método para reiniciar la espada en la posición del jugador
    public void reiniciarPosicion(float playerX, float playerY) {
        this.x = playerX;
        this.y = playerY;
        this.espadaLanzada = false;
        this.regresando = false;
        this.colisionDetectada = false;
        this.distanciaRecorrida = 0;
        this.cooldownLanzamiento = 0;
    }
    
    public void forzarRegreso() {
        if (espadaLanzada) {
            regresando = true;
            colisionDetectada = false;
        }
    }
    
    @Override
    public void disparar() {
        if (!espadaLanzada && cooldownLanzamiento <= 0) {
            espadaLanzada = true;
            regresando = false;
            colisionDetectada = false;
            distanciaRecorrida = 0;
            
            posicionXInicial = x;
            posicionYInicial = y;
            
            posicionXObjetivo = posicionXInicial + (float) Math.cos(rotacion) * distanciaMaxima;
            posicionYObjetivo = posicionYInicial + (float) Math.sin(rotacion) * distanciaMaxima;
            
            float distanciaTotal = (float) Math.sqrt(
                Math.pow(posicionXObjetivo - posicionXInicial, 2) + 
                Math.pow(posicionYObjetivo - posicionYInicial, 2)
            );
            
            dx = (posicionXObjetivo - posicionXInicial) / distanciaTotal * velocidadEspada;
            dy = (posicionYObjetivo - posicionYInicial) / distanciaTotal * velocidadEspada;
        }
    }
    
    @Override
    public void update(float playerX, float playerY, AimController aimController) {
        if (cooldownLanzamiento > 0) {
            cooldownLanzamiento--;
        }
        
        jugadorX = playerX;
        jugadorY = playerY;
        
        if (!espadaLanzada) {
            super.update(playerX, playerY, aimController);
        } else {
            if (!regresando && !colisionDetectada) {
                x += dx;
                y += dy;
                
                distanciaRecorrida += Math.sqrt(dx*dx + dy*dy);
                
                if (distanciaRecorrida >= distanciaMaxima) {
                    regresando = true;
                }
                
                if (Utilz.MetodoAyuda.isSolido(x, y, Juego.NIVEL_ACTUAL_DATA)) {
                    colisionDetectada = true;
                    tiempoEspera = 10;
                }
                
                comprobarColisionConEnemigos();
            } else if (colisionDetectada) {
                tiempoEspera--;
                if (tiempoEspera <= 0) {
                    regresando = true;
                    colisionDetectada = false;
                }
            } else if (regresando) {
                float dirX = jugadorX - x;
                float dirY = jugadorY - y;
                float distancia = (float) Math.sqrt(dirX * dirX + dirY * dirY);
                
                if (distancia < 20 * Juego.SCALE) {
                    espadaLanzada = false;
                    regresando = false;
                    distanciaRecorrida = 0;
                    cooldownLanzamiento = COOLDOWN_MAX;

                    x = jugadorX;
                    y = jugadorY;
                    return;
                }
                
                float velocidadRegreso = velocidadEspada * 1.5f;
                dirX = dirX / distancia * velocidadRegreso;
                dirY = dirY / distancia * velocidadRegreso;
                
                x += dirX;
                y += dirY;
                
                rotacion = (float) Math.atan2(dirY, dirX);
            }
        }
    }
    
    private void comprobarColisionConEnemigos() {
        if (Juego.ADMIN_ENEMIGOS != null) {
            for (Enemigo enemigo : Juego.ADMIN_ENEMIGOS.getEnemigos()) {
                if (enemigo.estaActivo() && enemigo.getHitBox().contains(x, y)) {
                    enemigo.recibirDaño(dañoEspada, tipoDaño);
                    colisionDetectada = true;
                    tiempoEspera = 10;
                    break;
                }
            }
        }
    }
    
    
    public boolean estaEspadaLanzada() {
        return espadaLanzada;
    }
    
    public boolean puedeDisparar() {
        return !espadaLanzada && cooldownLanzamiento <= 0;
    }
}