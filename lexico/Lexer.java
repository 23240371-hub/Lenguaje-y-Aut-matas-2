public class Lexer {

    private LectorArchivo lector;
    private ManejadorErrores manejadorErrores;

    public Lexer(LectorArchivo lector, ManejadorErrores manejadorErrores) {
        this.lector = lector;
        this.manejadorErrores = manejadorErrores;
    }

    /**
     * Descarta espacios en blanco, tabuladores, saltos de línea y comentarios.
     */
    private void descartarEspaciosYComentarios() {
        while (!lector.finDeArchivo()) {
            char actual = lector.mirar();

            if (actual == ' ' || actual == '\t' || actual == '\r' || actual == '\n') {
                lector.avanzar();
            } else if (actual == '/') {
                char siguiente = lector.mirarSiguiente();

                // Comentario de línea //
                if (siguiente == '/') {
                    lector.avanzar();
                    lector.avanzar();
                    while (!lector.finDeArchivo() && lector.mirar() != '\n') {
                        lector.avanzar();
                    }
                }
                // Comentario multilínea /* ... */
                else if (siguiente == '*') {
                    int filaInicio = lector.getFila();
                    int colInicio = lector.getColumna();
                    lector.avanzar();
                    lector.avanzar();
                    boolean cerrado = false;

                    while (!lector.finDeArchivo()) {
                        if (lector.mirar() == '*' && lector.mirarSiguiente() == '/') {
                            lector.avanzar();
                            lector.avanzar();
                            cerrado = true;
                            break;
                        }
                        lector.avanzar();
                    }

                    // Si llegó al final del archivo y nunca cerró el comentario
                    if (!cerrado) {
                        manejadorErrores.agregarError(filaInicio, colInicio, "Comentario multilinea sin cerrar");
                    }
                } else {
                    break; // Es un operador de división '/'
                }
            } else {
                break;
            }
        }
    }


    public Token siguienteToken() {
        descartarEspaciosYComentarios();

        if (lector.finDeArchivo()) {
            return null;
        }

        int filaInicio = lector.getFila();
        int colInicio = lector.getColumna();
        char actual = lector.mirar();

        // 1. RECONOCIMIENTO DE PALABRAS (Identificadores o Palabras Reservadas)
        if (Character.isLetter(actual)) {
            StringBuilder lexema = new StringBuilder();
            while (!lector.finDeArchivo() && Character.isLetterOrDigit(lector.mirar())) {
                lexema.append(lector.avanzar());
            }

            String palabra = lexema.toString();
            String tipo = Automata.evaluarPalabra(palabra);
            return new Token(tipo, palabra, filaInicio, colInicio);
        }

        // 2. RECONOCIMIENTO DE NÚMEROS (Enteros o Decimales)
        if (Character.isDigit(actual)) {
            StringBuilder lexema = new StringBuilder();
            while (!lector.finDeArchivo() && (Character.isDigit(lector.mirar()) || lector.mirar() == '.')) {
                lexema.append(lector.avanzar());
            }

            String numero = lexema.toString();
            String tipo = Automata.evaluarNumero(numero);

            if (tipo != null) {
                return new Token(tipo, numero, filaInicio, colInicio);
            } else {
                manejadorErrores.agregarError(filaInicio, colInicio, "Numero mal formado: '" + numero + "'");
                return siguienteToken(); // Continúa buscando el siguiente token
            }
        }

        // 3. RECONOCIMIENTO DE OPERADORES (De 2 caracteres primero, luego de 1 carácter)
        String posibleDoble = "" + actual + lector.mirarSiguiente();
        if (Operadores.esOperador(posibleDoble)) {
            lector.avanzar();
            lector.avanzar();
            return new Token(Operadores.getTipoOperador(posibleDoble), posibleDoble, filaInicio, colInicio);
        }

        String posibleSimple = "" + actual;
        if (Operadores.esOperador(posibleSimple)) {
            lector.avanzar();
            return new Token(Operadores.getTipoOperador(posibleSimple), posibleSimple, filaInicio, colInicio);
        }

        // 4. RECONOCIMIENTO DE DELIMITADORES DEL ALFABETO: ( ) { } ; " ,
        if (esDelimitador(actual)) {
            lector.avanzar();
            return new Token(getTipoDelimitador(actual), "" + actual, filaInicio, colInicio);
        }

        // 5. MANEJO DE ERRORES: Cualquier otro carácter no pertenece al alfabeto
        lector.avanzar();
        manejadorErrores.agregarError(filaInicio, colInicio, "Simbolo no reconocido: '" + actual + "'");
        return siguienteToken();
    }

    /**
     * Verifica si el carácter corresponde a los delimitadores permitidos.
     */
    private boolean esDelimitador(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ';' || c == '"' || c == ',';
    }

    /**
     * Devuelve el nombre del token para cada delimitador.
     */
    private String getTipoDelimitador(char c) {
        switch (c) {
            case '(': return "DELIM_PARENTESIS_ABRE";
            case ')': return "DELIM_PARENTESIS_CIERRA";
            case '{': return "DELIM_LLAVE_ABRE";
            case '}': return "DELIM_LLAVE_CIERRA";
            case ';': return "DELIM_PUNTO_COMA";
            case '"': return "DELIM_COMILLA";
            case ',': return "DELIM_COMA";
            default:  return "DELIMITADOR";
        }
    }
}
