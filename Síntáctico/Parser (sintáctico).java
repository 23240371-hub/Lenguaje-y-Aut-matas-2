import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Sintáctico Recursivo Descendente (Modelo Bajo Demanda).
 * Solicita los tokens uno por uno al ProveedorTokens (Lexer o MockTokens)
 * conforme avanza el análisis gramatical.
 */
class Parser {

    private final ProveedorTokens proveedorTokens;
    private Token actual;
    private Token anterior;
    private final ManejadorErrores manejadorErrores;
    private boolean huboErrorSintactico;

    private static class ErrorSintacticoException extends RuntimeException {
        public ErrorSintacticoException(String mensaje) {
            super(mensaje);
        }
    }

    /**
     * Constructor principal: recibe el proveedor bajo demanda y el ManejadorErrores compartido.
     */
    public Parser(ProveedorTokens proveedorTokens, ManejadorErrores manejadorErrores) {
        this.proveedorTokens = proveedorTokens;
        this.manejadorErrores = (manejadorErrores != null) ? manejadorErrores : new ManejadorErrores();
        this.huboErrorSintactico = false;

        // Pre-carga del primer token (lookahead LL(1))
        this.actual = solicitarSiguienteToken(1, 1);
        this.anterior = this.actual;
    }

    /**
     * Constructor auxiliar para pruebas aisladas sin instancia previa de ManejadorErrores.
     */
    public Parser(ProveedorTokens proveedorTokens) {
        this(proveedorTokens, new ManejadorErrores());
    }

    private Token solicitarSiguienteToken(int lineaRespaldo, int columnaRespaldo) {
        if (proveedorTokens == null) {
            return new Token(TipoToken.EOF, "", lineaRespaldo, columnaRespaldo);
        }
        Token recibido = proveedorTokens.siguienteToken();
        if (recibido == null) {
            return new Token(TipoToken.EOF, "", lineaRespaldo, columnaRespaldo);
        }
        return recibido;
    }

    // =========================================================================
    // PUNTO DE ENTRADA E IMPRESIÓN DEL AST
    // =========================================================================

    public NodoPrograma parsear() {
        List<NodoAST> sentencias = new ArrayList<>();

        try {
            // Soporte opcional para estructura: class NombreClase { main { sentencias } }
            if (coincidir(TipoToken.CLASS)) {
                consumir(TipoToken.IDENTIFICADOR, "Se esperaba el nombre de la clase después de 'class'");
                consumir(TipoToken.LLAVE_IZQ, "Se esperaba '{' después del nombre de la clase");

                if (coincidir(TipoToken.MAIN)) {
                    if (coincidir(TipoToken.PARENTESIS_IZQ)) {
                        consumir(TipoToken.PARENTESIS_DER, "Se esperaba ')' después de 'main('");
                    }
                    sentencias.addAll(bloque());
                } else {
                    while (!verificar(TipoToken.LLAVE_DER) && !esFinDeArchivo()) {
                        NodoAST s = parsearSentenciaConRecuperacion();
                        if (s != null) {
                            sentencias.add(s);
                        }
                    }
                }

                consumir(TipoToken.LLAVE_DER, "Se esperaba '}' al final de la clase");
                return new NodoPrograma(sentencias);
            }

            // Soporte opcional para estructura: main { sentencias } o main() { sentencias }
            if (coincidir(TipoToken.MAIN)) {
                if (coincidir(TipoToken.PARENTESIS_IZQ)) {
                    consumir(TipoToken.PARENTESIS_DER, "Se esperaba ')' después de 'main('");
                }
                sentencias.addAll(bloque());
                return new NodoPrograma(sentencias);
            }
        } catch (ErrorSintacticoException ex) {
            sincronizar();
        }

        // Programa directo compuesto por sentencias globales
        while (!esFinDeArchivo()) {
            NodoAST sentencia = parsearSentenciaConRecuperacion();
            if (sentencia != null) {
                sentencias.add(sentencia);
            }
        }

        return new NodoPrograma(sentencias);
    }

    public void imprimirAST(NodoPrograma raiz) {
        if (raiz == null) {
            System.out.println("(AST vacío)");
            return;
        }
        raiz.imprimir("", true);
    }

    public boolean tieneErrores() {
        return huboErrorSintactico || manejadorErrores.tieneErrores();
    }

