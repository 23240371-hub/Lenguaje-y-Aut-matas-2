public class GeneradorIntermedio {
    
    //Contador para generar temporales como t1, t2...
    private int contadorTemporales = 1;
    
    //Método que crea un nombre temporal único y aumenta el contador
    private String crearNuevoTemporal() {
        return "t" + (contadorTemporales++);
    }
    
    //Función principal que ahce el recorrido en "Postorden"
    public String traducir(NodoAST nodo){
        if (nodo == null) return "";
        
        //Regla 1: Traducción de OperacionBinaria
        if (nodo instanceof NodoAST.OperacionBinaria op){
            
            //1. PostOrden: Da una llamada recursiva al hijo izquierdo
            String izq = traducir(op.izquierda);
            
            //2. PostOrden: Da una llamada recursiva al hijo derecho
            String der = traducir(op.derecha);
            
            //3. Crea una variable temporal 
            String temporal = crearNuevoTemporal();
            
            //4. Genera el Cuádruplo conectando con la clase Cuadruplo
            Cuadruplo.generar(op.operador, izq, der, temporal);
            
            //5. Retorna el temporal para que los nodos de arriba lo usen
            return temporal;
        }
        
        //Regla 2: Traduccion de asignacioones (ejemplo: x = 5)
        else if (nodo instanceof NodoAST.Asignacion asig) {
            
            //1. PostOrden: Llama al hijo derecho, ya sea la expresion o valor a asignar)
            String resultadoExpresion = traducir(asig.expresion);
            
            //2. Genera el Cuádruplo de asignacion, el arg2 va nulo y el resultado es la variable
            Cuadruplo.generar("=", resultadoExpresion, null, asig.nombreVariable);
            
            return asig.nombreVariable;
        }
        
        //Regla 3: Traduccion de literales (numeros y Variables
        //Si se llega a la hoja del arbol, simplemente devolvemos su valor
        else if(nodo instanceof NodoAST.Literal lit) {
            return lit.valor.toString(); //aqui retorna el numero
        }
        else if (nodo instanceof NodoAST.Variable var) {
            return var.nombre; //aqui retorna el nombre
        }
        
        //Regla que le pasa todo al programa y recorre todas las sentencias
        else if (nodo instanceof NodoAST.Programa prog) {
            for (NodoAST sentencia : prog.sentencias) {
                traducir(sentencia);
            }
        }
        return "";
    }
}
