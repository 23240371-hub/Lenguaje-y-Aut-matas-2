package prueba;

import java.util.Arrays;
import java.util.List;

public abstract class NodoAST {
    public int linea;

    public NodoAST(int linea) {
        this.linea = linea;
    }

    public static class Programa extends NodoAST {
        public List<NodoAST> sentencias;

        public Programa(NodoAST... sentencias) {
            super(1);
            this.sentencias = Arrays.asList(sentencias);
        }
    }

    public static class Declaracion extends NodoAST {
        public String tipoDato;
        public String nombre;
        public NodoAST expresionInicial;

        public Declaracion(int linea, String tipoDato, String nombre, NodoAST expresionInicial) {
            super(linea);
            this.tipoDato = tipoDato;
            this.nombre = nombre;
            this.expresionInicial = expresionInicial;
        }
    }

    public static class Asignacion extends NodoAST {
        public String nombreVariable;
        public NodoAST expresion;

        public Asignacion(int linea, String nombreVariable, NodoAST expresion) {
            super(linea);
            this.nombreVariable = nombreVariable;
            this.expresion = expresion;
        }
    }

    public static class If extends NodoAST {
        public NodoAST condicion;
        public List<NodoAST> bloqueEntonces;

        public If(int linea, NodoAST condicion, NodoAST... bloqueEntonces) {
            super(linea);
            this.condicion = condicion;
            this.bloqueEntonces = Arrays.asList(bloqueEntonces);
        }
    }

    public static class While extends NodoAST {
        public NodoAST condicion;
        public List<NodoAST> cuerpo;

        public While(int linea, NodoAST condicion, NodoAST... cuerpo) {
            super(linea);
            this.condicion = condicion;
            this.cuerpo = Arrays.asList(cuerpo);
        }
    }

    public static class OperacionBinaria extends NodoAST {
        public NodoAST izquierda;
        public String operador;
        public NodoAST derecha;

        public OperacionBinaria(int linea, NodoAST izquierda, String operador, NodoAST derecha) {
            super(linea);
            this.izquierda = izquierda;
            this.operador = operador;
            this.derecha = derecha;
        }
    }

    public static class Variable extends NodoAST {
        public String nombre;

        public Variable(int linea, String nombre) {
            super(linea);
            this.nombre = nombre;
        }
    }

    public static class Literal extends NodoAST {
        public String tipoDato;
        public Object valor;

        public Literal(int linea, String tipoDato, Object valor) {
            super(linea);
            this.tipoDato = tipoDato;
            this.valor = valor;
        }
    }
}
