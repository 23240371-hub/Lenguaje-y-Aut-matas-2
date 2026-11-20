package sintactico;

import java.util.ArrayList;
import java.util.List;

public class NodoWhile extends NodoAST {
    private final NodoAST condicion;
    private final List<NodoAST> cuerpo;

    public NodoWhile(NodoAST condicion, List<NodoAST> cuerpo, int linea) {
        super(linea);
        this.condicion = condicion;
        this.cuerpo = (cuerpo != null) ? cuerpo : new ArrayList<>();
    }

    public NodoAST getCondicion() { return condicion; }
    public List<NodoAST> getCuerpo() { return cuerpo; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + "While [L" + linea + "]");
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");

        System.out.println(nuevoPrefijo + "├── Condicion:");
        if (condicion != null) {
            condicion.imprimir(nuevoPrefijo + "│   ", true);
        }

        System.out.println(nuevoPrefijo + "└── Cuerpo:");
        imprimirListaSentencias(cuerpo, nuevoPrefijo + "    ");
    }
}