    public ManejadorErrores getManejadorErrores() {
        return manejadorErrores;
    }

    // =========================================================================
    // MÉTODOS BASE DEL PARSER BAJO DEMANDA Y MODO PÁNICO
    // =========================================================================

    private Token tokenActual() {
        return actual;
    }

    private Token tokenAnterior() {
        return anterior;
    }

    private boolean esFinDeArchivo() {
        return actual.getTipo() == TipoToken.EOF;
    }

    private boolean verificar(TipoToken tipo) {
        if (esFinDeArchivo()) return false;
        return actual.getTipo() == tipo;
    }

    private boolean coincidir(TipoToken tipo) {
        if (verificar(tipo)) {
            avanzar();
            return true;
        }
        return false;
    }

    private boolean coincidir(TipoToken tipo1, TipoToken tipo2) {
        if (verificar(tipo1) || verificar(tipo2)) {
            avanzar();
            return true;
        }
        return false;
    }

    private boolean esOperadorMultiplicativo() {
        TipoToken tipo = actual.getTipo();
        return tipo == TipoToken.MULTIPLICACION
                || tipo == TipoToken.DIVISION
                || tipo == TipoToken.MODULO;
    }

    private boolean esOperadorRelacional() {
        TipoToken tipo = actual.getTipo();
        return tipo == TipoToken.IGUAL_IGUAL
                || tipo == TipoToken.DIFERENTE
                || tipo == TipoToken.MENOR
                || tipo == TipoToken.MENOR_IGUAL
                || tipo == TipoToken.MAYOR
                || tipo == TipoToken.MAYOR_IGUAL;
    }

    /**
     * Consume el token actual y solicita bajo demanda el siguiente token al Lexer.
     */
    private Token avanzar() {
        if (!esFinDeArchivo()) {
            anterior = actual;
            actual = solicitarSiguienteToken(anterior.getLinea(), anterior.getColumna());
        }
        return anterior;
    }

    private Token consumir(TipoToken tipoEsperado, String mensajeError) {
        if (verificar(tipoEsperado)) {
            return avanzar();
        }
        int lineaReporte = (tipoEsperado == TipoToken.PUNTO_Y_COMA)
                ? anterior.getLinea()
                : actual.getLinea();
        throw error(lineaReporte, mensajeError + " (se encontró '" + actual.getLexema() + "')");
    }

    private ErrorSintacticoException error(int linea, String mensaje) {
        huboErrorSintactico = true;
        manejadorErrores.agregarErrorSintactico(linea, mensaje);
        return new ErrorSintacticoException(mensaje);
    }

    /**
     * Recuperación de errores en modo pánico:
     * Pide tokens bajo demanda hasta encontrar ';' o '}' o el inicio de otra sentencia válida.
     */
    private void sincronizar() {
        avanzar();

        while (!esFinDeArchivo()) {
            if (anterior.getTipo() == TipoToken.PUNTO_Y_COMA
                    || anterior.getTipo() == TipoToken.LLAVE_DER) {
                return;
            }

            switch (actual.getTipo()) {
                case INT:
                case FLOAT:
                case DOUBLE:
                case BOOLEAN:
                case CHAR:
                case BYTE:
                case SHORT:
                case IF:
                case WHILE:
                case PRINT:
                    return;
                default:
                    avanzar();
            }
        }
    }

    // =========================================================================
    // REGLAS DE SENTENCIAS
    // =========================================================================

    private NodoAST parsearSentenciaConRecuperacion() {
        try {
            return sentencia();
        } catch (ErrorSintacticoException ex) {
            sincronizar();
            return null;
        }
    }

    private NodoAST sentencia() {
        if (esTipoDato(actual.getTipo())) {
            return declaracionVariable();
        }
        if (verificar(TipoToken.IF)) {
            return sentenciaIf();
        }
        if (verificar(TipoToken.WHILE)) {
            return sentenciaWhile();
        }
        if (verificar(TipoToken.PRINT)) {
            return sentenciaPrint();
        }
        if (verificar(TipoToken.IDENTIFICADOR)) {
            return sentenciaAsignacion();
        }

        throw error(actual.getLinea(),
                "Inicio de sentencia inválido '" + actual.getLexema() + "'");
    }

