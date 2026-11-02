import java.util.ArrayList;
import java.util.List;

public class ManejadorErrores {

    public enum TipoError {
        LEXICO,
        SINTACTICO,
        SEMANTICO
    }

    public static class ErrorCompilador {
        private TipoError tipo;
        private int linea;
        private String descripcion;

        public ErrorCompilador(TipoError tipo, int linea, String descripcion) {
            this.tipo = tipo;
            this.linea = linea;
            this.descripcion = descripcion;
        }

        public TipoError getTipo() {
            return tipo;
        }

        public int getLinea() {
            return linea;
        }

        public String getDescripcion() {
            return descripcion;
        }

        @Override
        public String toString() {
            String lineaStr = (linea != -1) ? "Línea " + linea : "Línea desconocida";
            return String.format("[%s] %s: %s", tipo, lineaStr, descripcion);
        }
    }

    private List<ErrorCompilador> listaErrores;

    public ManejadorErrores() {
        this.listaErrores = new ArrayList<>();
    }

    public void agregarErrorSintactico(int linea, String mensaje) {
        listaErrores.add(new ErrorCompilador(TipoError.SINTACTICO, linea, mensaje));
    }

    public void agregarErrorLexico(int linea, String mensaje) {
        listaErrores.add(new ErrorCompilador(TipoError.LEXICO, linea, mensaje));
    }

    public void agregarErrorSemantico(int linea, String mensaje) {
        listaErrores.add(new ErrorCompilador(TipoError.SEMANTICO, linea, mensaje));
    }

    public void agregarErrorSemantico(String mensaje) {
        listaErrores.add(new ErrorCompilador(TipoError.SEMANTICO, -1, mensaje));
    }

    public boolean tieneErrores() {
        return !listaErrores.isEmpty();
    }

    public List<ErrorCompilador> getListaErrores() {
        return listaErrores;
    }

    public void limpiarErrores() {
        listaErrores.clear();
    }

    public void imprimirErrores() {
        if (listaErrores.isEmpty()) {
            System.out.println(">>> Compilación exitosa: No se encontraron errores.");
            return;
        }

        System.out.println("\n=== REPORTE GENERAL DE ERRORES DE COMPILACIÓN ===");
        for (ErrorCompilador err : listaErrores) {
            System.err.println(err);
        }
    }
}
