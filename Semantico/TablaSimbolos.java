import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    private String nombreAmbito;             
    private TablaSimbolos padre;             
    private Map<String, Simbolo> tabla;       
    private int contadorMemoria;             

    public TablaSimbolos(String nombreAmbito) {
        this(nombreAmbito, null);
    }

    public TablaSimbolos(String nombreAmbito, TablaSimbolos padre) {
        this.nombreAmbito = nombreAmbito;
        this.padre = padre;
        this.tabla = new HashMap<>();
        this.contadorMemoria = (padre != null) ? padre.contadorMemoria : 0;
    }
  
    public boolean insertar(String nombre, String tipoDato, Object valorInicial, int linea) {
        if (tabla.containsKey(nombre)) {
            System.err.println("Error semántico en línea " + linea + ": La variable '" + nombre + "' ya fue declarada en el ámbito '" + nombreAmbito + "'.");
            return false;
        }

        int dirMemoria = this.contadorMemoria;

        Simbolo simbolo = new Simbolo(nombre, tipoDato, this.nombreAmbito, valorInicial, linea, dirMemoria);
        
        tabla.put(nombre, simbolo);

        this.contadorMemoria += Simbolo.getTamanioEnBytes(tipoDato);

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

    public String getNombreAmbito() {
        return nombreAmbito;
    }

    public TablaSimbolos getPadre() {
        return padre;
    }

    public Map<String, Simbolo> getTabla() {
        return tabla;
    }

    public int getContadorMemoria() {
        return contadorMemoria;
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
