package Elementos.Quimica;

import java.util.HashMap;
import java.util.Map;

public class RecetaCompuesto {
    private String nombre;
    private String formula;
    private Map<String, Integer> elementos;
    
    public RecetaCompuesto(String nombre, String formula) {
        this.nombre = nombre;
        this.formula = formula;
        this.elementos = new HashMap<>();
    }
    
    public void agregarElemento(String simbolo, int cantidad) {
        elementos.put(simbolo, cantidad);
    }
    
    public String getNombre() { return nombre; }
    public String getFormula() { return formula; }
    public Map<String, Integer> getElementos() { return elementos; }
    
    public boolean puedeCrearCon(InventarioQuimico inventario) {
        for (Map.Entry<String, Integer> entry : elementos.entrySet()) {
            if (!inventario.tieneElementosSuficientes(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    public void consumirElementos(InventarioQuimico inventario) {
        for (Map.Entry<String, Integer> entry : elementos.entrySet()) {
            ElementoQuimico elemento = inventario.getElemento(entry.getKey());
            elemento.consumir(entry.getValue());
        }
    }
}