class NodoDeclaracion extends NodoAST {
    private final String tipoDato;
    private final String identificador;
    private final NodoAST expresionInicial; // null si es declaración sin inicializar (ej. int x;)

    public NodoDeclaracion(String tipoDato, String identificador, NodoAST expresionInicial, int linea) {
        super(linea);
        this.tipoDato = tipoDato;
        this.identificador = identificador;
        this.expresionInicial = expresionInicial;
    }

    public String getTipoDato() { return tipoDato; }
    public String getIdentificador() { return identificador; }
    public NodoAST getExpresionInicial() { return expresionInicial; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ")
                + "Declaracion (" + tipoDato + " " + identificador + ") [L" + linea + "]");
        if (expresionInicial != null) {
            expresionInicial.imprimir(prefijo + (esUltimo ? "    " : "│   "), true);
        }
    }
}
