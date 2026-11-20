package sintactico;

public class NodoAsignacion extends NodoAST {
    private final String identificador;
    private final NodoAST expresion;

    public NodoAsignacion(String identificador, NodoAST expresion, int linea) {
        super(linea);
        this.identificador = identificador;
        this.expresion = expresion;
    }

    public String getIdentificador() { return identificador; }
    public NodoAST getExpresion() { return expresion; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ")
                + "Asignacion (" + identificador + " =) [L" + linea + "]");
        if (expresion != null) {
            expresion.imprimir(prefijo + (esUltimo ? "    " : "│   "), true);
        }
    }
}