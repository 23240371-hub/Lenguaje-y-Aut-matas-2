public class Lexer {

    private LectorArchivo lector;

    public Lexer(LectorArchivo lector) {
        this.lector = lector;
    }

    /**
     * Consume y descarta espacios en blanco, tabulaciones, saltos de línea
     * y comentarios de una línea (//) o multilínea (/* ... */).
     */
    public void descartarEspaciosYComentarios() {
        while (!lector.finDeArchivo()) {
            char actual = lector.mirar();

            // 1. Descartar espacios, tabuladores y retornos
            if (actual == ' ' || actual == '\t' || actual == '\r' || actual == '\n') {
                lector.avanzar();
            }
            // 2. Posible inicio de comentario: empieza con '/'
            else if (actual == '/') {
                char siguiente = lector.mirarSiguiente();

                // Comentario de una sola línea: //
                if (siguiente == '/') {
                    lector.avanzar(); // consume el primer '/'
                    lector.avanzar(); // consume el segundo '/'
                    
                    // Consume todo hasta encontrar el salto de línea o fin de archivo
                    while (!lector.finDeArchivo() && lector.mirar() != '\n') {
                        lector.avanzar();
                    }
                }
                // Comentario multilínea: /* ... */
                else if (siguiente == '*') {
                    lector.avanzar(); // consume el '/'
                    lector.avanzar(); // consume el '*'

                    // Consume todo hasta encontrar la combinación "*/"
                    while (!lector.finDeArchivo()) {
                        if (lector.mirar() == '*' && lector.mirarSiguiente() == '/') {
                            lector.avanzar(); // consume '*'
                            lector.avanzar(); // consume '/'
                            break;            // fin del comentario multilínea
                        }
                        lector.avanzar();
                    }
                }
                // Si solo era una barra '/' (operador de división), no se descarta
                else {
                    break;
                }
            }
            // Si es cualquier otro carácter (letras, números, delimitadores), nos detenemos
            else {
                break;
            }
        }
    }
}
