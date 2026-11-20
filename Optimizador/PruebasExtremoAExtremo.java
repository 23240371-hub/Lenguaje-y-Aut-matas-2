/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilador;

// PRUEBAS DE EXTREMO A EXTREMO: GENERADOR -> OPTIMIZADOR -> CLASIFICADOR

public class PruebasExtremoAExtremo {

    public static void main(String[] args) {

        // -----------------------------------------------------------------
        // PRUEBA 1: Operaciones aritméticas redundantes + código muerto
        // Simula:
        //   w + 0;                 (operación sin usar -> código muerto)
        //   x = (a * 1) + (b - 0); (operaciones redundantes)
        // -----------------------------------------------------------------
        TablaSimbolos tabla1 = new TablaSimbolos("Prueba_1", null);
        tabla1.insertar("w", "int", null, 1);
        tabla1.insertar("a", "int", null, 1);
        tabla1.insertar("b", "int", null, 1);
        tabla1.insertar("x", "int", null, 1);

        NodoAST.OperacionBinaria opMuerta = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "w"), "+", new NodoAST.Literal(1, "int", 0)
        );

        NodoAST.OperacionBinaria ladoIzq = new NodoAST.OperacionBinaria(
            2, new NodoAST.Variable(2, "a"), "*", new NodoAST.Literal(2, "int", 1)
        );

        NodoAST.OperacionBinaria ladoDer = new NodoAST.OperacionBinaria(
            2, new NodoAST.Variable(2, "b"), "-", new NodoAST.Literal(2, "int", 0)
        );

        NodoAST.Asignacion asignacionX = new NodoAST.Asignacion(
            2, "x", new NodoAST.OperacionBinaria(2, ladoIzq, "+", ladoDer)
        );

        NodoAST.Programa programa1 = new NodoAST.Programa(opMuerta, asignacionX);

        ejecutarPrueba("PRUEBA 1: Aritmética redundante y código muerto", programa1, tabla1);


        // -----------------------------------------------------------------
        // PRUEBA 2: Programa completo con While, If y optimización interna
        // Simula:
        //   a + 0;                 (código muerto)
        //   while (x < 10) {
        //       x = x + 1;
        //       if (x == 5) {
        //           y = z * 1;     (simplificable)
        //       }
        //   }
        // -----------------------------------------------------------------
        TablaSimbolos tabla2 = new TablaSimbolos("Prueba_2", null);
        tabla2.insertar("a", "int", null, 1);
        tabla2.insertar("x", "int", null, 1);
        tabla2.insertar("y", "int", null, 1);
        tabla2.insertar("z", "int", null, 1);

        NodoAST.OperacionBinaria basura = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "a"), "+", new NodoAST.Literal(1, "int", 0)
        );

        NodoAST.OperacionBinaria condWhile = new NodoAST.OperacionBinaria(
            2, new NodoAST.Variable(2, "x"), "<", new NodoAST.Literal(2, "int", 10)
        );

        NodoAST.Asignacion incremento = new NodoAST.Asignacion(
            3, "x", new NodoAST.OperacionBinaria(3, new NodoAST.Variable(3, "x"), "+", new NodoAST.Literal(3, "int", 1))
        );

        NodoAST.OperacionBinaria condIf = new NodoAST.OperacionBinaria(
            4, new NodoAST.Variable(4, "x"), "==", new NodoAST.Literal(4, "int", 5)
        );

        NodoAST.Asignacion cuerpoIf = new NodoAST.Asignacion(
            5, "y", new NodoAST.OperacionBinaria(5, new NodoAST.Variable(5, "z"), "*", new NodoAST.Literal(5, "int", 1))
        );

        NodoAST.If bloqueIf = new NodoAST.If(4, condIf, cuerpoIf);
        NodoAST.While bloqueWhile = new NodoAST.While(2, condWhile, incremento, bloqueIf);
        NodoAST.Programa programa2 = new NodoAST.Programa(basura, bloqueWhile);

        ejecutarPrueba("PRUEBA 2: Ciclo While + If (Flujo completo)", programa2, tabla2);
        
        // -----------------------------------------------------------------
        // PRUEBA 3: Todas las reglas de Simplificación Algebraica
        // Simula:
        //   r1 = (0 + a) + (b * 0);
        //   r2 = (c * 2) - 0;
        // -----------------------------------------------------------------
        TablaSimbolos tabla3 = new TablaSimbolos("Prueba_3", null);
        tabla3.insertar("a", "int", null, 1);
        tabla3.insertar("b", "int", null, 1);
        tabla3.insertar("c", "int", null, 1);
        tabla3.insertar("r1", "int", null, 1);
        tabla3.insertar("r2", "int", null, 1);

        NodoAST.OperacionBinaria sumaCeroIzq = new NodoAST.OperacionBinaria(
            1, new NodoAST.Literal(1, "int", 0), "+", new NodoAST.Variable(1, "a")
        );
        NodoAST.OperacionBinaria multCero = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "b"), "*", new NodoAST.Literal(1, "int", 0)
        );
        NodoAST.Asignacion asigR1 = new NodoAST.Asignacion(
            1, "r1", new NodoAST.OperacionBinaria(1, sumaCeroIzq, "+", multCero)
        );

        NodoAST.OperacionBinaria multDos = new NodoAST.OperacionBinaria(
            2, new NodoAST.Variable(2, "c"), "*", new NodoAST.Literal(2, "int", 2)
        );
        NodoAST.OperacionBinaria restaCero = new NodoAST.OperacionBinaria(
            2, multDos, "-", new NodoAST.Literal(2, "int", 0)
        );
        NodoAST.Asignacion asigR2 = new NodoAST.Asignacion(2, "r2", restaCero);

        NodoAST.Programa programa3 = new NodoAST.Programa(asigR1, asigR2);
        ejecutarPrueba("PRUEBA 3: Simplificación algebraica (0+, -0, *0, *2)", programa3, tabla3);


        // -----------------------------------------------------------------
        // PRUEBA 4: Eliminación de código muerto en cascada (múltiples niveles)
        // Simula:
        //   (a + b) * (c - d);   (genera t1, t2 y t3 sin asignarse a nadie)
        //   res = a + 5;         (instrucción útil que debe sobrevivir)
        // -----------------------------------------------------------------
        TablaSimbolos tabla4 = new TablaSimbolos("Prueba_4", null);
        tabla4.insertar("a", "int", null, 1);
        tabla4.insertar("b", "int", null, 1);
        tabla4.insertar("c", "int", null, 1);
        tabla4.insertar("d", "int", null, 1);
        tabla4.insertar("res", "int", null, 1);

        NodoAST.OperacionBinaria sumaMuerta = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "a"), "+", new NodoAST.Variable(1, "b")
        );
        NodoAST.OperacionBinaria restaMuerta = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "c"), "-", new NodoAST.Variable(1, "d")
        );
        NodoAST.OperacionBinaria multMuerta = new NodoAST.OperacionBinaria(
            1, sumaMuerta, "*", restaMuerta
        );

        NodoAST.Asignacion asigUtil = new NodoAST.Asignacion(
            2, "res", new NodoAST.OperacionBinaria(2, new NodoAST.Variable(2, "a"), "+", new NodoAST.Literal(2, "int", 5))
        );

        NodoAST.Programa programa4 = new NodoAST.Programa(multMuerta, asigUtil);
        ejecutarPrueba("PRUEBA 4: Código muerto en cascada (3 temporales)", programa4, tabla4);


        // -----------------------------------------------------------------
        // PRUEBA 5: Clasificación de tipos (float y boolean)
        // Simula:
        //   monto = precio + 15.5;   (temporal debe quedar como float)
        //   if (monto > 100) {       (temporal debe quedar como boolean)
        //       descuento = 10;
        //   }
        // -----------------------------------------------------------------
        TablaSimbolos tabla5 = new TablaSimbolos("Prueba_5", null);
        tabla5.insertar("precio", "float", null, 1);
        tabla5.insertar("monto", "float", null, 1);
        tabla5.insertar("descuento", "int", null, 1);

        NodoAST.Asignacion calcMonto = new NodoAST.Asignacion(
            1, "monto", new NodoAST.OperacionBinaria(
                1, new NodoAST.Variable(1, "precio"), "+", new NodoAST.Literal(1, "float", 15.5)
            )
        );
        NodoAST.OperacionBinaria condDescuento = new NodoAST.OperacionBinaria(
            2, new NodoAST.Variable(2, "monto"), ">", new NodoAST.Literal(2, "int", 100)
        );
        NodoAST.Asignacion aplicaDescuento = new NodoAST.Asignacion(
            3, "descuento", new NodoAST.Literal(3, "int", 10)
        );
        NodoAST.If bloqueDescuento = new NodoAST.If(2, condDescuento, aplicaDescuento);

        NodoAST.Programa programa5 = new NodoAST.Programa(calcMonto, bloqueDescuento);
        ejecutarPrueba("PRUEBA 5: Clasificación de tipos float y boolean", programa5, tabla5);


        // -----------------------------------------------------------------
        // PRUEBA 6: Caso control (código ya óptimo sin redundancias)
        // Simula:
        //   resultado = (a + b) * c;
        // -----------------------------------------------------------------
        TablaSimbolos tabla6 = new TablaSimbolos("Prueba_6", null);
        tabla6.insertar("a", "int", null, 1);
        tabla6.insertar("b", "int", null, 1);
        tabla6.insertar("c", "int", null, 1);
        tabla6.insertar("resultado", "int", null, 1);

        NodoAST.OperacionBinaria sumaAB = new NodoAST.OperacionBinaria(
            1, new NodoAST.Variable(1, "a"), "+", new NodoAST.Variable(1, "b")
        );
        NodoAST.Asignacion asigFinal = new NodoAST.Asignacion(
            1, "resultado", new NodoAST.OperacionBinaria(1, sumaAB, "*", new NodoAST.Variable(1, "c"))
        );

        NodoAST.Programa programa6 = new NodoAST.Programa(asigFinal);
        ejecutarPrueba("PRUEBA 6: Caso control (sin cambios esperados)", programa6, tabla6);
    }

    // =====================================================================
    // MÉTODO AUXILIAR 1: Ejecuta el flujo completo de una prueba
    // =====================================================================
    public static void ejecutarPrueba(String nombrePrueba, NodoAST.Programa arbol, TablaSimbolos tabla) {

        System.out.println("===============================================================");
        System.out.println(" " + nombrePrueba);
        System.out.println("===============================================================");

        // 1. Limpiamos la lista global para que no se mezclen cuádruplos de pruebas anteriores
        Cuadruplo.listaCuadruplos.clear();

        // 2. Generación de Código Intermedio desde el AST
        GeneradorIntermedio generador = new GeneradorIntermedio();
        generador.traducir(arbol);

        // 3. Guardamos el número de cuádruplos ANTES de optimizar y mostramos la tabla
        int cuadruplosAntes = Cuadruplo.listaCuadruplos.size();
        System.out.println("\n--- ANTES DE OPTIMIZAR (" + cuadruplosAntes + " cuádruplos) ---");
        Cuadruplo.imprimirTabla();

        // 4. Optimización en cascada
        TACOptimizador optimizador = new TACOptimizador();
        optimizador.optimizar();

        // 5. Guardamos el número de cuádruplos DESPUÉS de optimizar y mostramos la tabla
        int cuadruplosDespues = Cuadruplo.listaCuadruplos.size();
        System.out.println("--- DESPUÉS DE OPTIMIZAR (" + cuadruplosDespues + " cuádruplos) ---");
        Cuadruplo.imprimirTabla();

        // 6. CLASIFICACIÓN: Registramos en TablaSimbolos solo los temporales que sobrevivieron
        ClasificadorTemporales clasificador = new ClasificadorTemporales();
        clasificador.clasificarYRegistrar(tabla);

        // 7. Imprimimos la tabla de símbolos final y el resumen comparativo
        tabla.imprimirTabla();
        System.out.println("Memoria total reservada (bytes): " + tabla.getMemoriaTotalAsignada());
        mostrarComparacion(cuadruplosAntes, cuadruplosDespues);
    }
    // MÉTODO AUXILIAR 2: Calcula e imprime la comparación Antes vs Después
    private static void mostrarComparacion(int antes, int despues) {
        int eliminados = antes - despues;
        double porcentaje = 0;

        if (antes > 0) {
            porcentaje = (eliminados * 100.0) / antes;
        }

        System.out.println("\n--- RESUMEN DE OPTIMIZACIÓN ---");
        System.out.println("Cuádruplos antes:      " + antes);
        System.out.println("Cuádruplos después:    " + despues);
        System.out.println("Cuádruplos eliminados: " + eliminados);
        System.out.printf("Reducción lograda:     %.1f%%%n", porcentaje);

        // Validación automática sencilla de la prueba
        if (despues < antes) {
            System.out.println("Estado de la prueba:   [EXITOSA] El optimizador redujo el código.");
        } else {
            System.out.println("Estado de la prueba:   [SIN CAMBIOS] No se redujeron cuádruplos.");
        }
        System.out.println();
    }
}