    private boolean esTipoDato(TipoToken tipo) {
        return tipo == TipoToken.INT
                || tipo == TipoToken.FLOAT
                || tipo == TipoToken.DOUBLE
                || tipo == TipoToken.BOOLEAN
                || tipo == TipoToken.CHAR
                || tipo == TipoToken.BYTE
                || tipo == TipoToken.SHORT;
    }

    /**
     * Declaración de variables -> Tipo IDENTIFICADOR ('=' ExpresionLogica)? ';'
     */
    public NodoDeclaracion declaracionVariable() {
        Token tokenTipo = avanzar();
        Token tokenId = consumir(TipoToken.IDENTIFICADOR,
                "Se esperaba el nombre de la variable después del tipo '" + tokenTipo.getLexema() + "'");

        NodoAST expresionInicial = null;
        if (coincidir(TipoToken.ASIGNACION)) {
            expresionInicial = expresionLogica();
        }

        consumir(TipoToken.PUNTO_Y_COMA,
                "Se esperaba ';' después de la declaración de '" + tokenId.getLexema() + "' en la línea " + tokenId.getLinea());

        return new NodoDeclaracion(tokenTipo.getLexema(), tokenId.getLexema(), expresionInicial, tokenTipo.getLinea());
    }

    /**
     * Sentencia de asignación -> IDENTIFICADOR '=' ExpresionLogica ';'
     */
    public NodoAsignacion sentenciaAsignacion() {
        Token tokenId = consumir(TipoToken.IDENTIFICADOR, "Se esperaba un identificador");
        consumir(TipoToken.ASIGNACION,
                "Se esperaba '=' después del identificador '" + tokenId.getLexema() + "'");

        NodoAST valor = expresionLogica();

        consumir(TipoToken.PUNTO_Y_COMA,
                "Se esperaba ';' después de la expresión en la línea " + anterior.getLinea());

        return new NodoAsignacion(tokenId.getLexema(), valor, tokenId.getLinea());
    }

    /**
     * Estructura condicional if-else
     */
    public NodoIf sentenciaIf() {
        Token tokenIf = consumir(TipoToken.IF, "Se esperaba 'if'");
        consumir(TipoToken.PARENTESIS_IZQ, "Se esperaba '(' después de 'if'");
        NodoAST condicion = expresionLogica();
        consumir(TipoToken.PARENTESIS_DER, "Se esperaba ')' después de la condición del 'if'");

        List<NodoAST> bloqueThen = bloque();
        List<NodoAST> bloqueElse = null;

        if (coincidir(TipoToken.ELSE)) {
            bloqueElse = bloque();
        }

        return new NodoIf(condicion, bloqueThen, bloqueElse, tokenIf.getLinea());
    }

    /**
     * Ciclo while
     */
    public NodoWhile sentenciaWhile() {
        Token tokenWhile = consumir(TipoToken.WHILE, "Se esperaba 'while'");
        consumir(TipoToken.PARENTESIS_IZQ, "Se esperaba '(' después de 'while'");
        NodoAST condicion = expresionLogica();
        consumir(TipoToken.PARENTESIS_DER, "Se esperaba ')' después de la condición del 'while'");

        List<NodoAST> cuerpo = bloque();
        return new NodoWhile(condicion, cuerpo, tokenWhile.getLinea());
    }

    /**
     * Salida print
     */
    public NodoPrint sentenciaPrint() {
        Token tokenPrint = consumir(TipoToken.PRINT, "Se esperaba 'print'");
        consumir(TipoToken.PARENTESIS_IZQ, "Se esperaba '(' después de 'print'");
        NodoAST expresion = expresionLogica();
        consumir(TipoToken.PARENTESIS_DER, "Se esperaba ')' después de la expresión de 'print'");
        consumir(TipoToken.PUNTO_Y_COMA,
                "Se esperaba ';' después de la sentencia 'print' en la línea " + anterior.getLinea());

        return new NodoPrint(expresion, tokenPrint.getLinea());
    }

    private List<NodoAST> bloque() {
        consumir(TipoToken.LLAVE_IZQ, "Se esperaba '{' para abrir el bloque");
        List<NodoAST> sentenciasBloque = new ArrayList<>();

        while (!verificar(TipoToken.LLAVE_DER) && !esFinDeArchivo()) {
            NodoAST s = parsearSentenciaConRecuperacion();
            if (s != null) {
                sentenciasBloque.add(s);
            }
        }

        consumir(TipoToken.LLAVE_DER, "Se esperaba '}' para cerrar el bloque");
        return sentenciasBloque;
    }

