import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// 1. Clase contenedora incluida para ejecución autónoma
class Cuadruplo {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;

    public Cuadruplo(String operador, String arg1, String arg2, String resultado) {
        this.operador = operador;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = resultado;
    }

    public static void imprimirTabla(List<Cuadruplo> cuadruplos) {
        System.out.printf("%-6s | %-10s | %-10s | %-10s | %-10s%n", 
                          "Índice", "Operador", "Arg1", "Arg2", "Resultado");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < cuadruplos.size(); i++) {
            Cuadruplo c = cuadruplos.get(i);
            String op = (c.operador != null && !c.operador.isEmpty()) ? c.operador : "-";
            String a1 = (c.arg1 != null && !c.arg1.isEmpty()) ? c.arg1 : "-";
            String a2 = (c.arg2 != null && !c.arg2.isEmpty()) ? c.arg2 : "-";
            String res = (c.resultado != null && !c.resultado.isEmpty()) ? c.resultado : "-";
            System.out.printf("%-6d | %-10s | %-10s | %-10s | %-10s%n", i, op, a1, a2, res);
        }
        System.out.println();
    }
}

// 2. Clase principal con métodos y simulación
public class TACOptimizador_Pruebas {

    public void simplificacionAlgebraica(List<Cuadruplo> lista) {
        for (Cuadruplo c : lista) {
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("0".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                }
            } else if (c.operador.equals("-")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                }
            } else if (c.operador.equals("*")) {
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
        }
    }

    public void eliminacionCodigoMuerto(List<Cuadruplo> lista) {
        boolean huboCambios;
        do {
            huboCambios = false;
            Iterator<Cuadruplo> it = lista.iterator();
            while (it.hasNext()) {
                Cuadruplo c = it.next();
                if (c.resultado != null && c.resultado.startsWith("t")) {
                    boolean usado = false;
                    for (Cuadruplo revisado : lista) {
                        if (c.resultado.equals(revisado.arg1) || c.resultado.equals(revisado.arg2)) {
                            usado = true;
                            break;
                        }
                    }
                    if (!usado) {
                        it.remove();
                        huboCambios = true;
                        break; 
                    }
                }
            }
        } while (huboCambios); 
    }

    public void optimizar(List<Cuadruplo> codigoIntermedio) {
        boolean huboCambios;
        do {
            String estadoAnterior = generarFirma(codigoIntermedio);

            simplificacionAlgebraica(codigoIntermedio);
            eliminacionCodigoMuerto(codigoIntermedio);

            String estadoActual = generarFirma(codigoIntermedio);
            huboCambios = !estadoAnterior.equals(estadoActual);

        } while (huboCambios); 
    }

    private String generarFirma(List<Cuadruplo> lista) {
        StringBuilder sb = new StringBuilder();
        for (Cuadruplo c : lista) {
            sb.append(c.operador).append(c.arg1).append(c.arg2).append(c.resultado);
        }
        return sb.toString();
    }

    // 3. Método MAIN de prueba
    public static void main(String[] args) {
        List<Cuadruplo> codigoPrueba = new ArrayList<>();
        
        // Simulación
        codigoPrueba.add(new Cuadruplo("-", "y", "0", "t1")); // t1 = y - 0 (Nueva regla)
        codigoPrueba.add(new Cuadruplo("*", "a", "1", "t2")); // t2 = a * 1
        codigoPrueba.add(new Cuadruplo("+", "z", "0", "t3")); // t3 = z + 0 (Se simplifica a t3 = z, pero nadie usa t3, así que morirá en la eliminación)
        codigoPrueba.add(new Cuadruplo("+", "t1", "t2", "x")); // x = t1 + t2

        System.out.println("CÓDIGO INTERMEDIO ORIGINAL (Sin optimizar):");
        Cuadruplo.imprimirTabla(codigoPrueba);

        TACOptimizador optimizador = new TACOptimizador();
        optimizador.optimizar(codigoPrueba);

        System.out.println("CÓDIGO INTERMEDIO TRAS LA OPTIMIZACIÓN (Cascada final):");
        Cuadruplo.imprimirTabla(codigoPrueba);
    }
}