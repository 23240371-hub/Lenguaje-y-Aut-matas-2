import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorArchivo {

    private String contenido; // Guarda todo el texto del archivo
    private int indice;      // Puntero de caracteres (0, 1, 2...)
    private int fila;        // Número de línea actual
    private int columna;     // Número de columna actual

    //recibe la ruta del archivo y lo lee
    public LectorArchivo(String rutaArchivo) {
        this.contenido = leerArchivo(rutaArchivo);
        this.indice = 0;
        this.fila = 1;
        this.columna = 1;
    }

    // Método que abre y carga el .txt en una sola cadena
    private String leerArchivo(String ruta) {
        StringBuilder texto = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                texto.append(linea).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return texto.toString();
    }

    // Lee el carácter actual y avanza el puntero, la fila y la columna
    public char avanzar() {
        if (indice >= contenido.length()) {
            return '\0'; // Carácter nulo si llegó al final del archivo (EOF)
        }

        char actual = contenido.charAt(indice);
        indice++;

        // Si es salto de línea, pasamos a la siguiente fila y reiniciamos columna
        if (actual == '\n') {
            fila++;
            columna = 1;
        } else {
            columna++;
        }

        return actual;
    }

    // Mira el carácter actual SIN mover el puntero
    public char mirar() {
        if (indice >= contenido.length()) {
            return '\0';
        }
        return contenido.charAt(indice);
    }

    // Indica si ya se leyó todo el archivo
    public boolean finDeArchivo() {
        return indice >= contenido.length();
    }

    // Getters 
    public int getIndice() {
        return indice;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
