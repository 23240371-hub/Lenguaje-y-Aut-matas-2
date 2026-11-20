package sintactico;

import java.util.List;

public abstract class NodoAST {
    protected int linea;

    public NodoAST(int linea) {
        this.linea = linea;
    }

    public int getLinea() {
        return linea;
    }

    public abstract void imprimir(String prefijo, boolean esUltimo);

    protected static void imprimirListaSentencias(List<NodoAST> sentencias, String prefijo) {
        if (sentencias == null) return;
        for (int i = 0; i < sentencias.size(); i++) {
            boolean ultimo = (i == sentencias.size() - 1);
            sentencias.get(i).imprimir(prefijo, ultimo);
        }
    }
}