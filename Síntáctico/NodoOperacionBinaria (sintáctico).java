class NodoOperacionBinaria extends NodoAST {
    private final NodoAST izquierda;
    private final String operador;
    private final NodoAST derecha;

    public NodoOperacionBinaria(NodoAST izquierda, String operador, NodoAST derecha, int linea) {
        super(linea);
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    public NodoAST getIzquierda() { return izquierda; }
    public String getOperador() { return operador; }
    public NodoAST getDerecha() { return derecha; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ")
                + "OpBinaria (" + operador + ")");
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
        if (izquierda != null) izquierda.imprimir(nuevoPrefijo, false);
        if (derecha != null) derecha.imprimir(nuevoPrefijo, true);
    }
}
