import java.util.HashMap;
import java.util.Map;

public class Operadores {

    private static final Map<String, String> OPERADORES = new HashMap<>();

    static {
        // Operadores de tu alfabeto
        OPERADORES.put("+", "OPERADOR_SUMA");
        OPERADORES.put("-", "OPERADOR_RESTA");
        OPERADORES.put("*", "OPERADOR_MULTIPLICACION");
        OPERADORES.put("/", "OPERADOR_DIVISION");
        OPERADORES.put("=", "OPERADOR_ASIGNACION");

        // Operadores extra 
        OPERADORES.put("%", "OPERADOR_MODULO");
        OPERADORES.put("==", "OPERADOR_IGUALDAD");
        OPERADORES.put("!=", "OPERADOR_DIFERENTE");
        OPERADORES.put("<", "OPERADOR_MENOR");
        OPERADORES.put("<=", "OPERADOR_MENOR_IGUAL");
        OPERADORES.put(">", "OPERADOR_MAYOR");
        OPERADORES.put(">=", "OPERADOR_MAYOR_IGUAL");
        OPERADORES.put("!", "OPERADOR_NOT");
        OPERADORES.put("&&", "OPERADOR_AND");
        OPERADORES.put("||", "OPERADOR_OR");
    }

    public static String getTipoOperador(String op) {
        if (op == null) return null;
        return OPERADORES.get(op);
    }

    public static boolean esOperador(String op) {
        if (op == null) return false;
        return OPERADORES.containsKey(op);
    }
}
