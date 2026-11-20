package sintactico;

import java.util.ArrayList;
import java.util.List;

public class NodoPrograma extends NodoAST {
    private final String nombreClase; // null si no tiene envoltorio class/main
    private final List<NodoAST> sentencias;

    public NodoPrograma(String nombreClase, List<NodoAST> sentencias, int linea) {
        super(linea);
        this.nombreClase = nombreClase;
        this.sentencias = (sentencias != null) ? sentencias : new ArrayList<>();
    }

    public String getNombreClase() { return nombreClase; }
    public List<NodoAST> getSentencias() { return sentencias; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        String titulo = (nombreClase != null)
                ? "Programa (class " + nombreClase + " -> main)"
                : "Programa";
        System.out.println(titulo);
        imprimirListaSentencias(sentencias, "");
    }
}