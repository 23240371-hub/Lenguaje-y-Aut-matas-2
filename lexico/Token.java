
//Representa un token reconocido por el analizador léxico.

public class Token {

    private String tipo;      // Ej. PR_INT, IDENTIFICADOR, NUMERO, OPERADOR_SUMA
    private String lexema;    // El texto exacto leído del archivo (ej. "int", "x", "12")
    private int fila;         // Línea donde inicia el token
    private int columna;      // Columna donde inicia el token

    public Token(String tipo, String lexema, int fila, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return "Token [Tipo: " + tipo + " | Lexema: '" + lexema + "' | Fila: " + fila + ", Col: " + columna + "]";
    }
}
