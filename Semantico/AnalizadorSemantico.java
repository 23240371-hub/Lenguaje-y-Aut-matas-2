import java.util.ArrayList;
import java.util.List;

public class AnalizadorSemantico {

    private TablaSimbolos tablaActual;
    private ManejadorErrores errores;
    private ValidadorTipos validador;
    private List<TablaSimbolos> historialTablas;
    private int contadorBloques;

    public AnalizadorSemantico(ManejadorErrores errores) {
        this.errores = errores;
        this.validador = new ValidadorTipos(errores);
        this.tablaActual = new TablaSimbolos("global", errores);
        this.historialTablas = new ArrayList<>();
        this.historialTablas.add(this.tablaActual);
        this.contadorBloques = 0;
    }

    public void recorrerAST(NodoAST nodo) {
        if (nodo == null) return;

        if (nodo instanceof NodoAST.Programa) {
            NodoAST.Programa programa = (NodoAST.Programa) nodo;
            for (NodoAST sentencia : programa.sentencias) {
                recorrerAST(sentencia);
            }

        } else if (nodo instanceof NodoAST.Declaracion) {
            NodoAST.Declaracion decl = (NodoAST.Declaracion) nodo;
            Object valorInicial = null;

            if (decl.expresionInicial != null) {
                String tipoExpresion = evaluarExpresion(decl.expresionInicial);
                boolean esCompatible = validador.validarAsignacion(decl.tipoDato, tipoExpresion, decl.linea);

                if (esCompatible && decl.expresionInicial instanceof NodoAST.Literal) {
                    valorInicial = ((NodoAST.Literal) decl.expresionInicial).valor;
                }
            }

            tablaActual.insertar(decl.nombre, decl.tipoDato, valorInicial, decl.linea);

        } else if (nodo instanceof NodoAST.Asignacion) {
            NodoAST.Asignacion asig = (NodoAST.Asignacion) nodo;
            String tipoExpr = evaluarExpresion(asig.expresion);
            Simbolo variableDestino = tablaActual.buscar(asig.nombreVariable);

            if (variableDestino == null) {
                errores.agregarErrorSemantico(asig.linea,
                        "Variable no declarada: No se puede asignar un valor a '" + asig.nombreVariable + "'.");
            } else {
                validador.validarAsignacion(variableDestino, tipoExpr, asig.linea);
            }

        } else if (nodo instanceof NodoAST.If) {
            NodoAST.If nodoIf = (NodoAST.If) nodo;

            String tipoCondicion = evaluarExpresion(nodoIf.condicion);
            validador.validarCondicion(tipoCondicion, "if", nodoIf.linea);

            contadorBloques++;
            TablaSimbolos ambitoAnterior = this.tablaActual;
            this.tablaActual = new TablaSimbolos("id_if_" + contadorBloques, ambitoAnterior, errores);
            this.historialTablas.add(this.tablaActual);

            for (NodoAST sentencia : nodoIf.bloqueEntonces) {
                recorrerAST(sentencia);
            }

            this.tablaActual = ambitoAnterior;

        } else if (nodo instanceof NodoAST.While) {
            NodoAST.While nodoWhile = (NodoAST.While) nodo;

            String tipoCondicion = evaluarExpresion(nodoWhile.condicion);
            validador.validarCondicion(tipoCondicion, "while", nodoWhile.linea);

            contadorBloques++;
            TablaSimbolos ambitoAnterior = this.tablaActual;
            this.tablaActual = new TablaSimbolos("id_while_" + contadorBloques, ambitoAnterior, errores);
            this.historialTablas.add(this.tablaActual);

            for (NodoAST sentencia : nodoWhile.cuerpo) {
                recorrerAST(sentencia);
            }

            this.tablaActual = ambitoAnterior;
        }
    }

    private String evaluarExpresion(NodoAST expr) {
        if (expr == null) {
            return ValidadorTipos.TIPO_ERROR;
        }

        if (expr instanceof NodoAST.Literal) {
            return ((NodoAST.Literal) expr).tipoDato;
        }

        if (expr instanceof NodoAST.Variable) {
            NodoAST.Variable var = (NodoAST.Variable) expr;
            Simbolo simboloEncontrado = tablaActual.buscar(var.nombre);

            if (simboloEncontrado == null) {
                errores.agregarErrorSemantico(var.linea,
                        "Variable no declarada: '" + var.nombre + "' no existe en el ámbito '" + tablaActual.getNombreAmbito() + "'.");
                return ValidadorTipos.TIPO_ERROR;
            }
            return simboloEncontrado.getTipoDato();
        }

        if (expr instanceof NodoAST.OperacionBinaria) {
            NodoAST.OperacionBinaria op = (NodoAST.OperacionBinaria) expr;
            String tipoIzq = evaluarExpresion(op.izquierda);
            String tipoDer = evaluarExpresion(op.derecha);
            return validador.inferirTipoBinario(tipoIzq, op.operador, tipoDer, op.linea);
        }

        return ValidadorTipos.TIPO_ERROR;
    }

    public void imprimirTablas() {
        for (TablaSimbolos t : historialTablas) {
            t.imprimirTabla();
        }
        System.out.println("\n>>> Espacio físico total reservado en memoria: " + tablaActual.getMemoriaTotalAsignada() + " bytes.");
    }
}
