import java.util.List;

public class Optimizador {

    public void simplificacionAlgebraica(List<Cuadruplo> lista) {
        for (Cuadruplo c : lista) {
            
            // 1. Elimina sumas con 0
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) {
                    // x + 0 = x (se convierte en asignación simple: resultado es = x)
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("0".equals(c.arg1)) {
                    // 0 + x -> x
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                }
            } 
            // 2. Simplificaciones de multiplicación
            else if (c.operador.equals("*")) {
                // Cambiar x * 0 por 0 (o 0 * x)
                if ("0".equals(c.arg1) || "0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg1 = "0";
                    c.arg2 = null;
                } 
                // Eliminar multiplicaciones por 1
                else if ("1".equals(c.arg2)) {
                    // x * 1 -> x
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("1".equals(c.arg1)) {
                    // 1 * x -> x
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                } 
                // Cambiar x * 2 por x + x
                else if ("2".equals(c.arg2)) {
                    // x * 2 -> x + x
                    c.operador = "+";
                    c.arg2 = c.arg1;
                } else if ("2".equals(c.arg1)) {
                    // 2 * x -> x + x
                    c.operador = "+";
                    c.arg1 = c.arg2;
                }
            }
        }
    }
}