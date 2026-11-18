public class ValidadorTipos {

    private ManejadorErrores manejadorErrores;
    public static final String TIPO_ERROR = "ERROR";

    public ValidadorTipos(ManejadorErrores manejadorErrores) {
        this.manejadorErrores = manejadorErrores;
    }

    public String inferirTipoBinario(Simbolo izq, String operador, Simbolo der, int linea) {
        if (izq == null || der == null) {
            return TIPO_ERROR;
        }
        return inferirTipoBinario(izq.getTipoDato(), operador, der.getTipoDato(), linea);
    }

    public String inferirTipoBinario(String tipoIzq, String operador, String tipoDer, int linea) {
        if (TIPO_ERROR.equals(tipoIzq) || TIPO_ERROR.equals(tipoDer) || tipoIzq == null || tipoDer == null) {
            return TIPO_ERROR;
        }

        String t1 = tipoIzq.toLowerCase().trim();
        String t2 = tipoDer.toLowerCase().trim();
        String op = normalizarOperador(operador);

        switch (op) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                if (esNumerico(t1) && esNumerico(t2)) {
                    return promoverTipoNumerico(t1, t2);
                }
                reportarError(linea, "Operación aritmética '" + op + "' inválida entre '" + t1 + "' y '" + t2 + "'.");
                return TIPO_ERROR;

            case "<":
            case ">":
            case "<=":
            case ">=":
                if (esNumerico(t1) && esNumerico(t2)) {
                    return "boolean";
                }
                reportarError(linea, "No se puede comparar '" + t1 + "' " + op + " '" + t2 + "'. Se requieren tipos numéricos.");
                return TIPO_ERROR;

            case "==":
            case "!=":
                if ((esNumerico(t1) && esNumerico(t2)) || (esBooleano(t1) && esBooleano(t2)) || t1.equals(t2)) {
                    return "boolean";
                }
                reportarError(linea, "Comparación de igualdad '" + op + "' incompatible entre '" + t1 + "' y '" + t2 + "'.");
                return TIPO_ERROR;

            case "&&":
            case "||":
                if (esBooleano(t1) && esBooleano(t2)) {
                    return "boolean";
                }
                reportarError(linea, "Operador lógico '" + op + "' requiere operandos booleanos, se recibió '" + t1 + "' y '" + t2 + "'.");
                return TIPO_ERROR;

            default:
                reportarError(linea, "Operador desconocido en análisis semántico: '" + operador + "'.");
                return TIPO_ERROR;
        }
    }

    public boolean validarAsignacion(Simbolo variableDestino, String tipoExpresion, int linea) {
        if (variableDestino == null) {
            return false;
        }
        return validarAsignacion(variableDestino.getTipoDato(), tipoExpresion, linea);
    }

    public boolean validarAsignacion(String tipoDestino, String tipoExpresion, int linea) {
        if (TIPO_ERROR.equals(tipoExpresion) || tipoDestino == null || tipoExpresion == null) {
            return false;
        }

        String dest = tipoDestino.toLowerCase().trim();
        String expr = tipoExpresion.toLowerCase().trim();

        if (dest.equals(expr) || (esBooleano(dest) && esBooleano(expr))) {
            return true;
        }

        if (dest.equals("double") && (expr.equals("float") || expr.equals("int") || expr.equals("short") || expr.equals("byte"))) {
            return true;
        }
        if (dest.equals("float") && (expr.equals("int") || expr.equals("short") || expr.equals("byte"))) {
            return true;
        }
        if (dest.equals("int") && (expr.equals("short") || expr.equals("byte"))) {
            return true;
        }

        reportarError(linea, "Incompatibilidad de tipos en asignación: no se puede convertir '" + expr + "' a '" + dest + "'.");
        return false;
    }

    public boolean validarCondicion(String tipoCondicion, String estructura, int linea) {
        if (TIPO_ERROR.equals(tipoCondicion) || tipoCondicion == null) {
            return false;
        }
        if (!esBooleano(tipoCondicion.toLowerCase().trim())) {
            reportarError(linea, "La condición dentro de '" + estructura + "(...)' debe devolver obligatoriamente un tipo 'bool', pero se encontró '" + tipoCondicion + "'.");
            return false;
        }
        return true;
    }

    public boolean validarCondicion(String tipoCondicion, int linea) {
        return validarCondicion(tipoCondicion, "if/while", linea);
    }

    public String evaluarOperacionVariables(TablaSimbolos tabla, String nombreIzq, String operador, String nombreDer, int linea) {
        Simbolo sIzq = tabla.buscar(nombreIzq);
        Simbolo sDer = tabla.buscar(nombreDer);

        if (sIzq == null) {
            reportarError(linea, "La variable '" + nombreIzq + "' no ha sido declarada.");
            return TIPO_ERROR;
        }
        if (sDer == null) {
            reportarError(linea, "La variable '" + nombreDer + "' no ha sido declarada.");
            return TIPO_ERROR;
        }

        return inferirTipoBinario(sIzq, operador, sDer, linea);
    }

    private boolean esBooleano(String tipo) {
        return tipo.equals("boolean") || tipo.equals("bool");
    }

    private boolean esNumerico(String tipo) {
        return tipo.equals("int") || tipo.equals("float") || tipo.equals("double")
                || tipo.equals("short") || tipo.equals("byte");
    }

    private String promoverTipoNumerico(String t1, String t2) {
        if (t1.equals("double") || t2.equals("double")) {
            return "double";
        }
        if (t1.equals("float") || t2.equals("float")) {
            return "float";
        }
        return "int";
    }

    private String normalizarOperador(String op) {
        if (op == null) return "";
        switch (op) {
            case "OPERADOR_SUMA": return "+";
            case "OPERADOR_RESTA": return "-";
            case "OPERADOR_MULTIPLICACION": return "*";
            case "OPERADOR_DIVISION": return "/";
            case "OPERADOR_MODULO": return "%";
            case "OPERADOR_MENORQUE": return "<";
            case "OPERADOR_MAYORQUE": return ">";
            case "OPERADOR_MENORIGUAL": return "<=";
            case "OPERADOR_MAYORIGUAL": return ">=";
            case "OPERADOR_IGUALA": return "==";
            case "OPERADOR_DIFERENTEA": return "!=";
            case "OPERADOR_AND": return "&&";
            case "OPERADOR_OR": return "||";
            default: return op.trim();
        }
    }

    private void reportarError(int linea, String mensaje) {
        if (manejadorErrores != null) {
            manejadorErrores.agregarErrorSemantico(linea, mensaje);
        }
    }
}
