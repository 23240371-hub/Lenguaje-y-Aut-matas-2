import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// 1. Clase contenedora incluida para ejecución autónoma
class Cuadruplo {
    public String operador;
    public String arg1;
    public String arg2;
    public String resultado;

    public static List<Cuadruplo> listaCuadruplos = new ArrayList<>();

    public Cuadruplo(String operador, String arg1, String arg2, String resultado) {
        this.operador = operador;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.resultado = resultado;
    }

    public static void generar(String operador, String arg1, String arg2, String resultado){
        Cuadruplo nuevo = new Cuadruplo(operador, arg1, arg2, resultado);
        listaCuadruplos.add(nuevo);
    }
    
    public static void imprimirTabla(){
        System.out.printf("%-6s | %-10s | %-10s | %-10s | %-10s%n", 
                          "Índice", "Operador", "Arg1", "Arg2", "Resultado");
        System.out.println("---------------------------------------------------------------");
        
        for(int i = 0;i < listaCuadruplos.size(); i++){
            Cuadruplo c = listaCuadruplos.get(i);
            
            String op = (c.operador != null && !c.operador.isEmpty()) ? c.operador : "-";
            String a1 = (c.arg1 != null && !c.arg1.isEmpty()) ? c.arg1 : "-";
            String a2 = (c.arg2 != null && !c.arg2.isEmpty()) ? c.arg2 : "-";
            String res = (c.resultado != null && !c.resultado.isEmpty()) ? c.resultado : "-";
            
            System.out.printf("%-6d | %-10s | %-10s | %-10s | %-10s%n", i, op, a1, a2, res);
        }
        System.out.println();
    }
}
public class TACOptimizador {
    public boolean simplificacionAlgebraica(){
        boolean modificado = false;
        
        for(Cuadruplo c : Cuadruplo.listaCuadruplos){
            //Para SUMA
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) { //Si hay un 0 en el argumento 2
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;                
                } else if ("0".equals(c.arg1)){
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                    modificado = true;
                }  
            //Para RESTA
            } else if(c.operador.equals("-")){
                if("0".equals(c.arg2)){
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;
                }
            //Para MULTIPLICACION
            } else if(c.operador.equals("*")){
                if ("0".equals(c.arg1) || "0".equals(c.arg2)){
                    c.operador = "=";
                    c.arg1 = "0";
                    c.arg2 = null;
                    modificado = true;
                } else if("1".equals(c.arg2)){
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;
                } else if("1".equals(c.arg1)){
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                    modificado = true;
                } else if("2".equals(c.arg2)){
                    c.operador = "+";
                    c.arg2 = c.arg1;
                    modificado = true;
                } else if("2".equals(c.arg1)){
                    c.operador = "+";
                    c.arg1 = c.arg2;
                    modificado = true;
                }
            }
        }
        return modificado;
    }
    //Devuelve "true" automaticamente si borra un temporal inutil
    public boolean eliminacionCodigoMuerto(){
        Iterator<Cuadruplo> it = Cuadruplo.listaCuadruplos.iterator();
        while(it.hasNext()){
            Cuadruplo c = it.next();
            if(c.resultado != null && c.resultado.startsWith("t")){
                boolean usado = false;
                for(Cuadruplo revisado : Cuadruplo.listaCuadruplos){
                    if(c.resultado.equals(revisado.arg1) || c.resultado.equals(revisado.arg2)){
                        usado = true;
                        break;
                    }
                }
                if (!usado){
                    it.remove();
                    return true; //Borra algo, avisa para reiniciar la revision
                }
            }
            
        }
        return false; //Reviso todo y elimino la basura
    }
    
    public void optimizar(){  
        boolean huboCambios;
        do{
            boolean cambiosAlgebra = simplificacionAlgebraica();
            boolean cambiosMuertos = eliminacionCodigoMuerto();
            
            huboCambios = cambiosAlgebra || cambiosMuertos;
        }   while (huboCambios);
    }
}
