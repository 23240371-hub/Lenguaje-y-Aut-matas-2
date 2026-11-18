public class GeneradorIntermedio {
  
    private int contadorTemporales = 1; //Contador para generar temporales como t1, t2...
    private int contadorEtiquetas = 1; //Contador para generar temporales como L1, L2...
    
    //Método que crea un nombre temporal único y aumenta el contador
    private String crearNuevoTemporal() {
        return "t" + (contadorTemporales++);
    }

    //Método que crea un nombre de etiqueta único
    private String crearNuevaEtiqueta() {
        return "L" + (contadorEtiquetas++);
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
        //Regla 4: Para nodo If (Sin Else)
        else if (nodo instanceof NodoAST.If nodoIf) {
            //1. Traduce la condición y recupera el temporal
            String condicionTemp = traducir(nodoIf.condicion);
          
            //2. Genera el nombre de la etiqueta de salida
            String lFin = crearNuevaEtiqueta();
          
            //3. Cuadruplo IF_FALSE: Si la condiciones falsa, salta a lFin
            Cuadruplo.generar("IF_FALSE", condicionTemp, null, lFin);
          
            //4. Traduce recursivamente todas las sentencias dentro del If
            for (NodoAST sentencia : nodoIf.bloqueEntonces) {
                traducir(sentencia);
            }
          
            //5. Cuadruplo Label: Se coloca la marca fisica para el destino del salto
            Cuadruplo.generar("LABEL", null, null, lFin);
            return "";
        }
        //Regla 5: Nodo While
        else if (nodo instanceof NodoAST.While nodoWhile) {
            // 1. Genera las dos etiquetas
            String lInicio = crearNuevaEtiqueta();
            String lFin = crearNuevaEtiqueta();
            
            // 2. Cuadruplo LABEL: Coloca la marca fisica de inicio que es donde rebotara
            Cuadruplo.generar("LABEL", null, null, lInicio);
            
            // 3. Traduce la condición
            String condicionTemp = traducir(nodoWhile.condicion);
            
            // 4. Cuádruplo IF_FALSE: Si la condición es falsa, rompe el ciclo saltando a lFin
            Cuadruplo.generar("IF_FALSE", condicionTemp, null, lFin);
            
            // 5. Traduce recursivamente el cuerpo del While
            for (NodoAST sentencia : nodoWhile.cuerpo) {
                traducir(sentencia);
            }
            
            // 6. Cuádruplo GOTO: Regresa al inicio para volver a evaluar
            Cuadruplo.generar("GOTO", null, null, lInicio);
            
            // 7. Cuádruplo LABEL: Coloca la marca física de fin del ciclo
            Cuadruplo.generar("LABEL", null, null, lFin);
            
            return "";
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
