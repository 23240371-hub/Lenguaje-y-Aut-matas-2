import prueba.NodoAST.*;

public class PruebasVarias {

    private static int pruebasPasadas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  SUITE DE PRUEBAS SEMÁNTICAS (AST SINTÁCTICAMENTE CORRECTOS)");
        System.out.println("=================================================================\n");

        prueba1_ProgramaCompletamenteValido();
        prueba2_VariablesFantasma();
        prueba3_RedeclaracionEnMismoBloque();
        prueba4_IncompatibilidadTiposAsignacion();
        prueba5_OperacionesYComparacionesInvalidas();
        prueba6_CondicionesNoBooleanasEnControl();

        System.out.println("\n=================================================================");
        System.out.printf("  RESUMEN FINAL DE LA SUITE: %d PASARON | %d FALLARON%n", pruebasPasadas, pruebasFallidas);
        System.out.println("=================================================================");
    }

    private static void prueba1_ProgramaCompletamenteValido() {
        NodoAST ast = new Programa(
            new Declaracion(1, "int", "edad", new Literal(1, "int", 21)),          
            new Declaracion(2, "float", "salario", new Literal(2, "float", 1500.5f)),
            new Declaracion(3, "double", "bono", new Literal(3, "double", 300.25)),
            new Declaracion(4, "bool", "esActivo", new Literal(4, "bool", true)),  
            new Declaracion(5, "char", "categoria", new Literal(5, "char", 'A')),   

            new Asignacion(7, "bono",
                new OperacionBinaria(7, new Variable(7, "edad"), "+", new Variable(7, "salario"))
            ),

            new If(9,
                new OperacionBinaria(9,
                    new OperacionBinaria(9, new Variable(9, "edad"), ">=", new Literal(9, "int", 18)),
                    "&&",
                    new Variable(9, "esActivo")
                ),
                new Declaracion(10, "int", "edad", new Literal(10, "int", 99)),   
                new Asignacion(11, "salario",
                    new OperacionBinaria(11, new Variable(11, "edad"), "*", new Literal(11, "float", 2.0f))
                )
            )
        );

        ejecutarCaso("CASO 1: Programa Semánticamente Válido (Promoción, Offsets y Ámbitos)", ast, 0, true);
    }

    private static void prueba2_VariablesFantasma() {
        NodoAST ast = new Programa(
            new Asignacion(1, "contador", new Literal(1, "int", 5)),
            new Declaracion(2, "int", "contador", new Literal(2, "int", 10)),
            new Declaracion(3, "int", "total",
                new OperacionBinaria(3, new Variable(3, "contador"), "+", new Variable(3, "fantasma"))
            ),

            new Declaracion(4, "int", "auto",
                new OperacionBinaria(4, new Variable(4, "auto"), "+", new Literal(4, "int", 1))
            ),

            new While(6,
                new OperacionBinaria(6, new Variable(6, "contador"), "<", new Literal(6, "int", 20)),
                new Declaracion(7, "int", "temporalWhile", new Literal(7, "int", 1))
            ),

            new Asignacion(9, "contador", new Variable(9, "temporalWhile"))
        );

        ejecutarCaso("CASO 2: Variables Fantasma y Fuera de Alcance (Scope)", ast, 4, false);
    }

    private static void prueba3_RedeclaracionEnMismoBloque() {
        NodoAST ast = new Programa(
            new Declaracion(1, "int", "x", new Literal(1, "int", 10)),
            new Declaracion(2, "float", "x", new Literal(2, "float", 5.5f)),

            new If(4,
                new Literal(4, "bool", true),
                new Declaracion(5, "int", "x", new Literal(5, "int", 20)),
                new Declaracion(6, "int", "x", new Literal(6, "int", 30))
            )
        );

        ejecutarCaso("CASO 3: Redeclaración de Variables en el Mismo Bloque", ast, 2, false);
    }

    private static void prueba4_IncompatibilidadTiposAsignacion() {
        NodoAST ast = new Programa(

            new Declaracion(1, "int", "numero", new Literal(1, "String", "Hola, papu")),
            new Declaracion(2, "float", "decimal", new Literal(2, "float", 9.8f)),
            new Declaracion(3, "bool", "bandera", new Literal(3, "bool", false)),

            new Asignacion(4, "numero", new Variable(4, "decimal")),

            new Asignacion(5, "decimal", new Variable(5, "bandera"))
        );

        ejecutarCaso("CASO 4: Incompatibilidad de Tipos en Asignación", ast, 3, false);
    }

    private static void prueba5_OperacionesYComparacionesInvalidas() {
        NodoAST ast = new Programa(
            new Declaracion(1, "int", "a", new Literal(1, "int", 10)),
            new Declaracion(2, "double", "b", new Literal(2, "double", 20.5)),
            new Declaracion(3, "bool", "flag", new Literal(3, "bool", true)),

            new Asignacion(4, "a",
                new OperacionBinaria(4, new Variable(4, "a"), "+", new Variable(4, "flag"))
            ),

            new Asignacion(5, "flag",
                new OperacionBinaria(5, new Variable(5, "b"), "<", new Variable(5, "flag"))
            ),

            new Asignacion(6, "flag",
                new OperacionBinaria(6, new Variable(6, "a"), "==", new Variable(6, "flag"))
            ),

            new Asignacion(7, "flag",
                new OperacionBinaria(7, new Variable(7, "a"), "&&", new Variable(7, "b"))
            )
        );

        ejecutarCaso("CASO 5: Operaciones Aritméticas, Relacionales y Lógicas Inválidas", ast, 4, false);
    }

    private static void prueba6_CondicionesNoBooleanasEnControl() {
        NodoAST ast = new Programa(
            new Declaracion(1, "int", "contador", new Literal(1, "int", 1)),
            new Declaracion(2, "float", "precio", new Literal(2, "float", 12.5f)),
            new Declaracion(3, "String", "texto", new Literal(3, "String", "activo")),

            new If(4, new Variable(4, "contador")),

            new While(6,
                new OperacionBinaria(6, new Variable(6, "contador"), "+", new Variable(6, "precio"))
            ),

            new If(8, new Variable(8, "texto"))
        );

        ejecutarCaso("CASO 6: Condiciones No Booleanas en if(...) y while(...)", ast, 3, false);
    }

    private static void ejecutarCaso(String titulo, NodoAST ast, int erroresEsperados, boolean mostrarTablas) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println(titulo);
        System.out.println("-----------------------------------------------------------------");

        ManejadorErrores manejador = new ManejadorErrores();
        AnalizadorSemantico semantico = new AnalizadorSemantico(manejador);

        semantico.recorrerAST(ast);

        int erroresDetectados = manejador.getListaErrores().size();
        boolean exito = (erroresDetectados == erroresEsperados);

        if (exito) {
            pruebasPasadas++;
            System.out.printf("[PASS] Errores esperados: %d | Errores detectados: %d%n", erroresEsperados, erroresDetectados);
        } else {
            pruebasFallidas++;
            System.err.printf("[FAIL] Errores esperados: %d | Errores detectados: %d%n", erroresEsperados, erroresDetectados);
        }

        if (mostrarTablas) {
            semantico.imprimirTablas();
        }

        if (manejador.tieneErrores()) {
            for (ManejadorErrores.ErrorCompilador err : manejador.getListaErrores()) {
                System.out.println("   -> " + err);
            }
        } else {
            System.out.println("   -> Sin errores semánticos.");
        }
        System.out.println();
    }
   
}
