package sintactico;

public class NodoLiteral extends NodoAST {
    private final Object valor;
    private final String lexema;
    private final String tipoDato;

    public NodoLiteral(Object valor, String lexema, String tipoDato, int linea) {
        super(linea);
        this.valor = valor;
        this.lexema = lexema;
        this.tipoDato = tipoDato;
    }

    public Object getValor() { return valor; }
    public String getLexema() { return lexema; }
    public String getTipoDato() { return tipoDato; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ")
                + "Literal (" + lexema + " : " + tipoDato + ")");
    }
}