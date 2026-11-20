package sintactico;

public class NodoOperacionUnaria extends NodoAST {
    private final String operador;
    private final NodoAST operando;

    public NodoOperacionUnaria(String operador, NodoAST operando, int linea) {
        super(linea);
        this.operador = operador;
        this.operando = operando;
    }

    public String getOperador() { return operador; }
    public NodoAST getOperando() { return operando; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ")
                + "OpUnaria (" + operador + ")");
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
        if (operando != null) {
            operando.imprimir(nuevoPrefijo, true);
        }
    }
}