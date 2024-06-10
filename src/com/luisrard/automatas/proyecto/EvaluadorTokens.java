package com.luisrard.automatas.proyecto;

import java.util.TreeMap;
import java.util.function.BiFunction;

public class EvaluadorTokens {
    public enum Token  implements Comparable<Token>{
        PALABRA_RESERVADA("Palabras reservadas"),
        IDENTIFICADOR("Identificadores"),
        OPERADOR_RELACIONAL("Operadores Relacionales"),
        OPERADOR_LOGICO("Operadores Lógicos"),
        OPERADOR_ARITMETICO("Operadores Aritméticos"),
        ASIGNACION("Asignaciones"),
        NUMERO_ENTERO("Número Enteros"),
        NUMERO_DECIMAL("Números Decimales"),
        INCREMENTO("Incremento"),
        DECREMENTO("Decremento"),
        CADENA("Cadena de Caracteres"),
        COMENTARIO("Comentario"),
        COMENTARIO_DE_LINEA("Comentario de Linea"),
        PARENTESIS("Paréntesis"),
        LLAVE("Llaves"),
        ERROR("Errores");

        String value;

        Token(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }
    public static final
    BiFunction<Token, Integer, Integer> TOKEN_INTEGER_INTEGER_BI_FUNCTION = (token, value) -> value == null ? 1 : value + 1;

    public static String detectTokensAndGenerateReport(String text){
        text += " ";
        TreeMap<Token, Integer> tokenIntegerTreeMap = detectTokens(text.toCharArray());
        StringBuilder report = new StringBuilder();
        tokenIntegerTreeMap.forEach((token, value) -> report.append(token).append(" : ").append(value).append("\n"));
        return report.toString();
    }

    public static TreeMap<Token, Integer> detectTokens(char[] characters){
        Token[] tokens = Token.values();
        TreeMap<Token, Integer> tokensMap = new TreeMap<>();
        for (Token token : tokens){
            tokensMap.put(token, 0);
        }

        //q1
        for (int i = 0; i < characters.length - 1;){
            char character = characters[i];
            if (isSeparator(character)){
                i++;
                continue;
            }
            if (isLetter(character)) {
                i = validateIdentifier(characters, i, tokensMap);
            }
            else if (character == '<' || character == '>'){
                i = validateRelationLtgt(characters, i, tokensMap);
            }
            else if (character == '&'){
                i = validateLogicAnd(characters, i, tokensMap);
            }
            else if (character == '|'){
                i = validateLogicOr(characters, i, tokensMap);
            }
            else if (character == '!'){
                i = validateLogicDifferent(characters, i, tokensMap);
            }
            else if (character == '*' || character == '%'){
                i = validateArithmeticByAndPercent(characters, i, tokensMap);
            }
            else if (character == '/'){
                i = validateArithmeticDivision(characters, i, tokensMap);
            }
            else if (character == '+'){
                i = validateArithmeticSum(characters, i, tokensMap);
            }
            else if (character == '-'){
                i = validateArithmeticSubtraction(characters, i, tokensMap);
            }
            else if (character == '='){
                i = validateAssignation(characters, i, tokensMap);
            }
            else if (isNumber(character)){
                i = validateNumber(characters, i, tokensMap);
            }
            else if (character == '"'){
                i = validateString(characters, i, tokensMap);
            }
            else if (character == '{' || character == '}'){
                i = validateBraces(characters, i, tokensMap);
            }
            else if (character == '(' || character == ')'){
                i = validateParentheses(characters, i, tokensMap);
            }
            else {
                i = validateError(characters, i, tokensMap);
            }
        }

        return tokensMap;
    }

