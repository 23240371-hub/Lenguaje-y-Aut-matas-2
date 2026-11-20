package sintactico;

import java.util.ArrayList;
import java.util.List;

public class NodoIf extends NodoAST {
    private final NodoAST condicion;
    private final List<NodoAST> bloqueThen;
    private final List<NodoAST> bloqueElse;

    public NodoIf(NodoAST condicion, List<NodoAST> bloqueThen, List<NodoAST> bloqueElse, int linea) {
        super(linea);
        this.condicion = condicion;
        this.bloqueThen = (bloqueThen != null) ? bloqueThen : new ArrayList<>();
        this.bloqueElse = bloqueElse;
    }

    public NodoAST getCondicion() { return condicion; }
    public List<NodoAST> getBloqueThen() { return bloqueThen; }
    public List<NodoAST> getBloqueElse() { return bloqueElse; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + "If [L" + linea + "]");
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
        boolean tieneElse = (bloqueElse != null && !bloqueElse.isEmpty());

        System.out.println(nuevoPrefijo + "├── Condicion:");
        if (condicion != null) {
            condicion.imprimir(nuevoPrefijo + "│   ", true);
        }

        System.out.println(nuevoPrefijo + (tieneElse ? "├── " : "└── ") + "Then:");
        imprimirListaSentencias(bloqueThen, nuevoPrefijo + (tieneElse ? "│   " : "    "));

        if (tieneElse) {
            System.out.println(nuevoPrefijo + "└── Else:");
            imprimirListaSentencias(bloqueElse, nuevoPrefijo + "    ");
        }
    }
}