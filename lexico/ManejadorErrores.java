import java.util.ArrayList;
import java.util.List;

public class ManejadorErrores {

    // Lista simple para almacenar los mensajes de error encontrados
    private List<String> errores;

    public ManejadorErrores() {
        this.errores = new ArrayList<>();
    }

    // Registra un nuevo error con su fila y columna
    public void agregarError(int fila, int columna, String mensaje) {
        String error = "Error [Fila " + fila + ", Col " + columna + "]: " + mensaje;
        errores.add(error);
    }

    // Indica si hubo al menos un error
    public boolean hayErrores() {
        return !errores.isEmpty();
    }

    // Devuelve cuántos errores se detectaron
    public int totalErrores() {
        return errores.size();
    }

    // Imprime todos los errores en consola
    public void imprimirErrores() {
        if (errores.isEmpty()) {
            System.out.println("No se encontraron errores lexicos.");
            return;
        }

        System.out.println("\n--- LISTA DE ERRORES (" + errores.size() + ") ---");
        for (String err : errores) {
            System.out.println(err);
        }
    }

    // Permite vaciar la lista si quieres reiniciar el análisis
    public void limpiar() {
        errores.clear();
    }
}
