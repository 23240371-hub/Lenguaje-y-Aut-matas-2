enum TipoToken {
    // Palabras reservadas de tipos de dato (compatibles con Simbolo.getTamanioEnBytes)
    INT, FLOAT, DOUBLE, BOOLEAN, CHAR, BYTE, SHORT,

    // Palabras reservadas de estructura, control y salida
    CLASS, MAIN, IF, ELSE, WHILE, PRINT,

    // Literales e identificadores
    IDENTIFICADOR,
    LITERAL_ENTERO,
    LITERAL_DECIMAL,
    LITERAL_BOOLEANO,
    LITERAL_CARACTER,
    LITERAL_CADENA,

    // Operadores aritméticos
    SUMA,           // +
    RESTA,          // -
    MULTIPLICACION, // *
    DIVISION,       // /
    MODULO,         // %

    // Operadores relacionales y asignación
    ASIGNACION,     // =
    IGUAL_IGUAL,    // ==
    DIFERENTE,      // !=
    MENOR,          // <
    MENOR_IGUAL,    // <=
    MAYOR,          // >
    MAYOR_IGUAL,    // >=

    // Operadores lógicos
    AND,            // &&
    OR,             // ||
    NOT,            // !

    // Delimitadores
    PUNTO_Y_COMA,   // ;
    PARENTESIS_IZQ, // (
    PARENTESIS_DER, // )
    LLAVE_IZQ,      // {
    LLAVE_DER,      // }

    // Fin de archivo
    EOF
}
