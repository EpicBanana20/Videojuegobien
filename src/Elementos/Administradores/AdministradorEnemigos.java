package Elementos.Administradores;

import java.awt.Graphics;
import java.util.ArrayList;

import Elementos.Bala;
import Elementos.Enemigo;
import Elementos.Enemigos.BossRadon;
import Elementos.Enemigos.BossZefir;
import Elementos.Enemigos.EnemigoAranara;
import Elementos.Enemigos.EnemigoEnergy;
import Elementos.Enemigos.EnemigoHibit;
import Elementos.Enemigos.EnemigoKoko;
import Elementos.Enemigos.EnemigoPiedrora;
import Elementos.Enemigos.EnemigoSkeler;
import Elementos.Enemigos.EnemigoThor;
import Elementos.Enemigos.EnemigoVerde;
import Elementos.Enemigos.EnemigoYbirth;
import Elementos.Enemigos.Ommi;

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
    public Ommi crearEnemigoHongo(float x, float y) {
        Ommi nuevoEnemigo = new Ommi(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoYbirth crearEnemigoPlanta(float x, float y){
        EnemigoYbirth nuevoEnemigo = new EnemigoYbirth(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoKoko crearEnemigoCocodrilo(float x, float y){
        EnemigoKoko nuevoEnemigo = new EnemigoKoko(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoThor crearEnemigoTortuga(float x, float y){
        EnemigoThor nuevoEnemigo = new EnemigoThor(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public BossRadon crearBossRadon(float x, float y){
        BossRadon nuevoEnemigo = new BossRadon(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoAranara crearEnemigoAranara(float x, float y){
        EnemigoAranara nuevoEnemigo = new EnemigoAranara(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoEnergy crearEnemigoEnergy(float x, float y){
        EnemigoEnergy nuevoEnemigo = new EnemigoEnergy(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoHibit crearEnemigoHibit(float x, float y){
        EnemigoHibit nuevoEnemigo = new EnemigoHibit(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoPiedrora crearEnemigoPiedrora(float x, float y){
        EnemigoPiedrora nuevoEnemigo = new EnemigoPiedrora(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public EnemigoSkeler crearEnemigoSkeler(float x, float y){
        EnemigoSkeler nuevoEnemigo = new EnemigoSkeler(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }

    public BossZefir crearBossZefir(float x, float y){
        BossZefir nuevoEnemigo = new BossZefir(x, y);
        agregarEnemigo(nuevoEnemigo);
        return nuevoEnemigo;
    }
    // Aquí puedes añadir más métodos para crear otros tipos de enemigos en el futuro
}