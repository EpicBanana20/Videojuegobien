package Elementos.Quimica;

import java.util.HashMap;
import java.util.Map;

public class InventarioQuimico {
    private Map<String, ElementoQuimico> elementos;
    
    public InventarioQuimico() {
        elementos = new HashMap<>();
        inicializarElementos();
    }
    
    private void inicializarElementos() {
        // Inicializar todos los elementos disponibles
        agregarElemento(new ElementoQuimico("Na", "Sodio"));
        agregarElemento(new ElementoQuimico("Cl", "Cloro"));
        agregarElemento(new ElementoQuimico("S", "Azufre"));
        agregarElemento(new ElementoQuimico("C", "Carbono"));
        agregarElemento(new ElementoQuimico("O", "Oxígeno"));
        agregarElemento(new ElementoQuimico("Ag", "Plata"));
        agregarElemento(new ElementoQuimico("Cu", "Cobre"));
        agregarElemento(new ElementoQuimico("B", "Boro"));
        agregarElemento(new ElementoQuimico("P", "Fósforo"));
        agregarElemento(new ElementoQuimico("I", "Yodo"));
        agregarElemento(new ElementoQuimico("Pt", "Platino"));
        agregarElemento(new ElementoQuimico("Cs", "Cesio"));
        agregarElemento(new ElementoQuimico("As", "Arsénico"));
        agregarElemento(new ElementoQuimico("Br", "Bromo"));
    }
    
    private void agregarElemento(ElementoQuimico elemento) {
        elementos.put(elemento.getSimbolo(), elemento);
    }
    
    public ElementoQuimico getElemento(String simbolo) {
        return elementos.get(simbolo);
    }
    
    public boolean tieneElementosSuficientes(String simbolo, int cantidad) {
        ElementoQuimico elemento = elementos.get(simbolo);
        return elemento != null && elemento.getCantidad() >= cantidad;
    }
    
    public void recolectarElemento(String simbolo, int cantidad) {
        ElementoQuimico elemento = elementos.get(simbolo);
        if (elemento != null) {
            elemento.aumentarCantidad(cantidad);
        }
    }
    
    public Map<String, ElementoQuimico> getElementos() {
        return elementos;
    }
}