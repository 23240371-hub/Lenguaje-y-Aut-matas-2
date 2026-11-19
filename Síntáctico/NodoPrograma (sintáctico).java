import java.util.ArrayList;
import java.util.List;

class NodoPrograma extends NodoAST {
    private final List<NodoAST> sentencias;

    public NodoPrograma(List<NodoAST> sentencias) {
        super(1);
        this.sentencias = (sentencias != null) ? sentencias : new ArrayList<>();
    }

    public List<NodoAST> getSentencias() {
        return sentencias;
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println("Programa");
        imprimirListaSentencias(sentencias, "");
    }
}
