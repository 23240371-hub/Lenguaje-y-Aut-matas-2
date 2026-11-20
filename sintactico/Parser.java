package sintactico;

import java.util.ArrayList;
import java.util.List;
import lexico.Lexer;
import lexico.Token;
import sintactico.NodoAsignacion;
import sintactico.NodoDeclaracion;
import sintactico.NodoIdentificador;
import sintactico.NodoIf;
import sintactico.NodoLiteral;
import sintactico.NodoOperacionBinaria;
import sintactico.NodoOperacionUnaria;
import sintactico.NodoPrint;
import sintactico.NodoPrograma;
import sintactico.NodoWhile;

public class Parser {
    private final Lexer lexer;
    private final ManejadorErrores manejadorErrores;
    private Token tokenActual;
    private int ultimaFila = 1;
    private int ultimaColumna = 1;

    public Parser(Lexer lexer, ManejadorErrores manejadorErrores) {
        this.lexer = lexer;
        this.manejadorErrores = manejadorErrores;
        avanzar();
    }

    private void avanzar() {
        Token siguiente = lexer.siguienteToken();
        if (siguiente == null) {
            tokenActual = new Token("EOF", "EOF", ultimaFila, ultimaColumna);
        } else {
            tokenActual = siguiente;
            ultimaFila = siguiente.getFila();
            ultimaColumna = siguiente.getColumna();
        }
    }

    private boolean verificar(String tipoEsperado) {
        return tokenActual.getTipo().equals(tipoEsperado);
    }

    private boolean esTipoDato(String tipoToken) {
        return tipoToken.equals("PR_INT")
                || tipoToken.equals("PR_FLOAT")
                || tipoToken.equals("PR_BOOLEAN")
                || tipoToken.equals("PR_DOUBLE");
    }

    private Token consumir(String tipoEsperado, String mensajeError) {
        if (verificar(tipoEsperado)) {
            Token consumido = tokenActual;
            avanzar();
            return consumido;
        }
        manejadorErrores.agregarErrorSintactico(
                tokenActual.getFila(),
                tokenActual.getColumna(),
                mensajeError + " (se encontró '" + tokenActual.getLexema() + "')"
        );
        return null;
    }

    public NodoPrograma parsear() {
        int filaInicio = tokenActual.getFila();
        String nombreClase = null;
        List<NodoAST> sentencias = new ArrayList<>();

        // Soporta estructura opcional: class Nombre { main { ... } }
        if (verificar("PR_CLASS")) {
            avanzar();
            Token idClase = consumir("IDENTIFICADOR", "Se esperaba el nombre de la clase después de 'class'");
            if (idClase != null) {
                nombreClase = idClase.getLexema();
            }
            consumir("DELIM_LLAVE_ABRE", "Se esperaba '{' después del nombre de la clase");
            consumir("PR_MAIN", "Se esperaba 'main' dentro de la clase");
            sentencias = parsearBloque();
            consumir("DELIM_LLAVE_CIERRA", "Se esperaba '}' de cierre de la clase");
        } else {
            while (!verificar("EOF")) {
                NodoAST sentencia = parsearSentencia();
                if (sentencia != null) {
                    sentencias.add(sentencia);
                }
            }
        }

        return new NodoPrograma(nombreClase, sentencias, filaInicio);
    }

    private List<NodoAST> parsearBloque() {
        List<NodoAST> sentencias = new ArrayList<>();
        consumir("DELIM_LLAVE_ABRE", "Se esperaba '{' para iniciar el bloque");

        while (!verificar("DELIM_LLAVE_CIERRA") && !verificar("EOF")) {
            NodoAST sentencia = parsearSentencia();
            if (sentencia != null) {
                sentencias.add(sentencia);
            }
        }

        consumir("DELIM_LLAVE_CIERRA", "Se esperaba '}' para cerrar el bloque");
        return sentencias;
    }

    private NodoAST parsearSentencia() {
        String tipo = tokenActual.getTipo();

        if (esTipoDato(tipo)) {
            return parsearDeclaracion();
        }
        if (tipo.equals("IDENTIFICADOR")) {
            return parsearAsignacion();
        }
        if (tipo.equals("PR_IF")) {
            return parsearIf();
        }
        if (tipo.equals("PR_WHILE")) {
            return parsearWhile();
        }
        if (tipo.equals("PR_PRINT")) {
            return parsearPrint();
        }

        manejadorErrores.agregarErrorSintactico(
                tokenActual.getFila(),
                tokenActual.getColumna(),
                "Inicio de sentencia no válido: '" + tokenActual.getLexema() + "'"
        );
        sincronizar();
        return null;
    }

    private NodoDeclaracion parsearDeclaracion() {
        Token tipoTok = tokenActual;
        avanzar();

        Token idTok = consumir("IDENTIFICADOR", "Se esperaba un identificador después de '" + tipoTok.getLexema() + "'");
        if (idTok == null) {
            sincronizar();
            return null;
        }

        NodoAST exprInicial = null;
        if (verificar("OPERADOR_ASIGNACION")) {
            avanzar();
            exprInicial = parsearExpresion();
        }

        if (consumir("DELIM_PUNTO_COMA", "Se esperaba ';' al final de la declaración") == null) {
            sincronizar();
            return null;
        }

        return new NodoDeclaracion(tipoTok.getLexema(), idTok.getLexema(), exprInicial, tipoTok.getFila());
    }

    private NodoAsignacion parsearAsignacion() {
        Token idTok = tokenActual;
        avanzar();

        if (consumir("OPERADOR_ASIGNACION", "Se esperaba '=' en la asignación") == null) {
            sincronizar();
            return null;
        }

        NodoAST expr = parsearExpresion();

        if (consumir("DELIM_PUNTO_COMA", "Se esperaba ';' al final de la asignación") == null) {
            sincronizar();
            return null;
        }

        return new NodoAsignacion(idTok.getLexema(), expr, idTok.getFila());
    }

