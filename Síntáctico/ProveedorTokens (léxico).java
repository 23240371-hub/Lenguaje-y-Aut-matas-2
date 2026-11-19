/**
 * Interfaz del modelo bajo demanda:
 * Tanto el Lexer real como MockTokens implementan este contrato para que el Parser
 * solicite los tokens uno por uno mediante siguienteToken().
 */
interface ProveedorTokens {
    Token siguienteToken();
}
