package sintactico;

import lexico.LectorArchivo;
import lexico.Lexer;

public class MainSintactico {
    public static void main(String[] args) {
        String rutaArchivo = (args.length > 0) ? args[0] : "programa.txt";

        LectorArchivo lector = new LectorArchivo(rutaArchivo);
        lexico.ManejadorErrores erroresLexicos = new lexico.ManejadorErrores();
        Lexer lexer = new Lexer(lector, erroresLexicos);

        sintactico.ManejadorErrores erroresSintacticos = new sintactico.ManejadorErrores();
        Parser parser = new Parser(lexer, erroresSintacticos);

        NodoPrograma ast = parser.parsear();

        if (erroresLexicos.hayErrores()) {
            erroresLexicos.imprimirErrores();
        }

        if (erroresSintacticos.tieneErrores()) {
            erroresSintacticos.imprimirErrores();
        } else if (!erroresLexicos.hayErrores()) {
            System.out.println("Análisis léxico y sintáctico exitoso. AST generado:\n");
            parser.imprimirAST(ast);
        }
    }
}