package Elementos.Quimica;

import java.util.HashMap;
import java.util.Map;

public class InventarioCompuestos {
    private Map<String, CompuestoQuimico> compuestos;
    
    public InventarioCompuestos() {
        compuestos = new HashMap<>();
    }
    
    public void agregarCompuesto(CompuestoQuimico compuesto) {
        compuestos.put(compuesto.getFormula(), compuesto);
    }
    
    public CompuestoQuimico getCompuesto(String formula) {
        return compuestos.get(formula);
    }
    
    public boolean tieneCompuestoSuficiente(String formula, int cantidad) {
        CompuestoQuimico compuesto = compuestos.get(formula);
        return compuesto != null && compuesto.getCantidad() >= cantidad;
    }
    
    public Map<String, CompuestoQuimico> getCompuestos() {
        return compuestos;
    }
}