
public class pruevaSintactico {
    public static void main(String[] args) {
        String nombreArchivo = "gramatica.txt"; 
        
       EstructurasDelLenguaje extractor = new EstructurasDelLenguaje(nombreArchivo);

        System.out.println("• Símbolos No Terminales (Variables):");
        System.out.println("  " + extractor.getNoTerminales());
        System.out.println("\n• Símbolos Terminales (Alfabeto/Tokens):");
        System.out.println("  " + extractor.getTerminales());
        System.out.println("\n• Reglas de Producción detectadas:");
        
        for (int i = 0; i < extractor.getLadoIzquierdo().size(); i++) {
            String nt = extractor.getLadoIzquierdo().get(i);
            String[] opciones = extractor.getLadoDerecho().get(i);
            System.out.printf("  %-15s -> %s\n", nt, String.join(" | ", opciones));
        }
        System.out.println();

       
        GeneradorMatrizPredictiva analizador = new GeneradorMatrizPredictiva(extractor);

        
        analizador.mostrarMatriz();
        
    }
}
