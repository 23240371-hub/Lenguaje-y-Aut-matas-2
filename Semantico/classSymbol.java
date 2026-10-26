public class Simbolo {

    private String nombre;      
    private String tipoDato;    
    private String ambito;          
    private Object valorInicial;    
    private int lineaDeclaracion;     
    private int direccionMemoria;     

    public Simbolo(String nombre, String tipoDato, String ambito, Object valorInicial, int lineaDeclaracion, int direccionMemoria) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.ambito = ambito;
        this.valorInicial = valorInicial;
        this.lineaDeclaracion = lineaDeclaracion;
        this.direccionMemoria = direccionMemoria;
    }

    public Simbolo(String nombre, String tipoDato, String ambito, Object valorInicial, int direccionMemoria) {
        this(nombre, tipoDato, ambito, valorInicial, -1, direccionMemoria);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public Object getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(Object valorInicial) {
        this.valorInicial = valorInicial;
    }

    public int getLineaDeclaracion() {
        return lineaDeclaracion;
    }

    public void setLineaDeclaracion(int lineaDeclaracion) {
        this.lineaDeclaracion = lineaDeclaracion;
    }

    public int getDireccionMemoria() {
        return direccionMemoria;
    }

    public void setDireccionMemoria(int direccionMemoria) {
        this.direccionMemoria = direccionMemoria;
    }

    public static int getTamanioEnBytes(String tipoDato) {
        switch (tipoDato.toLowerCase()) {
            case "int":
            case "float":
                return 4;
            case "double":
                return 8;
            case "boolean":
            case "byte":
                return 1;
            case "char":
            case "short":
                return 2;
            default:
                return 4; 
        }
    }

    @Override
    public String toString() {
        String lineaStr = (lineaDeclaracion != -1) ? String.valueOf(lineaDeclaracion) : "N/A";
        return String.format("Simbolo { Nombre: '%s', Tipo: '%s', Ámbito: '%s', Valor: %s, Línea: %s, Dir. Memoria: %d }",
                nombre, tipoDato, ambito, valorInicial, lineaStr, direccionMemoria);
    }
}
