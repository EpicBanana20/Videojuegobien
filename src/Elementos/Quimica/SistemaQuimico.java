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
        RecetaCompuesto decaoxidoTetrafosforo = new RecetaCompuesto("Decaoxido de tetrafósforo", "P4O10");
        decaoxidoTetrafosforo.agregarElemento("P", 4);
        decaoxidoTetrafosforo.agregarElemento("O", 10);
        recetasCompuestos.add(decaoxidoTetrafosforo);
        

        RecetaCompuesto trioxidoAzufre = new RecetaCompuesto("Trióxido de azufre", "SO3");
        trioxidoAzufre.agregarElemento("S", 1);
        trioxidoAzufre.agregarElemento("O", 3);
        recetasCompuestos.add(trioxidoAzufre);

        RecetaCompuesto tribromuroBoro = new RecetaCompuesto("Tribromuro de boro", "BBr3");
        tribromuroBoro.agregarElemento("B", 1);
        tribromuroBoro.agregarElemento("Br", 3);
        recetasCompuestos.add(tribromuroBoro);

        RecetaCompuesto yoduroPlata = new RecetaCompuesto("Yoduro de plata", "AgI");
        yoduroPlata.agregarElemento("Ag", 1);
        yoduroPlata.agregarElemento("I", 1);
        recetasCompuestos.add(yoduroPlata);

        RecetaCompuesto sulfatoCobreIII = new RecetaCompuesto("Sulfato de cobre(III)", "CuSO4");
        sulfatoCobreIII.agregarElemento("Cu", 1);
        sulfatoCobreIII.agregarElemento("S", 1);
        sulfatoCobreIII.agregarElemento("O", 4);
        recetasCompuestos.add(sulfatoCobreIII);

        RecetaCompuesto cloruroCesio = new RecetaCompuesto("Cloruro de cesio", "CsCl");
        cloruroCesio.agregarElemento("Cs", 1);
        cloruroCesio.agregarElemento("Cl", 1);
        recetasCompuestos.add(cloruroCesio);

        RecetaCompuesto arsenitoSodio = new RecetaCompuesto("Arsenito de sodio", "Na3AsO3");
        arsenitoSodio.agregarElemento("Na", 1);
        arsenitoSodio.agregarElemento("As", 1);
        arsenitoSodio.agregarElemento("O", 2);
        recetasCompuestos.add(arsenitoSodio);

        RecetaCompuesto cloruroPlatinoII = new RecetaCompuesto("Cloruro de platino(II)", "PtCl2");
        cloruroPlatinoII.agregarElemento("Pt", 1);
        cloruroPlatinoII.agregarElemento("Cl", 2);
        recetasCompuestos.add(cloruroPlatinoII);

        RecetaCompuesto tetacloruroCarbono = new RecetaCompuesto("Tetacloruro de carbono", "CCl4");
        tetacloruroCarbono.agregarElemento("C", 1);
        tetacloruroCarbono.agregarElemento("Cl", 4);
        recetasCompuestos.add(tetacloruroCarbono);
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