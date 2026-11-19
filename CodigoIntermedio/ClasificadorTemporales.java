package com.mycompany.compilador;

public class ClasificadorTemporales {
    //Metodo principal que recorre los cuadruplos y registra los temporales vivos
    public void clasificarYRegistrar(TablaSimbolos tabla) {
        //1. Recorremos la lista global de cuádruplos que ya se optimizo
        for (Cuadruplo c : Cuadruplo.listaCuadruplos) { 
            //Guardamos el campo resultado en una variable 
            String destino = c.resultado;
            
            //2. Verificamos si el resultado es un temporaly ignoramlos
            //variables del usuario (x, total) y etiquetas de salto (L1,L2)
            if (esTemporal(destino)) {
                //3. Evitamos duplicados usando el método existe()
                if (!tabla.existe(destino)) { //si el destino no existe en la tabla
                     //4. Deducimos que tipo de datos guardará este temporal
                    String tipoDato = determinarTipo(c, tabla);
                    //5. Insertamos el temporal en la tabla de simbolos
                    tabla.insertar(destino,     // t1 
                                    tipoDato,   // "int"
                                    null,       // se calcula el valor en ejecución
                                    0);         // variable interna declarada por el usuario
            }
        }
    }


    //Método auxiliar que identifica si un nombre corresponde a un temporal
    private boolean esTemporal(String nombre) {
        if (nombre == null) {
            return false;
        }
        return nombre.matches("t\\d+"); //valida que empiece con "t" y luego un numero
    }

    //Método auxiliar que deduce el tipo de dato del temporal según el cuádruplo
    private String determinarTipo(Cuadruplo c, TablaSimbolos tabla) { 
        String op = c.operador;
    
        //Caso A: Si es una comparacion lógica (If o While), es boolean
        if (op.equals("<") || op.equals(">") || op.equals("<=") ||
            op.equals(">=") || op.equals("==") || op.equals("!=")){
            return "boolean";
        }
    
        //Caso B: Si alguno de los operandos es decimal, el resultado será decimal
        if (esDecimal(c.arg1, tabla) || esDecimal(c.arg2, tabla)) {
            return "float";
        }
    
        //Caso C: En cualquier otro caso, es entero
        return "int";
    }

    //Método auxiliar 3: Revisa si un operando es decimal
    private boolean esDecimal(String operando, TablaSimbolos tabla) {
        if (operando == null) {
            return false;
        }
    
        //1. Si un número contiene un punto decimal
        if (operando.contains(".")) {
            return true;
        }
    
        //2. Si simbolo es distinto a nulo
        Simbolo simbolo = tabla.buscar(operando);
        if (simbolo != null) {
            String tipo = simbolo.getTipoDato();
            return "float".equals(tipo) || "double".equals(tipo);
        }
        return false;
    }
}