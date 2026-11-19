class MainSintactico {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA 1: PROGRAMA VÁLIDO (BAJO DEMANDA) ===");
        ManejadorErrores manejador1 = new ManejadorErrores();
        Parser parserValido = new Parser(MockTokens.crearProveedorProgramaValido(), manejador1);
        NodoPrograma ast1 = parserValido.parsear();

        if (!manejador1.tieneErrores()) {
            System.out.println("Análisis sintáctico exitoso. AST generado:\n");
            parserValido.imprimirAST(ast1);
        } else {
            manejador1.imprimirErrores();
        }

        System.out.println("\n=== PRUEBA 2: PROGRAMA CON CLASS / MAIN Y MÓDULO (%) ===");
        ManejadorErrores manejador2 = new ManejadorErrores();
        Parser parserClase = new Parser(MockTokens.crearProveedorConClaseYModulo(), manejador2);
        NodoPrograma ast2 = parserClase.parsear();

        if (!manejador2.tieneErrores()) {
            System.out.println("Análisis sintáctico exitoso. AST generado:\n");
            parserClase.imprimirAST(ast2);
        } else {
            manejador2.imprimirErrores();
        }

        System.out.println("\n=== PRUEBA 3: MODO PÁNICO CON MANEJADOR DE ERRORES CENTRALIZADO ===");
        ManejadorErrores manejador3 = new ManejadorErrores();
        Parser parserErrores = new Parser(MockTokens.crearProveedorConErrores(), manejador3);
        parserErrores.parsear();
        manejador3.imprimirErrores();
    }
}
