package sintactico;

public class NodoIdentificador extends NodoAST {
    private final String nombre;

    public NodoIdentificador(String nombre, int linea) {
        super(linea);
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + "Id (" + nombre + ")");
    }
}