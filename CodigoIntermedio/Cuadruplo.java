import java.util.ArrayList;
import java.util.List;

class Cuadruplo {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;
    //Lista estática
    public static List<Cuadruplo> listaCuadruplos = new ArrayList<>();

    public Cuadruplo(String operador, String arg1, String arg2, String resultado) {
        this.operador = operador;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = resultado;
    }
    //Método para generar y guardar automáticamente el cuádruplo
    public static void generar(String operador, String arg1, String arg2, String resultado){
        Cuadruplo nuevo = new Cuadruplo(operador, arg1, arg2, resultado);
        listaCuadruplos.add(nuevo);
    }
    //Método de impresión para leer directamente la lista 
    public static void imprimirTabla(){
        System.out.printf("%-6s | %-10s | %-10s | %-10s | %-10s%n", 
                          "Índice", "Operador", "Arg1", "Arg2", "Resultado");
        System.out.println("---------------------------------------------------------------");
        //ciclo que va a dar tantas vueltas como elementos existan guardados en listaCuadruplos
        for(int i = 0;i < listaCuadruplos.size(); i++){
            Cuadruplo c = listaCuadruplos.get(i);
            //Operador ternario
            String op = (c.operador != null && !c.operador.isEmpty()) ? c.operador : "-";
            String a1 = (c.arg1 != null && !c.arg1.isEmpty()) ? c.arg1 : "-";
            String a2 = (c.arg2 != null && !c.arg2.isEmpty()) ? c.arg2 : "-";
            String res = (c.resultado != null && !c.resultado.isEmpty()) ? c.resultado : "-";
            
            System.out.printf("%-6d | %-10s | %-10s | %-10s | %-10s%n", i, op, a1, a2, res);
        }
        System.out.println();
    }
}
