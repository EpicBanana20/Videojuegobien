package Elementos;

import java.awt.Graphics;
import java.util.ArrayList;
import Elementos.Enemigos.EnemigoVerde;

public class AdministradorEnemigos {
    // Lista para almacenar todos los enemigos activos
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    
    // Método para añadir un nuevo enemigo a la lista
    public void agregarEnemigo(Enemigo nuevoEnemigo) {
        enemigos.add(nuevoEnemigo);
    }
    
    // Actualizar todos los enemigos y eliminar los inactivos
    public void update() {
        // Crear una lista temporal para los enemigos a eliminar
        ArrayList<Enemigo> enemigosAEliminar = new ArrayList<>();
        
        // Primero actualizar todos los enemigos y marcar los que hay que eliminar
        for (Enemigo enemigo : enemigos) {
            enemigo.update();
            
            // Si el enemigo ya no está activo, lo marcamos para eliminar
            if (!enemigo.estaActivo()) {
                enemigosAEliminar.add(enemigo);
            }
        }
        
        // Eliminar todos los enemigos marcados de una vez
        if (!enemigosAEliminar.isEmpty()) {
            enemigos.removeAll(enemigosAEliminar);
        }
    }
    
    // Renderizar todos los enemigos
    public void render(Graphics g, int xLvlOffset, int ylvlOffset) {
        // Creamos una copia para evitar problemas de concurrencia
        ArrayList<Enemigo> enemigosSeguro = new ArrayList<>(enemigos);
        for (Enemigo enemigo : enemigosSeguro) {
            enemigo.render(g, xLvlOffset, ylvlOffset);
        }
    }
    
    // Comprobar colisiones de balas con enemigos
    public void comprobarColisionesBalas(AdministradorBalas adminBalas) {
        if (adminBalas == null || enemigos.isEmpty()) return;
        
        ArrayList<Bala> balas = adminBalas.getBalas();
        
        for (Bala bala : balas) {
            if (bala.estaActiva()) {
                for (Enemigo enemigo : enemigos) {
                    if (enemigo.estaActivo() && bala.getHitBox().intersects(enemigo.getHitBox())) {
                        // La bala impactó en el enemigo
                        enemigo.recibirDaño(bala.getDaño());
                        bala.desactivar();
                        break; // Una bala solo puede impactar a un enemigo
                    }
                }
            }
        }
    }
    
    // Getter para acceder a la lista de enemigos
    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }
    
    // Método para limpiar todos los enemigos (útil al cambiar de nivel)
    public void limpiarEnemigos() {
        enemigos.clear();
    }
    
    // Métodos de fábrica para crear diferentes tipos de enemigos
    
    // Crear un enemigo verde en una posición determinada
    public EnemigoVerde crearEnemigoVerde(float x, float y) {
        EnemigoVerde nuevoEnemigo = new EnemigoVerde(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }
    
    // Método para crear varios enemigos verdes distribuidos
    public void crearGrupoEnemigosVerdes(int cantidad, float xInicial, float yInicial, float separacion) {
        for (int i = 0; i < cantidad; i++) {
            float x = xInicial + i * separacion;
            crearEnemigoVerde(x, yInicial);
        }
    }
    
    // Aquí puedes añadir más métodos para crear otros tipos de enemigos en el futuro
}