    private static int validateError(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                i ++;
                break;
            }
        }
        tokensMap.compute(Token.ERROR, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateParentheses(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.PARENTESIS, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateBraces(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.LLAVE, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateCloseString(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.CADENA, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateString(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                return returnErrorFinal(i, tokensMap);
            } else if (characters[i] == '"') {
                return validateCloseString(characters, i, tokensMap);
            }
        }
        tokensMap.compute(Token.ERROR, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateNumber(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                i ++;
                break;
            } else if (characters[i] == '.') {
                return validateDecimalNumber(characters, i, tokensMap);
            } else if (!isNumber(characters[i])){
                return  validateError(characters, i, tokensMap);
            }
        }
        tokensMap.compute(Token.NUMERO_ENTERO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateDecimalNumber(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                i ++;
                break;
            } else if (!isNumber(characters[i])){
                return  validateError(characters, i, tokensMap);
            }
        }
        tokensMap.compute(Token.NUMERO_DECIMAL, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateAssignation(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.ASIGNACION, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '='){
            return validateRelationLtgtoe(characters, next, tokensMap);
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateArithmeticSubtraction(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_ARITMETICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '-'){
            return validateDecrement(characters, next, tokensMap);
        } else if (isNumber(characters[next])){
            return validateNumber(characters, next, tokensMap);
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateDecrement(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.DECREMENTO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateArithmeticSum(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_ARITMETICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '+'){
            return validateIncrement(characters, next, tokensMap);
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateIncrement(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.INCREMENTO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateArithmeticDivision(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_ARITMETICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '/'){
            return validateCommentLine(characters, next, tokensMap);
        } else if (characters[next] == '*'){
            return validateComment(characters, next, tokensMap);
        }else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateComment(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                return returnErrorFinal(i, tokensMap);
            } else if (characters[i] == '*'){
                return validateAlmostFinalComment(characters, i, tokensMap);
            }
        }
        tokensMap.compute(Token.ERROR, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateAlmostFinalComment(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            return returnErrorFinal(next, tokensMap);
        } else if (characters[next] == '/'){
            return validateFinalComment(characters, next, tokensMap);
        } else {
            return validateComment(characters, next, tokensMap);
        }
    }

    private static int validateFinalComment(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.COMENTARIO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateCommentLine(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                i ++;
                break;
            }
        }
        tokensMap.compute(Token.COMENTARIO_DE_LINEA, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i;
    }

    private static int validateArithmeticByAndPercent(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_ARITMETICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateLogicDifferent(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_LOGICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '=') {
            return validateRelationLtgtoe(characters, next, tokensMap);
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateLogicOr(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            return returnErrorFinal(next, tokensMap);
        } else if (characters[next] == '|') {
            int afterSecondOr = next + 1;
            if (isSeparator(characters[afterSecondOr])) {
                tokensMap.compute(Token.OPERADOR_LOGICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
                return afterSecondOr + 1;
            } else {
                return validateError(characters, afterSecondOr, tokensMap);
            }
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateLogicAnd(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            return returnErrorFinal(next, tokensMap);
        } else if (characters[next] == '&') {
            int afterSecondAnd = next + 1;
            if (isSeparator(characters[afterSecondAnd])) {
                tokensMap.compute(Token.OPERADOR_LOGICO, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
                return afterSecondAnd + 1;
            } else {
                return validateError(characters, afterSecondAnd, tokensMap);
            }
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    /**
     * Returns error in the next position
     * @param i the position
     * @param tokensMap to add the error
     * @return i + 1
     */
    private static int returnErrorFinal(int i, TreeMap<Token, Integer> tokensMap) {
        tokensMap.compute(Token.ERROR, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        return i + 1;
    }

    private static int validateRelationLtgt(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_RELACIONAL, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else if (characters[next] == '=') {
            return validateRelationLtgtoe(characters, next, tokensMap);
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateRelationLtgtoe(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int next = i + 1;
        if (isSeparator(characters[next])){
            tokensMap.compute(Token.OPERADOR_RELACIONAL, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
            return next + 1;
        } else {
            return validateError(characters, next, tokensMap);
        }
    }

    private static int validateIdentifier(char[] characters, int i, TreeMap<Token, Integer> tokensMap) {
        int start = i;
        i++;
        for (; i < characters.length; i ++){
            if (isSeparator(characters[i])){
                validateReservedWords(characters, start, i, tokensMap);
                return i + 1;
            } else if (!isLetter(characters[i]) && characters[i] == '_') {
                return validateError(characters, i, tokensMap);
            }
        }
        validateReservedWords(characters, start, i, tokensMap);
        return i;
    }

    private static void validateReservedWords(char[] characters, int start, int end, TreeMap<Token, Integer> tokensMap) {
        switch (String.valueOf(characters, start, end - start)){
            case "if":
            case "main":
            case "else":
            case "switch":
            case "case":
            case "default":
            case "for":
            case "do":
            case "while":
            case "break":
            case "int":
            case "String":
            case "double":
            case "char":
            case "print":
                tokensMap.compute(Token.PALABRA_RESERVADA, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
                break;
            default:
                tokensMap.compute(Token.IDENTIFICADOR, TOKEN_INTEGER_INTEGER_BI_FUNCTION);
        }
    }

    public static boolean isSeparator(char character){
        return character == '\n' || character == ' ' || character == '\t';
    }

    public static boolean isLetter(char character){
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }


    private static boolean isNumber(char character) {
        return (character >= '0' && character <= '9');
    }
}
