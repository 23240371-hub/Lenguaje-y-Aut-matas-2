import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class GeneradorMatrizPredictiva {
    private EstructurasDelLenguaje estructura;
    private String simboloInicial;
    
    // Conjuntos FIRST y FOLLOW
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    
    // Matriz de Predicción: [NoTerminal][Terminal] -> Regla de producción resultante
    private Map<String, Map<String, String>> matriz;

    public GeneradorMatrizPredictiva(EstructurasDelLenguaje estructura) {
        this.estructura = estructura;
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.matriz = new HashMap<>();
        
        // El primer No Terminal leído se asume como el símbolo inicial de la gramática
        if (!estructura.getLadoIzquierdo().isEmpty()) {
            this.simboloInicial = estructura.getLadoIzquierdo().get(0);
        }

        inicializarEstructuras();
        calcularFirst();
        calcularFollow();
        generarMatriz();
    }

    private void inicializarEstructuras() {
        for (String nt : estructura.getNoTerminales()) {
            first.put(nt, new HashSet<>());
            follow.put(nt, new HashSet<>());
            matriz.put(nt, new HashMap<>());
        }
    }

    private void calcularFirst() {
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            
            // Recorrer cada definición de regla de la gramática original
            for (int i = 0; i < estructura.getLadoIzquierdo().size(); i++) {
                String nt = estructura.getLadoIzquierdo().get(i);
                Set<String> firstNT = first.get(nt);
                int tamInicial = firstNT.size();

                String[] producciones = estructura.getLadoDerecho().get(i);
                for (String prod : producciones) {
                    String[] simbolos = prod.split("\\s+");
                    boolean esVacio = true;

                    for (String simbolo : simbolos) {
                        simbolo = simbolo.trim();
                        if (simbolo.isEmpty()) continue;

                        if (simbolo.equals("ε")) {
                            firstNT.add("ε");
                            break;
                        } else if (!simbolo.startsWith("<") || !simbolo.endsWith(">")) { // Terminal
                            firstNT.add(simbolo);
                            esVacio = false;
                            break;
                        } else { // No Terminal
                            Set<String> firstSimb = first.get(simbolo);
                            for (String f : firstSimb) {
                                if (!f.equals("ε")) firstNT.add(f);
                            }
                            if (!firstSimb.contains("ε")) {
                                esVacio = false;
                                break;
                            }
                        }
                    }
                    if (esVacio) firstNT.add("ε");
                }
                if (firstNT.size() > tamInicial) cambio = true;
            }
        }
    }

    private void calcularFollow() {
        if (simboloInicial != null) {
            follow.get(simboloInicial).add("$"); // Fin de archivo
        }

        boolean cambio = true;
        while (cambio) {
            cambio = false;

            for (int i = 0; i < estructura.getLadoIzquierdo().size(); i++) {
                String nt = estructura.getLadoIzquierdo().get(i);
                String[] producciones = estructura.getLadoDerecho().get(i);

                for (String prod : producciones) {
                    String[] simbolos = prod.split("\\s+");

                    for (int j = 0; j < simbolos.length; j++) {
                        String B = simbolos[j].trim();
                        if (B.startsWith("<") && B.endsWith(">")) { // Si B es No Terminal
                            Set<String> followB = follow.get(B);
                            int tamInicial = followB.size();
                            boolean esVacio = true;

                            for (int k = j + 1; k < simbolos.length; k++) {
                                String beta = simbolos[k].trim();
                                if (beta.isEmpty()) continue;

                                if (!beta.startsWith("<") || !beta.endsWith(">")) { // Terminal
                                    followB.add(beta);
                                    esVacio = false;
                                    break;
                                } else { // No Terminal
                                    Set<String> firstBeta = first.get(beta);
                                    for (String f : firstBeta) {
                                        if (!f.equals("ε")) followB.add(f);
                                    }
                                    if (!firstBeta.contains("ε")) {
                                        esVacio = false;
                                        break;
                                    }
                                }
                            }

                            if (esVacio) {
                                followB.addAll(follow.get(nt));
                            }

                            if (followB.size() > tamInicial) cambio = true;
                        }
                    }
                }
            }
        }
    }

    private void generarMatriz() {
        for (int i = 0; i < estructura.getLadoIzquierdo().size(); i++) {
            String nt = estructura.getLadoIzquierdo().get(i);
            String[] producciones = estructura.getLadoDerecho().get(i);

            for (String prod : producciones) {
                String[] simbolos = prod.split("\\s+");
                Set<String> firstProd = obtenerFirstDeCadena(simbolos);
                String produccionCompleta = nt + " -> " + prod;

                for (String t : firstProd) {
                    if (!t.equals("ε")) {
                        matriz.get(nt).put(t, produccionCompleta);
                    }
                }

                if (firstProd.contains("ε")) {
                    for (String t : follow.get(nt)) {
                        matriz.get(nt).put(t, produccionCompleta);
                    }
                }
            }
        }
    }

    private Set<String> obtenerFirstDeCadena(String[] simbolos) {
        Set<String> resultado = new HashSet<>();
        boolean esVacio = true;

        for (String simbolo : simbolos) {
            simbolo = simbolo.trim();
            if (simbolo.isEmpty()) continue;

            if (simbolo.equals("ε")) {
                resultado.add("ε");
                break;
            } else if (!simbolo.startsWith("<") || !simbolo.endsWith(">")) {
                resultado.add(simbolo);
                esVacio = false;
                break;
            } else {
                Set<String> firstSimb = first.get(simbolo);
                for (String f : firstSimb) {
                    if (!f.equals("ε")) resultado.add(f);
                }
                if (!firstSimb.contains("ε")) {
                    esVacio = false;
                    break;
                }
            }
        }
        if (esVacio) resultado.add("ε");
        return resultado;
    }

    public void mostrarMatriz() {
        System.out.println("          MATRIZ PREDICTIVA           ");
        
        for (String nt : matriz.keySet()) {
            Map<String, String> celdas = matriz.get(nt);
            for (String t : celdas.keySet()) {
                System.out.printf("M[%-15s , %-10s] => %s\n", nt, t, celdas.get(t));
            }
        }
    }
}
