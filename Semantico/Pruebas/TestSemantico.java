import prueba.NodoAST.*;

public class TestCSemantico {

    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("  PRUEBA DE MEMORIA Y CONDICIONES");
        System.out.println("=======================================================");

        NodoAST arbolPrograma = new Programa(

            new Declaracion(1, "int", "a", new Literal(1, "int", 10)),

            new Declaracion(2, "float", "b", new Literal(2, "float", 5.5f)),

            new Declaracion(3, "double", "c", new Literal(3, "double", 20.5)),

            new Declaracion(4, "bool", "activo", new Literal(4, "bool", true)),

            new Declaracion(5, "char", "letra", new Literal(5, "char", 'A')),

            new If(7,
                new OperacionBinaria(7,
                    new OperacionBinaria(7, new Variable(7, "a"), "<", new Variable(7, "b")),
                    "&&",
                    new Variable(7, "activo")
                ),

                new Declaracion(8, "int", "dentroIf", new Literal(8, "int", 1))
            ),

            new Declaracion(10, "int", "despuesIf", new Literal(10, "int", 99)),

            new If(12,
                new Variable(12, "a")
            ),

            new While(14,
                new OperacionBinaria(14, new Variable(14, "a"), "+", new Variable(14, "b")),
                new Asignacion(15, "a", new OperacionBinaria(15, new Variable(15, "a"), "+", new Literal(15, "int", 1)))
            ),

            new While(17,
                new OperacionBinaria(17, new Variable(17, "a"), "<=", new Literal(17, "int", 20)),
                new Asignacion(18, "a", new OperacionBinaria(18, new Variable(18, "a"), "+", new Literal(18, "int", 1)))
            )
        );

        ManejadorErrores manejadorErrores = new ManejadorErrores();
        AnalizadorSemantico semantico = new AnalizadorSemantico(manejadorErrores);

        semantico.recorrerAST(arbolPrograma);

        semantico.imprimirTablas();
        manejadorErrores.imprimirErrores();
    }
}
