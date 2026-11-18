package prueba;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    private String nombreAmbito;
    private TablaSimbolos padre;
    private Map<String, Simbolo> tabla;
    private int offsetGlobal; 
    private ManejadorErrores manejadorErrores;

    public TablaSimbolos(String nombreAmbito, ManejadorErrores manejador) {
        this(nombreAmbito, null, manejador);
    }

    public TablaSimbolos(String nombreAmbito, TablaSimbolos padre, ManejadorErrores manejador) {
        this.nombreAmbito = nombreAmbito;
        this.padre = padre;
        this.tabla = new HashMap<>();
        this.offsetGlobal = 0;
        this.manejadorErrores = manejador;
    }

    private TablaSimbolos getRaiz() {
        TablaSimbolos actual = this;
        while (actual.padre != null) {
            actual = actual.padre;
        }
        return actual;
    }

    public boolean insertar(String nombre, String tipoDato, Object valorInicial, int linea) {
        if (tabla.containsKey(nombre)) {
            if (manejadorErrores != null) {
                manejadorErrores.agregarErrorSemantico(
                    linea,
                    "La variable '" + nombre + "' ya fue declarada en el ámbito '" + nombreAmbito + "'."
                );
            }
            return false;
        }

        int tamanioBytes = Simbolo.getTamanioEnBytes(tipoDato);

        TablaSimbolos raiz = getRaiz();
        int offsetActual = raiz.offsetGlobal;

        Simbolo simbolo = new Simbolo(nombre, tipoDato, this.nombreAmbito, valorInicial, linea, offsetActual);
        tabla.put(nombre, simbolo);

        raiz.offsetGlobal += tamanioBytes;

        return true;
    }

    public Simbolo buscar(String nombre) {
        if (tabla.containsKey(nombre)) {
            return tabla.get(nombre);
        } else if (padre != null) {
            return padre.buscar(nombre);
        }
        return null;
    }

    public boolean existe(String nombre) {
        return buscar(nombre) != null;
    }

    public String getNombreAmbito() { return nombreAmbito; }
    public TablaSimbolos getPadre() { return padre; }
    public Map<String, Simbolo> getTabla() { return tabla; }

    public int getMemoriaTotalAsignada() {
        return getRaiz().offsetGlobal;
    }

    public void imprimirTabla() {
        System.out.println("\n=== TABLA DE SÍMBOLOS (Ámbito: " + nombreAmbito + ") ===");
        if (tabla.isEmpty()) {
            System.out.println("  [Ámbito vacío]");
        } else {
            for (Simbolo s : tabla.values()) {
                System.out.println("  " + s);
            }
        }
    }
}
