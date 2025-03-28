package Elementos.Administradores;

import java.awt.Graphics;
import java.util.ArrayList;

import Elementos.Bala;
import Juegos.Juego;

public class AdministradorBalas {
    // Lista para almacenar todas las balas activas
    private ArrayList<Bala> balas = new ArrayList<>();
    
    // Método para añadir una nueva bala a la lista
    public void agregarBala(Bala nuevaBala) {
        balas.add(nuevaBala);
    }
    
    // Actualizar todas las balas y eliminar las inactivas
    public void update() {
        // Crear una lista temporal para las balas a eliminar
        ArrayList<Bala> balasAEliminar = new ArrayList<>();
        
        // Primero actualizar todas las balas y marcar las que hay que eliminar
        for (Bala bala : balas) {
            bala.update();
            
            // Verificar si está fuera de límites (y desactivarla si es así)
            bala.fueraDeLimites(Juego.NIVEL_ACTUAL_ANCHO, Juego.NIVEL_ACTUAL_ALTO);
            bala.colisionConBloque();
            // Si la bala ya no está activa, la marcamos para eliminar
            if (!bala.estaActiva()) {
                balasAEliminar.add(bala);
            }
        }
        
        // Eliminar todas las balas marcadas de una vez
        if (!balasAEliminar.isEmpty()) {
            balas.removeAll(balasAEliminar);
        }
    }
    
    // Renderizar todas las balas
    public void render(Graphics g, int xLvlOffset, int ylvlOffset) {
        ArrayList<Bala> balasSeguras = new ArrayList<>(balas);
        for (Bala bala : balasSeguras) {
            bala.render(g, xLvlOffset, ylvlOffset);
        }
    }
    
    // Getter para acceder a la lista de balas (útil para detección de colisiones)
    public ArrayList<Bala> getBalas() {
        return balas;
    }
    
    // Método para limpiar todas las balas (útil al cambiar de nivel)
    public void limpiarBalas() {
        balas.clear();
    }
}