import java.util.ArrayList;
import java.util.List;

/**
 * Simulador bajo demanda del Analizador Léxico:
 * Entrega los tokens uno por uno mediante siguienteToken() cada vez que el Parser
 * los solicita, permitiendo probar el Parser sin esperar al Lexer real.
 */
class MockTokens implements ProveedorTokens {

    private final List<Token> tokens;
    private int cursor;

    public MockTokens(List<Token> tokens) {
        this.tokens = (tokens != null) ? tokens : new ArrayList<>();
        this.cursor = 0;
    }

    @Override
    public Token siguienteToken() {
        if (cursor < tokens.size()) {
            Token actual = tokens.get(cursor);
            cursor++;
            return actual;
        }
        int linea = tokens.isEmpty() ? 1 : tokens.get(tokens.size() - 1).getLinea();
        int columna = tokens.isEmpty() ? 1 : tokens.get(tokens.size() - 1).getColumna();
        return new Token(TipoToken.EOF, "EOF", linea, columna);
    }

    /**
     * Prueba 1: Programa válido con expresiones anidadas, while, if-else y print:
     *
     * int a = 5;
     * int b = 3;
     * int c = 2;
     * int d = 4;
     * int resultado = (((a + b) * c) - d);
     * while (resultado > 0 && d != 0) {
     *     if (resultado >= 10) {
     *         print(resultado);
     *     } else {
     *         print(d);
     *     }
     *     resultado = resultado - 1;
     * }
     */
    public static MockTokens crearProveedorProgramaValido() {
        List<Token> lista = new ArrayList<>();

        // Línea 1: int a = 5;
        lista.add(new Token(TipoToken.INT, "int", 1, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "a", 1, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 1, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "5", 1, 9));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 1, 10));

        // Línea 2: int b = 3;
        lista.add(new Token(TipoToken.INT, "int", 2, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "b", 2, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 2, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "3", 2, 9));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 2, 10));

        // Línea 3: int c = 2;
        lista.add(new Token(TipoToken.INT, "int", 3, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "c", 3, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 3, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "2", 3, 9));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 3, 10));

        // Línea 4: int d = 4;
        lista.add(new Token(TipoToken.INT, "int", 4, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "d", 4, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 4, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "4", 4, 9));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 4, 10));

        // Línea 5: int resultado = (((a + b) * c) - d);
        lista.add(new Token(TipoToken.INT, "int", 5, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 5, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 5, 15));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 5, 17));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 5, 18));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 5, 19));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "a", 5, 20));
        lista.add(new Token(TipoToken.SUMA, "+", 5, 22));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "b", 5, 24));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 5, 25));
        lista.add(new Token(TipoToken.MULTIPLICACION, "*", 5, 27));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "c", 5, 29));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 5, 30));
        lista.add(new Token(TipoToken.RESTA, "-", 5, 32));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "d", 5, 34));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 5, 35));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 5, 36));

        // Línea 6: while (resultado > 0 && d != 0) {
        lista.add(new Token(TipoToken.WHILE, "while", 6, 1));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 6, 7));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 6, 8));
        lista.add(new Token(TipoToken.MAYOR, ">", 6, 18));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "0", 6, 20));
        lista.add(new Token(TipoToken.AND, "&&", 6, 22));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "d", 6, 25));
        lista.add(new Token(TipoToken.DIFERENTE, "!=", 6, 27));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "0", 6, 30));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 6, 31));
        lista.add(new Token(TipoToken.LLAVE_IZQ, "{", 6, 33));

        // Línea 7: if (resultado >= 10) {
        lista.add(new Token(TipoToken.IF, "if", 7, 5));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 7, 8));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 7, 9));
        lista.add(new Token(TipoToken.MAYOR_IGUAL, ">=", 7, 19));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "10", 7, 22));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 7, 24));
        lista.add(new Token(TipoToken.LLAVE_IZQ, "{", 7, 26));

        // Línea 8: print(resultado);
        lista.add(new Token(TipoToken.PRINT, "print", 8, 9));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 8, 14));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 8, 15));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 8, 24));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 8, 25));

        // Línea 9: } else {
        lista.add(new Token(TipoToken.LLAVE_DER, "}", 9, 5));
        lista.add(new Token(TipoToken.ELSE, "else", 9, 7));
        lista.add(new Token(TipoToken.LLAVE_IZQ, "{", 9, 12));

        // Línea 10: print(d);
        lista.add(new Token(TipoToken.PRINT, "print", 10, 9));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 10, 14));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "d", 10, 15));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 10, 16));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 10, 17));

        // Línea 11: }
        lista.add(new Token(TipoToken.LLAVE_DER, "}", 11, 5));

        // Línea 12: resultado = resultado - 1;
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 12, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 12, 15));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "resultado", 12, 17));
        lista.add(new Token(TipoToken.RESTA, "-", 12, 27));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "1", 12, 29));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 12, 30));

        // Línea 13: }
        lista.add(new Token(TipoToken.LLAVE_DER, "}", 13, 1));
        lista.add(new Token(TipoToken.EOF, "EOF", 13, 2));

        return new MockTokens(lista);
    }

    /**
     * Prueba 2: Programa con cabecera 'class' / 'main' y operador módulo '%':
     *
     * class MiPrograma {
     *     main {
     *         int residuo = 17 % 5;
     *         print(residuo);
     *     }
     * }
     */
    public static MockTokens crearProveedorConClaseYModulo() {
        List<Token> lista = new ArrayList<>();

        lista.add(new Token(TipoToken.CLASS, "class", 1, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "MiPrograma", 1, 7));
        lista.add(new Token(TipoToken.LLAVE_IZQ, "{", 1, 18));

        lista.add(new Token(TipoToken.MAIN, "main", 2, 5));
        lista.add(new Token(TipoToken.LLAVE_IZQ, "{", 2, 10));

        lista.add(new Token(TipoToken.INT, "int", 3, 9));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "residuo", 3, 13));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 3, 21));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "17", 3, 23));
        lista.add(new Token(TipoToken.MODULO, "%", 3, 26));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "5", 3, 28));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 3, 29));

        lista.add(new Token(TipoToken.PRINT, "print", 4, 9));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 4, 14));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "residuo", 4, 15));
        lista.add(new Token(TipoToken.PARENTESIS_DER, ")", 4, 22));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 4, 23));

        lista.add(new Token(TipoToken.LLAVE_DER, "}", 5, 5));
        lista.add(new Token(TipoToken.LLAVE_DER, "}", 6, 1));
        lista.add(new Token(TipoToken.EOF, "EOF", 6, 2));

        return new MockTokens(lista);
    }

    /**
     * Prueba 3: Programa con errores sintácticos para probar recuperación en modo pánico:
     * Línea 1: int x = 10   (falta ';')
     * Línea 2: int y = 20;  (debe recuperarse y parsearla bien)
     * Línea 3: y = (x + 5;  (falta ')')
     */
    public static MockTokens crearProveedorConErrores() {
        List<Token> lista = new ArrayList<>();

        // Línea 1 sin ';'
        lista.add(new Token(TipoToken.INT, "int", 1, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "x", 1, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 1, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "10", 1, 9));

        // Línea 2 válida
        lista.add(new Token(TipoToken.INT, "int", 2, 1));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "y", 2, 5));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 2, 7));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "20", 2, 9));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 2, 11));

        // Línea 3 sin ')'
        lista.add(new Token(TipoToken.IDENTIFICADOR, "y", 3, 1));
        lista.add(new Token(TipoToken.ASIGNACION, "=", 3, 3));
        lista.add(new Token(TipoToken.PARENTESIS_IZQ, "(", 3, 5));
        lista.add(new Token(TipoToken.IDENTIFICADOR, "x", 3, 6));
        lista.add(new Token(TipoToken.SUMA, "+", 3, 8));
        lista.add(new Token(TipoToken.LITERAL_ENTERO, "5", 3, 10));
        lista.add(new Token(TipoToken.PUNTO_Y_COMA, ";", 3, 11));

        lista.add(new Token(TipoToken.EOF, "EOF", 3, 12));
        return new MockTokens(lista);
    }
}
