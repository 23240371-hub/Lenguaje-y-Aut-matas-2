public class Automata {
    public static String evaluarPalabra(String lexema) {
        int estado = 0; // Estado inicial

        for (int i = 0; i < lexema.length(); i++) {
            char c = lexema.charAt(i);

            if (estado == 0) {
                // El primer carácter debe ser obligatoriamente una letra
                if (Character.isLetter(c)) {
                    estado = 1; // Pasa a estado de aceptación
                } else {
                    return null; // Error léxico
                }
            } else if (estado == 1) {
                // Los siguientes caracteres pueden ser letras o números
                if (Character.isLetterOrDigit(c)) {
                    estado = 1; // Se mantiene en estado de aceptación
                } else {
                    return null;
                }
            }
        }

        // Si terminó en estado de aceptación (1), verificamos si es palabra reservada
        if (estado == 1) {
            if (PalabrasReservadas.esPalabraReservada(lexema)) {
                return PalabrasReservadas.getTipoPalabra(lexema);
            }
            return "IDENTIFICADOR";
        }

        return null;
    }

    /**
     * Evalúa una cadena numérica para validar enteros (ej. 10) o decimales (ej. 3.14).
     *
     * @param lexema Cadena numérica extraída.
     * @return "NUMERO_ENTERO", "NUMERO_DECIMAL" o null si está mal formado.
     */
    public static String evaluarNumero(String lexema) {
        int estado = 0; // Estado 0: inicio | Estado 1: entero | Estado 2: punto | Estado 3: decimal

        for (int i = 0; i < lexema.length(); i++) {
            char c = lexema.charAt(i);

            if (estado == 0) {
                if (Character.isDigit(c)) {
                    estado = 1;
                } else {
                    return null;
                }
            } else if (estado == 1) {
                if (Character.isDigit(c)) {
                    estado = 1; // Sigue leyendo dígitos enteros
                } else if (c == '.') {
                    estado = 2; // Encontró un punto decimal (estado intermedio no final)
                } else {
                    return null;
                }
            } else if (estado == 2) {
                // Después del punto debe venir al menos un dígito
                if (Character.isDigit(c)) {
                    estado = 3; // Estado de aceptación decimal
                } else {
                    return null;
                }
            } else if (estado == 3) {
                if (Character.isDigit(c)) {
                    estado = 3;
                } else {
                    return null;
                }
            }
        }

        if (estado == 1) return "NUMERO_ENTERO";
        if (estado == 3) return "NUMERO_DECIMAL";
        return null; // Si terminó en estado 2 (ej. "12."), es un número inválido
    }
}
