import java.util.HashMap;
import java.util.Map;

public class Operadores {

    //Metodo estatico de hashmap que no varia de extension desde su creacion.
    private static final Map<String, String> OPERADORES = new HashMap<>();

    static {
        // Aritméticos (necesarios para el programa mínimo: '+')
        //Poder reconocer y diferenciar operadores de 1 y 2 caracteres: 
        // = vs ==, ! vs !=, < vs <=, &&, ||
        OPERADORES.put("+", "OPERADOR_SUMA");
        OPERADORES.put("-", "OPERADOR_RESTA");
        OPERADORES.put("*", "OPERADOR_MULTIPLICACION");
        OPERADORES.put("/", "OPERADOR_DIVISION");
        OPERADORES.put("%", "OPERADOR_MODULO");
        // Asignación (necesario para el programa mínimo: '=')
        OPERADORES.put("=", "OPERADOR_ASIGNACION");
     
     
     
        OPERADORES.put("!=", "OPERADOR_DIFERENTEA");
        OPERADORES.put("", "OPERADOR_MENORIGUAL");
        OPERADORES.put("==", "OPERADOR_MAYORIGUAL");
        OPERADORES.put("==", "OPERADOR_ANDPERSON");
        OPERADORES.put("==", "OPERADOR_DOUBLEPIPE");
    }

    /**
     * Devuelve el tipo de operador o null si no existe.
     */
    public static String getTipoOperador(String op) {
        return OPERADORES.get(op);
    }

    /**
     * Valida si una cadena es un operador reconocido.
     */
    public static boolean esOperador(String op) {
        return OPERADORES.containsKey(op);
    }
}
