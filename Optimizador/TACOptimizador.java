package com.mycompany.compilador;


import java.util.ArrayList;
import java.util.List;

// Clase principal del Optimizador
public class TACOptimizador {

    // Método que aplica la técnica de Simplificación Algebraica
    public void simplificacionAlgebraica(List<Cuadruplo> lista) {
        for (Cuadruplo c : lista) {
            // 1. Elimina sumas con 0
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("0".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                }
            } 
            // 2. Simplificaciones de multiplicación
            else if (c.operador.equals("*")) {
                if ("0".equals(c.arg1) || "0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg1 = "0";
                    c.arg2 = null;
                } else if ("1".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("1".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                } else if ("2".equals(c.arg2)) {
                    c.operador = "+";
                    c.arg2 = c.arg1;
                } else if ("2".equals(c.arg1)) {
                    c.operador = "+";
                    c.arg1 = c.arg2;
                }
            }
            // 3. Eliminar restas con 0
            else if (c.operador.equals("-")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                }
            }
        }
    }

    // 3. Método MAIN para ejecutar la prueba independiente
    public static void main(String[] args) {
        // Creamos una lista de instrucciones falsas sin optimizar
        List<Cuadruplo> codigoPrueba = new ArrayList<>();
        
        // Simulación de instrucciones intermedias
        codigoPrueba.add(new Cuadruplo("-", "x", "0", "t1")); // t1 = x + 0
        codigoPrueba.add(new Cuadruplo("+", "0", "y", "t2")); // t2 = 0 + y
        codigoPrueba.add(new Cuadruplo("*", "z", "1", "t3")); // t3 = z * 1
        codigoPrueba.add(new Cuadruplo("*", "a", "0", "t4")); // t4 = a * 0
        codigoPrueba.add(new Cuadruplo("*", "b", "2", "t5")); // t5 = b * 2
        codigoPrueba.add(new Cuadruplo("+", "t1", "t2", "x")); // Instrucción normal

        System.out.println("CÓDIGO INTERMEDIO ORIGINAL (Sin optimizar):");
        Cuadruplo.imprimirTabla(codigoPrueba);

        // Instanciamos el optimizador y ejecutamos la simplificación
        TACOptimizador optimizador = new TACOptimizador();
        optimizador.simplificacionAlgebraica(codigoPrueba);

        System.out.println("CÓDIGO INTERMEDIO OPTIMIZADO:");
        Cuadruplo.imprimirTabla(codigoPrueba);
    }
}