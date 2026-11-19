import java.util.ArrayList;
import java.util.List;

// Fase de generador de código interrmedio (Postorden + Saltos)
public class GeneradorIntermedio {

    private int contadorTemporales = 1; // Genera t1, t2, t3...
    private int contadorEtiquetas = 1;  // Genera L1, L2, L3...

    // Crea un nombre único para cada temporal
    private String crearNuevoTemporal() {
        return "t" + (contadorTemporales++);
    }

    // Crea un nombre único para cada etiqueta de salto
    private String crearNuevaEtiqueta() {
        return "L" + (contadorEtiquetas++);
    }

    // Método recursivo principal que recorre el AST en Postorden y genera cuádruplos
    public String traducir(NodoAST nodo) {
        if (nodo == null) {
            return "";
        }

        // 1. Operaciones binarias (+, -, *, /, <, >, ==, !=, etc.)
        if (nodo instanceof NodoAST.OperacionBinaria op) {
            // Postorden: primero se resuelven los hijo izquierdos y luego derechos
            String izq = traducir(op.izquierda);
            String der = traducir(op.derecha);
            // Luego se crea un temporal y se guarda la operación
            String temporal = crearNuevoTemporal();
            Cuadruplo.generar(op.operador, izq, der, temporal);
            return temporal;
        }

        // 2. Asignaciones (ej. x = a + 5)
        else if (nodo instanceof NodoAST.Asignacion asig) {
            String resultadoExpresion = traducir(asig.expresion);
            Cuadruplo.generar("=", resultadoExpresion, null, asig.nombreVariable);
            return asig.nombreVariable;
        }

        // 3. Delcaracion con valores iniciales (ej. int x = 10)
        else if (nodo instanceof NodoAST.Declaracion decl) {
            if (decl.expresionInicial != null) {
                String valorInicial = traducir(decl.expresionInicial);
                Cuadruplo.generar("=", valorInicial, null, decl.nombre);
            }
            return decl.nombre;
        }

        // 4. Las hojas de los arboles (Literales y Variables)
        else if (nodo instanceof NodoAST.Literal lit) {
            return lit.valor.toString();
        }
        else if (nodo instanceof NodoAST.Variable var) {
            return var.nombre;
        }

        // 5. Condicional if
        else if (nodo instanceof NodoAST.If nodoIf) {
            // Traduce la condición y obtiene su temporal (ejemplo : t1)
            String condicionTemp = traducir(nodoIf.condicion);
            // Crea etiqueta de salida
            String lFin = crearNuevaEtiqueta();
            // Si la condición es falsa, salta al final del If
            Cuadruplo.generar("IF_FALSE", condicionTemp, null, lFin);
            // Traduce todas las sentencias dentro del cuerpo del If
            for (NodoAST sentencia : nodoIf.bloqueEntonces) {
                traducir(sentencia);
            }
            // Coloca la marca física de salida
            Cuadruplo.generar("LABEL", null, null, lFin);
            return "";
        }

        // 6. CICLO WHILE
        else if (nodo instanceof NodoAST.While nodoWhile) {
            // Crea etiquetas de inicio (para regresar) y fin (para salir)
            String lInicio = crearNuevaEtiqueta();
            String lFin = crearNuevaEtiqueta();
            // Marca de inicio del ciclo
            Cuadruplo.generar("LABEL", null, null, lInicio);
            // Evalúa la condición en cada vuelta
            String condicionTemp = traducir(nodoWhile.condicion);
            // Si la condición es falsa, rompe el ciclo saltando a lFin
            Cuadruplo.generar("IF_FALSE", condicionTemp, null, lFin);
            // Traduce el cuerpo del ciclo
            for (NodoAST sentencia : nodoWhile.cuerpo) {
                traducir(sentencia);
            }
            // Salto incondicional de regreso al inicio del ciclo
            Cuadruplo.generar("GOTO", null, null, lInicio);
            // Marca de salida del ciclo
            Cuadruplo.generar("LABEL", null, null, lFin);
            return "";
        }

        // 7. PROGRAMA PRINCIPAL (Lista raíz de sentencias)
        else if (nodo instanceof NodoAST.Programa prog) {
            for (NodoAST sentencia : prog.sentencias) {
                traducir(sentencia);
            }
        }

        return "";
    }
}

// Clase contenedora de Cuádruplos 
class Cuadruplo {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;

    // Almacén global estático donde se guardan todas las instrucciones
    public static List<Cuadruplo> listaCuadruplos = new ArrayList<>();

    public Cuadruplo(String operador, String arg1, String arg2, String resultado) {
        this.operador = operador;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = resultado;
    }

    // Crea un cuádruplo y lo agrega automáticamente a la lista global
    public static void generar(String operador, String arg1, String arg2, String resultado) {
        listaCuadruplos.add(new Cuadruplo(operador, arg1, arg2, resultado));
    }

    // Limpia la lista (útil si se compilan varios programas seguidos)
    public static void limpiar() {
        listaCuadruplos.clear();
    }

    // Imprime la lista completa formateada como tabla con índice
    public static void imprimirTabla() {
        System.out.printf("%-6s | %-10s | %-10s | %-10s | %-10s%n",
                          "Índice", "Operador", "Arg1", "Arg2", "Resultado");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < listaCuadruplos.size(); i++) {
            Cuadruplo c = listaCuadruplos.get(i);
            String op = (c.operador != null && !c.operador.isEmpty()) ? c.operador : "-";
            String a1 = (c.arg1 != null && !c.arg1.isEmpty()) ? c.arg1 : "-";
            String a2 = (c.arg2 != null && !c.arg2.isEmpty()) ? c.arg2 : "-";
            String res = (c.resultado != null && !c.resultado.isEmpty()) ? c.resultado : "-";
            System.out.printf("%-6d | %-10s | %-10s | %-10s | %-10s%n", i, op, a1, a2, res);
        }
        System.out.println();
    }
}
