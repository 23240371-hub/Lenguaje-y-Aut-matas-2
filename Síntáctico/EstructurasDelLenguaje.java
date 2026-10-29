import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class EstructurasDelLenguaje {
    private File archivo;
    
    private ArrayList<String> terminales;
    private ArrayList<String> noTerminales;

    private ArrayList<String> ladoIzquierdo;
    private ArrayList<String[]> ladoDerecho;

    public EstructurasDelLenguaje(String reglasGramatica) {
        archivo = new File(reglasGramatica);

        this.terminales = new ArrayList<>();
        this.noTerminales = new ArrayList<>();
        
        this.ladoDerecho = new ArrayList<>();
        this.ladoIzquierdo = new ArrayList<>();

        try (Scanner lector = new Scanner(archivo)) {
            while (lector.hasNextLine()) { 
                String linea = lector.nextLine().trim();
                if (linea.isEmpty() || linea.startsWith("//")) {
                    continue;
                }
                if (linea.contains("->")) {
                    String[] partes = linea.split("->");
                    String izquierdo = partes[0].trim();
                    String derecho = partes[1].trim();

                    ladoIzquierdo.add(izquierdo);

                    if (!noTerminales.contains(izquierdo)) {
                        noTerminales.add(izquierdo);
                    }

                    
                    String[] opciones = derecho.split("\\|");
                    for (int i = 0; i < opciones.length; i++) {
                        opciones[i] = opciones[i].trim();
                        
                        
                        String[] simbolos = opciones[i].split("\\s+");
                        for (String simbolo : simbolos) {
                            simbolo = simbolo.trim();
                            
                            
                            if (simbolo.equals("ε") || simbolo.isEmpty()) {
                                continue;
                            }
                            
                            
                            if (simbolo.startsWith("<") && simbolo.endsWith(">")) {
                                if (!noTerminales.contains(simbolo)) {
                                    noTerminales.add(simbolo);
                                }
                            } else {
                                
                                if (!terminales.contains(simbolo)) {
                                    terminales.add(simbolo);
                                }
                            }
                        }
                       
                    }

                    
                    ladoDerecho.add(opciones);
                }
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("no se encontro el archivo");
        }    
    }
    public ArrayList<String> getTerminales() { return terminales; }
    public ArrayList<String> getNoTerminales() { return noTerminales; }
    public ArrayList<String> getLadoIzquierdo() { return ladoIzquierdo; }
    public ArrayList<String[]> getLadoDerecho() { return ladoDerecho; }

}
