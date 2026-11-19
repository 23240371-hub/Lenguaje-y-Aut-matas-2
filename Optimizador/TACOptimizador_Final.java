import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Optimizador de Codigo Intermedio
public class TACOptimizador {

    // 1. Método orquestador que ejecuta todas las optimizaciones en cascada
    public void optimizar() {
        boolean huboCambios;
        do {
            // Ejecuta las 3 técnicas; si cualquiera modifica la lista, repite el ciclo
            boolean cambiosAlgebra = simplificacionAlgebraica();
            boolean cambiosCopias = propagacionCopias();
            boolean cambiosMuertos = eliminacionCodigoMuerto();

            huboCambios = cambiosAlgebra || cambiosCopias || cambiosMuertos;
        } while (huboCambios);
    }

    // =====================================================================
    // 2. SIMPLIFICACIÓN ALGEBRAICA (+0, -0, *0, *1, *2)
    // =====================================================================
    public boolean simplificacionAlgebraica() {
        boolean modificado = false;

        for (Cuadruplo c : Cuadruplo.listaCuadruplos) {
            if (c.operador == null) {
                continue;
            }

            // Sumas con 0: x + 0 -> x  |  0 + x -> x
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;
                } else if ("0".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                    modificado = true;
                }
            }
            // Restas con 0: x - 0 -> x (solo arg2 porque la resta no es conmutativa)
            else if (c.operador.equals("-")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;
                }
            }
            // Multiplicaciones por 0, 1 y 2
            else if (c.operador.equals("*")) {
                if ("0".equals(c.arg1) || "0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg1 = "0";
                    c.arg2 = null;
                    modificado = true;
                } else if ("1".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                    modificado = true;
                } else if ("1".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                    modificado = true;
                } else if ("2".equals(c.arg2)) {
                    c.operador = "+";
                    c.arg2 = c.arg1;
                    modificado = true;
                } else if ("2".equals(c.arg1)) {
                    c.operador = "+";
                    c.arg1 = c.arg2;
                    modificado = true;
                }
            }
        }
        return modificado;
    }

    // =====================================================================
    // 3. PROPAGACIÓN DE COPIAS (Sustituye temporales puente por su valor)
    // =====================================================================
    public boolean propagacionCopias() {
        boolean modificado = false;
        // Guarda asignaciones simples activas (ej. "t1" -> "x" o "t1" -> "20")
        Map<String, String> copias = new HashMap<>();

        for (Cuadruplo c : Cuadruplo.listaCuadruplos) {
            if (c.operador == null) {
                continue;
            }

            // Blindaje para ciclos y saltos: al cruzar una etiqueta o salto,
            // limpiamos el mapa para no propagar valores viejos entre iteraciones
            if (c.operador.equals("LABEL") || c.operador.equals("GOTO")) {
                copias.clear();
                continue;
            }

            // Reemplazamos arg1 y arg2 si apuntan a una copia registrada
            if (c.arg1 != null && copias.containsKey(c.arg1)) {
                String reemplazo = copias.get(c.arg1);
                if (!reemplazo.equals(c.arg1)) {
                    c.arg1 = reemplazo;
                    modificado = true;
                }
            }
            if (c.arg2 != null && copias.containsKey(c.arg2)) {
                String reemplazo = copias.get(c.arg2);
                if (!reemplazo.equals(c.arg2)) {
                    c.arg2 = reemplazo;
                    modificado = true;
                }
            }

            // Si era un salto condicional, ya usamos su condición; ahora limpiamos el bloque
            if (c.operador.equals("IF_FALSE")) {
                copias.clear();
                continue;
            }

            // Si una variable cambia de valor, invalidamos cualquier copia que dependiera de ella
            if (c.resultado != null) {
                copias.remove(c.resultado);
                copias.values().removeIf(val -> val.equals(c.resultado));
            }

            // Si el operador es igual a =, el segundo argumento es nulo, el resultado es diferente de nulo y el primer argumento es diferente de nulo
            if (c.operador.equals("=") && c.arg2 == null && c.resultado != null && c.arg1 != null) {
                if (!c.resultado.equals(c.arg1)) { //si el resultado de c no es igual al primer argumento de c
                    copias.put(c.resultado, c.arg1); // guarda en copias el resultado que tiene el primer argumento 
                }
            }
        }
        return modificado;
    }
    // 4. Eliminacion de codigo muerto
    //Devuelve "true" automaticamente si borra un temporal inutil
    public boolean eliminacionCodigoMuerto() {
        Iterator<Cuadruplo> it = Cuadruplo.listaCuadruplos.iterator();

        while (it.hasNext()) {
            Cuadruplo c = it.next();

            // Solo evaluamos temporales reales (t1, t2...) 
            //protegiendo variables como "total" y etiquetas "L1"
            if (esTemporal(c.resultado)) {
                boolean usado = false;

                // Revisamos si este temporal se lee como arg1 o arg2 en algún cuádruplo
                for (Cuadruplo revisado : Cuadruplo.listaCuadruplos) {
                    if (c.resultado.equals(revisado.arg1) || c.resultado.equals(revisado.arg2)) {
                        usado = true;
                        break;
                    }
                }

                // Si nadie lo usa, eliminamos la instrucción y avisamos para reiniciar la cascada
                if (!usado) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    // 5. Clasificacion de temporales en la Tabla de simbolos
    // Se llama DESPUÉS de optimizar(), para reservar memoria en ensamblador solo a los vivos
    public void clasificarTemporales(TablaSimbolos tabla) {
        for (Cuadruplo c : Cuadruplo.listaCuadruplos) {
            String destino = c.resultado;

            // Si es un temporal vivo y todavía no está registrado en la tabla
            if (esTemporal(destino) && !tabla.existe(destino)) {
                String tipoDato = determinarTipo(c, tabla);
                // Insertamos en TablaSimbolos (asigna tamaño en bytes y offset automáticamente)
                tabla.insertar(destino, tipoDato, null, 0);
            }
        }
    }

    // Metodos auxiliares compartidos
    // Valida formato exacto de temporal: "t" seguida únicamente de números (t1, t2, t10...)
    private boolean esTemporal(String nombre) {
        return nombre != null && nombre.matches("t\\d+");
    }

    // Deduce el tipo de dato del temporal según el operador y los operandos
    private String determinarTipo(Cuadruplo c, TablaSimbolos tabla) {
        String op = c.operador;

        // Comparaciones lógicas que regresan BOOLEAN
        if (op.equals("<") || op.equals(">") || op.equals("<=") ||
            op.equals(">=") || op.equals("==") || op.equals("!=")) {
            return "boolean";
        }

        // Si algún operando es decimal, el resultado es FLOAT
        if (esDecimal(c.arg1, tabla) || esDecimal(c.arg2, tabla)) {
            return "float";
        }

        // Por defecto es entero
        return "int";
    }

    // Comprueba si un operando es literal decimal (3.14) o una variable/temporal float en la tabla
    private boolean esDecimal(String operando, TablaSimbolos tabla) {
        if (operando == null) {   //si operando es nulo
            return false;         // devuelve falso
        }
        if (operando.contains(".")) {   // si operando contiene "."
            return true;                //devuelve true 
        }
        Simbolo simbolo = tabla.buscar(operando);
        if (simbolo != null) { //si simbolo es diferente de nulo
            String tipo = simbolo.getTipoDato(); //obten su tipo de dato
            return "float".equals(tipo) || "double".equals(tipo); //devuelve true si es float o false si es double
        }
        return false;
    }
}
