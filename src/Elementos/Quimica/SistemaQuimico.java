package Elementos.Quimica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Elementos.Arma;
import Elementos.Armas.ArmaMercurio;
import Elementos.Administradores.AdministradorBalas;

public class SistemaQuimico {
    private InventarioQuimico inventarioElementos;
    private InventarioCompuestos inventarioCompuestos;
    private List<RecetaCompuesto> recetasCompuestos;
    private Map<String, Class<? extends Arma>> recetasArmas;
    
    public SistemaQuimico() {
        inventarioElementos = new InventarioQuimico();
        inventarioCompuestos = new InventarioCompuestos();
        recetasCompuestos = new ArrayList<>();
        recetasArmas = new HashMap<>();
        
        inicializarRecetasCompuestos();
        inicializarRecetasArmas();
    }
    
    private void inicializarRecetasCompuestos() {
        // Crear algunas recetas de ejemplo
        RecetaCompuesto sulfatoCobre = new RecetaCompuesto("Sulfato de Cobre", "CuSO4");
        sulfatoCobre.agregarElemento("Cu", 1);
        sulfatoCobre.agregarElemento("S", 1);
        sulfatoCobre.agregarElemento("O", 4);
        recetasCompuestos.add(sulfatoCobre);
        
        RecetaCompuesto cloruroSodio = new RecetaCompuesto("Cloruro de Sodio", "NaCl");
        cloruroSodio.agregarElemento("Na", 1);
        cloruroSodio.agregarElemento("Cl", 1);
        recetasCompuestos.add(cloruroSodio);
        
        // Añadir más recetas según sea necesario
    }
    
    private void inicializarRecetasArmas() {
        // Asociar compuestos con armas
        recetasArmas.put("CuSO4", ArmaMercurio.class);
        // Añadir más recetas de armas
    }
    
    public List<RecetaCompuesto> getRecetasDisponibles() {
        return recetasCompuestos;
    }
    
    public boolean crearCompuesto(String formula) {
        // Buscar la receta
        RecetaCompuesto receta = null;
        for (RecetaCompuesto r : recetasCompuestos) {
            if (r.getFormula().equals(formula)) {
                receta = r;
                break;
            }
        }
        
        if (receta == null) {
            return false;
        }
        
        // Verificar si tiene los elementos necesarios
        if (!receta.puedeCrearCon(inventarioElementos)) {
            return false;
        }
        
        // Consumir los elementos
        receta.consumirElementos(inventarioElementos);
        
        // Crear o aumentar el compuesto
        CompuestoQuimico compuesto = inventarioCompuestos.getCompuesto(formula);
        if (compuesto == null) {
            compuesto = new CompuestoQuimico(receta.getNombre(), formula);
            inventarioCompuestos.agregarCompuesto(compuesto);
        }
        compuesto.aumentarCantidad(1);
        
        return true;
    }
    
    public Arma crearArma(String formulaCompuesto, AdministradorBalas adminBalas) throws Exception {
        // Verificar si tenemos el compuesto
        if (!inventarioCompuestos.tieneCompuestoSuficiente(formulaCompuesto, 1)) {
            return null;
        }
        
        // Verificar si existe una receta para este compuesto
        Class<? extends Arma> claseArma = recetasArmas.get(formulaCompuesto);
        if (claseArma == null) {
            return null;
        }
        
        // Consumir el compuesto
        CompuestoQuimico compuesto = inventarioCompuestos.getCompuesto(formulaCompuesto);
        compuesto.consumir(1);
        
        // Crear el arma usando reflexión
        return claseArma.getConstructor(AdministradorBalas.class).newInstance(adminBalas);
    }
    
    public InventarioQuimico getInventarioElementos() {
        return inventarioElementos;
    }
    
    public InventarioCompuestos getInventarioCompuestos() {
        return inventarioCompuestos;
    }
}