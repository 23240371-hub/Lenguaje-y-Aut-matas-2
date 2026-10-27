import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalabrasReservadas {

    // 1. Lista simple con las palabras reservadas en texto
    public static final List<String> LISTA_PALABRAS = new ArrayList<>();

    // 2. Mapa para asociar la palabra con su token (igual que en Operadores)
    private static final Map<String, String> PALABRAS_RESERVADAS = new HashMap<>();

    static {
        // Carga de la lista
        LISTA_PALABRAS.add("int");
        LISTA_PALABRAS.add("float");
        LISTA_PALABRAS.add("boolean");
        LISTA_PALABRAS.add("double");
        LISTA_PALABRAS.add("print");
        LISTA_PALABRAS.add("main");
        LISTA_PALABRAS.add("class");

        // Carga del mapa para identificación rápida
        PALABRAS_RESERVADAS.put("int", "PR_INT");
        PALABRAS_RESERVADAS.put("float", "PR_FLOAT");
        PALABRAS_RESERVADAS.put("boolean", "PR_BOOLEAN");
        PALABRAS_RESERVADAS.put("double", "PR_DOUBLE");
        PALABRAS_RESERVADAS.put("print", "PR_PRINT");
        PALABRAS_RESERVADAS.put("main", "PR_MAIN");
        PALABRAS_RESERVADAS.put("class", "PR_CLASS");
    }

    /**
     * Devuelve el tipo de token (ej. "PR_INT") o null si no es palabra reservada.
     */
    public static String getTipoPalabra(String palabra) {
        if (palabra == null) return null;
        return PALABRAS_RESERVADAS.get(palabra);
    }

    /**
     * Valida si una palabra ingresada es una palabra reservada.
     */
    public static boolean esPalabraReservada(String palabra) {
        if (palabra == null) return false;
        return PALABRAS_RESERVADAS.containsKey(palabra);
    }
}