    // =========================================================================
    // FUNCIONES RECURSIVAS DE EXPRESIONES Y PRECEDENCIA
    // =========================================================================

    public NodoAST expresionLogica() {
        NodoAST izquierda = expresionAnd();

        while (coincidir(TipoToken.OR)) {
            Token operador = tokenAnterior();
            NodoAST derecha = expresionAnd();
            izquierda = new NodoOperacionBinaria(izquierda, operador.getLexema(), derecha, operador.getLinea());
        }

        return izquierda;
    }

    private NodoAST expresionAnd() {
        NodoAST izquierda = expresionRelacional();

        while (coincidir(TipoToken.AND)) {
            Token operador = tokenAnterior();
            NodoAST derecha = expresionRelacional();
            izquierda = new NodoOperacionBinaria(izquierda, operador.getLexema(), derecha, operador.getLinea());
        }

        return izquierda;
    }

    public NodoAST expresionRelacional() {
        NodoAST izquierda = expresionAritmetica();

        if (esOperadorRelacional()) {
            Token operador = avanzar();
            NodoAST derecha = expresionAritmetica();
            izquierda = new NodoOperacionBinaria(izquierda, operador.getLexema(), derecha, operador.getLinea());
        }

        return izquierda;
    }

    public NodoAST expresionAritmetica() {
        NodoAST izquierda = termino();

        while (coincidir(TipoToken.SUMA, TipoToken.RESTA)) {
            Token operador = tokenAnterior();
            NodoAST derecha = termino();
            izquierda = new NodoOperacionBinaria(izquierda, operador.getLexema(), derecha, operador.getLinea());
        }

        return izquierda;
    }

    public NodoAST termino() {
        NodoAST izquierda = factor();

        while (esOperadorMultiplicativo()) {
            Token operador = avanzar();
            NodoAST derecha = factor();
            izquierda = new NodoOperacionBinaria(izquierda, operador.getLexema(), derecha, operador.getLinea());
        }

        return izquierda;
    }

    public NodoAST factor() {
        if (coincidir(TipoToken.NOT, TipoToken.RESTA)) {
            Token operador = tokenAnterior();
            NodoAST operando = factor();
            return new NodoOperacionUnaria(operador.getLexema(), operando, operador.getLinea());
        }

        if (coincidir(TipoToken.PARENTESIS_IZQ)) {
            int lineaApertura = tokenAnterior().getLinea();
            NodoAST expr = expresionLogica();
            consumir(TipoToken.PARENTESIS_DER,
                    "Se esperaba ')' para cerrar la expresión iniciada en la línea " + lineaApertura);
            return expr;
        }

        if (coincidir(TipoToken.IDENTIFICADOR)) {
            Token id = tokenAnterior();
            return new NodoIdentificador(id.getLexema(), id.getLinea());
        }

        if (coincidir(TipoToken.LITERAL_ENTERO)) {
            Token lit = tokenAnterior();
            return new NodoLiteral(Integer.parseInt(lit.getLexema()), lit.getLexema(), "int", lit.getLinea());
        }

        if (coincidir(TipoToken.LITERAL_DECIMAL)) {
            Token lit = tokenAnterior();
            return new NodoLiteral(Double.parseDouble(lit.getLexema()), lit.getLexema(), "double", lit.getLinea());
        }

        if (coincidir(TipoToken.LITERAL_BOOLEANO)) {
            Token lit = tokenAnterior();
            return new NodoLiteral(Boolean.parseBoolean(lit.getLexema()), lit.getLexema(), "boolean", lit.getLinea());
        }

        if (coincidir(TipoToken.LITERAL_CARACTER)) {
            Token lit = tokenAnterior();
            String lex = lit.getLexema();
            char c = (lex.length() >= 3 && lex.startsWith("'")) ? lex.charAt(1) : lex.charAt(0);
            return new NodoLiteral(c, lex, "char", lit.getLinea());
        }

        if (coincidir(TipoToken.LITERAL_CADENA)) {
            Token lit = tokenAnterior();
            return new NodoLiteral(lit.getLexema(), lit.getLexema(), "String", lit.getLinea());
        }

        throw error(actual.getLinea(),
                "Se esperaba un operando, identificador o '(' pero se encontró '" + actual.getLexema() + "'");
    }
}
