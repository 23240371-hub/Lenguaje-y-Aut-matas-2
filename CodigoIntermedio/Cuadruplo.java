package com.mycompany.compilador;

import java.util.List;

public class Cuadruplo {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;

    // Constructor que guarda los cuatro campos obligatorios
    public Cuadruplo(String operador, String arg1, String arg2, String resultado) {
        this.operador = operador;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = resultado;
    }

    // Método para exportar e imprimir los cuádruplos como tabla formateada
    public static void imprimirTabla(List<Cuadruplo> cuadruplos) {
        // Imprimir el encabezado con tabuladores fijos
        System.out.printf("%-6s | %-12s | %-12s | %-12s | %-12s%n", 
                          "Índice", "Operador", "Arg1", "Arg2", "Resultado");
        System.out.println("---------------------------------------------------------------------");
        
        // Recorrer secuencialmente la lista de cuádruplos
        for (int i = 0; i < cuadruplos.size(); i++) {
            Cuadruplo c = cuadruplos.get(i);
            
            // Reemplazar valores nulos o vacíos con un guion para no romper el formato
            String op = (c.operador != null && !c.operador.isEmpty()) ? c.operador : "-";
            String arg1 = (c.arg1 != null && !c.arg1.isEmpty()) ? c.arg1 : "-";
            String arg2 = (c.arg2 != null && !c.arg2.isEmpty()) ? c.arg2 : "-";
            String res = (c.resultado != null && !c.resultado.isEmpty()) ? c.resultado : "-";
            
            // Imprimir cada fila alineada con su número de línea (índice)
            System.out.printf("%-6d | %-12s | %-12s | %-12s | %-12s%n", 
                              i, op, arg1, arg2, res);
        }
    }
}