    private NodoIf parsearIf() {
        int fila = tokenActual.getFila();
        avanzar(); // consume 'if'

        consumir("DELIM_PARENTESIS_ABRE", "Se esperaba '(' después de 'if'");
        NodoAST condicion = parsearExpresion();
        consumir("DELIM_PARENTESIS_CIERRA", "Se esperaba ')' después de la condición de 'if'");

        List<NodoAST> bloqueThen = parsearBloque();
        List<NodoAST> bloqueElse = null;

        if (verificar("PR_ELSE")) {
            avanzar(); // consume 'else'
            bloqueElse = parsearBloque();
        }

        return new NodoIf(condicion, bloqueThen, bloqueElse, fila);
    }

    private NodoWhile parsearWhile() {
        int fila = tokenActual.getFila();
        avanzar(); // consume 'while'

        consumir("DELIM_PARENTESIS_ABRE", "Se esperaba '(' después de 'while'");
        NodoAST condicion = parsearExpresion();
        consumir("DELIM_PARENTESIS_CIERRA", "Se esperaba ')' después de la condición de 'while'");

        List<NodoAST> cuerpo = parsearBloque();
        return new NodoWhile(condicion, cuerpo, fila);
    }

    private NodoPrint parsearPrint() {
        int fila = tokenActual.getFila();
        avanzar(); // consume 'print'

        consumir("DELIM_PARENTESIS_ABRE", "Se esperaba '(' después de 'print'");
        NodoAST expr = parsearExpresion();
        consumir("DELIM_PARENTESIS_CIERRA", "Se esperaba ')' después de la expresión en 'print'");

        if (consumir("DELIM_PUNTO_COMA", "Se esperaba ';' después de 'print(...)'") == null) {
            sincronizar();
            return null;
        }

        return new NodoPrint(expr, fila);
    }

    // --- Jerarquía de precedencia de expresiones ---

    private NodoAST parsearExpresion() {
        return parsearOr();
    }

    private NodoAST parsearOr() {
        NodoAST izq = parsearAnd();
        while (verificar("OPERADOR_OR")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearAnd();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearAnd() {
        NodoAST izq = parsearIgualdad();
        while (verificar("OPERADOR_AND")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearIgualdad();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearIgualdad() {
        NodoAST izq = parsearRelacional();
        while (verificar("OPERADOR_IGUALDAD") || verificar("OPERADOR_DIFERENTE")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearRelacional();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearRelacional() {
        NodoAST izq = parsearSumaResta();
        while (verificar("OPERADOR_MENOR") || verificar("OPERADOR_MENOR_IGUAL")
                || verificar("OPERADOR_MAYOR") || verificar("OPERADOR_MAYOR_IGUAL")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearSumaResta();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearSumaResta() {
        NodoAST izq = parsearMultDivMod();
        while (verificar("OPERADOR_SUMA") || verificar("OPERADOR_RESTA")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearMultDivMod();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearMultDivMod() {
        NodoAST izq = parsearUnario();
        while (verificar("OPERADOR_MULTIPLICACION") || verificar("OPERADOR_DIVISION") || verificar("OPERADOR_MODULO")) {
            Token op = tokenActual;
            avanzar();
            NodoAST der = parsearUnario();
            izq = new NodoOperacionBinaria(izq, op.getLexema(), der, op.getFila());
        }
        return izq;
    }

    private NodoAST parsearUnario() {
        if (verificar("OPERADOR_NOT")) {
            Token op = tokenActual;
            avanzar();
            NodoAST operando = parsearUnario();
            return new NodoOperacionUnaria(op.getLexema(), operando, op.getFila());
        }
        return parsearPrimario();
    }

    private NodoAST parsearPrimario() {
        Token tok = tokenActual;

        if (verificar("NUMERO_ENTERO")) {
            avanzar();
            return new NodoLiteral(Integer.parseInt(tok.getLexema()), tok.getLexema(), "int", tok.getFila());
        }

        if (verificar("NUMERO_DECIMAL")) {
            avanzar();
            return new NodoLiteral(Double.parseDouble(tok.getLexema()), tok.getLexema(), "double", tok.getFila());
        }

        if (verificar("IDENTIFICADOR")) {
            avanzar();
            return new NodoIdentificador(tok.getLexema(), tok.getFila());
        }

        if (verificar("DELIM_PARENTESIS_ABRE")) {
            avanzar();
            NodoAST expr = parsearExpresion();
            consumir("DELIM_PARENTESIS_CIERRA", "Se esperaba ')' para cerrar la expresión");
            return expr;
        }

        manejadorErrores.agregarErrorSintactico(
                tok.getFila(),
                tok.getColumna(),
                "Expresión inesperada: '" + tok.getLexema() + "'"
        );
        avanzar();
        return null;
    }

    // Recuperación en modo pánico hasta el siguiente punto seguro
    private void sincronizar() {
        while (!verificar("EOF")) {
            if (verificar("DELIM_PUNTO_COMA")) {
                avanzar();
                return;
            }
            String tipo = tokenActual.getTipo();
            if (esTipoDato(tipo) || tipo.equals("PR_IF") || tipo.equals("PR_WHILE")
                    || tipo.equals("PR_PRINT") || tipo.equals("DELIM_LLAVE_CIERRA")) {
                return;
            }
            avanzar();
        }
    }

    public void imprimirAST(NodoPrograma ast) {
        if (ast != null) {
            ast.imprimir("", true);
        }
    }
}