import java.util.ArrayList;
import java.util.List;

class ManejadorErrores {
    private final List<String> errores = new ArrayList<>();

    public void agregarErrorSintactico(int linea, String mensaje) {
        errores.add("[Error Sintáctico - Línea " + linea + "] " + mensaje);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public void imprimirErrores() {
        if (errores.isEmpty()) {
            System.out.println("Sin errores.");
            return;
        }
        System.out.println("Se encontraron " + errores.size() + " error(es):");
        for (String err : errores) {
            System.out.println("  - " + err);
        }
    }
}