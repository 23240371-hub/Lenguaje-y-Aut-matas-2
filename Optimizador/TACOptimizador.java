package com.mycompany.compilador;

import java.util.Iterator;
import java.util.List;

public class TACOptimizador {

    public void simplificacionAlgebraica(List<Cuadruplo> lista) {
        for (Cuadruplo c : lista) {
            // 1. Elimina sumas con 0
            if (c.operador.equals("+")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("0".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                }
            } 
            // 2. Elimina restas con 0 (Solo el arg2)
            else if (c.operador.equals("-")) {
                if ("0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                }
            }
            // 3. Simplificaciones de multiplicación
            else if (c.operador.equals("*")) {
                if ("0".equals(c.arg1) || "0".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg1 = "0";
                    c.arg2 = null;
                } else if ("1".equals(c.arg2)) {
                    c.operador = "=";
                    c.arg2 = null;
                } else if ("1".equals(c.arg1)) {
                    c.operador = "=";
                    c.arg1 = c.arg2;
                    c.arg2 = null;
                } else if ("2".equals(c.arg2)) {
                    c.operador = "+";
                    c.arg2 = c.arg1;
                } else if ("2".equals(c.arg1)) {
                    c.operador = "+";
                    c.arg1 = c.arg2;
                }
            }
        }
    }

    public void eliminacionCodigoMuerto(List<Cuadruplo> lista) {
        boolean huboCambios;
        do {
            huboCambios = false;
            Iterator<Cuadruplo> it = lista.iterator();
            while (it.hasNext()) {
                Cuadruplo c = it.next();
                if (c.resultado != null && c.resultado.startsWith("t")) {
                    boolean usado = false;
                    for (Cuadruplo revisado : lista) {
                        if (c.resultado.equals(revisado.arg1) || c.resultado.equals(revisado.arg2)) {
                            usado = true;
                            break;
                        }
                    }
                    if (!usado) {
                        it.remove();
                        huboCambios = true;
                        break; 
                    }
                }
            }
        } while (huboCambios); 
    }

    // Método central que invoca las pasadas repetidamente hasta que no haya cambios
    public void optimizar(List<Cuadruplo> codigoIntermedio) {
        boolean huboCambios;
        do {
            String estadoAnterior = generarFirma(codigoIntermedio);

            simplificacionAlgebraica(codigoIntermedio);
            eliminacionCodigoMuerto(codigoIntermedio);

            String estadoActual = generarFirma(codigoIntermedio);
            huboCambios = !estadoAnterior.equals(estadoActual);

        } while (huboCambios); 
    }

    private String generarFirma(List<Cuadruplo> lista) {
        StringBuilder sb = new StringBuilder();
        for (Cuadruplo c : lista) {
            sb.append(c.operador).append(c.arg1).append(c.arg2).append(c.resultado);
        }
        return sb.toString();
    }
}
