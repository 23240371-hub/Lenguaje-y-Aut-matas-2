package sintactico;

public class NodoPrint extends NodoAST {
    private final NodoAST expresion;

    public NodoPrint(NodoAST expresion, int linea) {
        super(linea);
        this.expresion = expresion;
    }

    public NodoAST getExpresion() { return expresion; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + "Print [L" + linea + "]");
        if (expresion != null) {
            expresion.imprimir(prefijo + (esUltimo ? "    " : "│   "), true);
        }
    }
}