package Elementos.Quimica;

public class CompuestoQuimico {
    private String nombre;
    private String formula;
    private int cantidad;
    
    public CompuestoQuimico(String nombre, String formula) {
        this.nombre = nombre;
        this.formula = formula;
        this.cantidad = 0;
    }
    
    public String getNombre() { return nombre; }
    public String getFormula() { return formula; }
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