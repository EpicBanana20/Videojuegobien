package Elementos.Quimica;

public class ElementoQuimico {
    private String simbolo;
    private String nombre;
    private int cantidad;
    
    public ElementoQuimico(String simbolo, String nombre) {
        this.simbolo = simbolo;
        this.nombre = nombre;
        this.cantidad = 0;
    }
    
    // Getters y setters
    public String getSimbolo() { return simbolo; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    
    public void aumentarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }
    
    public boolean consumir(int cantidad) {
        if (this.cantidad >= cantidad) {
            this.cantidad -= cantidad;
            return true;
        }
        return false;
    }
}