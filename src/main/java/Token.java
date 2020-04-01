import lombok.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor@AllArgsConstructor
@Setter@Getter
public class Token implements Lexer {
    private TokenClass type;

    private String lexeme;

    private Integer string;

    private Integer position;

    public static Pattern patternString = Pattern.compile("&str\\W");

    public static Pattern patternSignedIntVariable = Pattern.compile("-\\d+");

    public static Pattern patternIntVariable = Pattern.compile("\\d+");

    public static Pattern patternHexVariable = Pattern.compile("0x[\\da-fA-F]+");

    public static Pattern patternOctalVariable = Pattern.compile("0o[0-7]+");

    public static Pattern patternBinaryVariable = Pattern.compile("0b[0-1]+");

    public static Pattern patternFloatVariable = Pattern.compile("\\d+\\.\\d+");

    public static Pattern patternStringVariable = Pattern.compile("\\\".*\\\"");

    public static Pattern patternArrayVariable = Pattern.compile("\\[\\d+\\,.*]");

    public static Pattern patternIn = Pattern.compile("in\\W");

    public static Pattern patternIf = Pattern.compile("if\\W");

    public static Pattern patternElse = Pattern.compile("else\\W");

    public static Pattern patternFor = Pattern.compile("for\\W");

    public static Pattern patternWhile = Pattern.compile("while\\W");

    public static Pattern patternMutable = Pattern.compile("mut\\W");

    public static Pattern patternFunctionDefinition = Pattern.compile("fn\\W");

    public static Pattern patternVariableDefinition = Pattern.compile("let\\W");

    public static Pattern patternMain = Pattern.compile("main\\(");

    public static Pattern patternReturn = Pattern.compile("return\\W");

    public static Pattern patternAssertMacro = Pattern.compile("assert!");

    public static Pattern patternPrintlnMacro = Pattern.compile("println!");

    public static Pattern patternInt = Pattern.compile("i32\\W");

    public static Pattern patternFloat = Pattern.compile("f64\\W");

    public static Pattern patternFunctionId = Pattern.compile("[a-zA-A]+\\w*\\(");

    public static Pattern patternIdLexeme = Pattern.compile("[a-zA-Z]+\\w*");

    @Override
    public Token getNextToken(String string, Integer numberOfString, Integer position) {
        position += 1;
        switch (string.charAt(position - 1)) {

            case '(':
                return new Token(TokenClass.LPARENTHESIS, "(", numberOfString, position);
            case ')':
                return new Token(TokenClass.RPARENTHESIS, ")", numberOfString, position);
            case '{':
                return new Token(TokenClass.LBRACE, "{", numberOfString, position);
            case '}':
                return new Token(TokenClass.RBRACE, "}", numberOfString, position);
            case '[':
                String lexemeArray;
                if ((lexemeArray = patternMatchingString(patternArrayVariable, string, position)) != null) {
                    return new Token(TokenClass.ARRAY, lexemeArray, numberOfString, position);
                } else {
                    return new Token(TokenClass.LSQUAREBRACKET, "[", numberOfString, position);
                }
            case ']':
                return new Token(TokenClass.RSQUAREBRACKET, "]", numberOfString, position);

            case ' ':
                if (position != string.length()) {
                    return getNextToken(string, numberOfString, position);
                }

            case '=':
                if (position < string.length() && string.charAt(position) == '=') {
                    return new Token(TokenClass.OPERATOREQUALITY, "==", numberOfString, position);
                } else {
                    return new Token(TokenClass.OPERATORASSIGNMENT, "=", numberOfString, position);
                }
            case '!':
                if (position < string.length() && string.charAt(position) == '=') {
                    return new Token(TokenClass.OPERATORINEQUALITY, "!=", numberOfString, position);
                } else {
                    return new Token(TokenClass.OPERATORNOT, "!", numberOfString, position);
                }
            case '+':
                return new Token(TokenClass.OPERATORSUMMING, "+", numberOfString, position);
            case '-':
                if (position < string.length() && string.charAt(position) == '>') {
                    return new Token(TokenClass.FUNCTIONRETURN, "->", numberOfString, position);
                } else {
                    String lexemeNeg;
                    if ((lexemeNeg = patternMatchingString(patternSignedIntVariable, string, position)) != null) {
                        return new Token(TokenClass.SINTVARIABLE, lexemeNeg, numberOfString, position);
                    } else {
                        return new Token(TokenClass.OPERATORDIFFERENCE, "-", numberOfString, position);
                    }
                }
            case '*':
                return new Token(TokenClass.OPERATORMULTIPLICATION, "*", numberOfString, position);
            case '/':
                if (position < string.length() && string.charAt(position) == '/') {
                    return new Token();
                } else {
                    return new Token(TokenClass.OPERATORDIVISION, "/", numberOfString, position);
                }
            case '%':
                return new Token(TokenClass.OPERATORMODULO, "%", numberOfString, position);

            case ':':
                return new Token(TokenClass.COLON, ":", numberOfString, position);
            case ';':
                return new Token(TokenClass.SEMICOLON, ";", numberOfString, position);
            case '.':
                if (position < string.length() && string.charAt(position) == '.') {
                    return new Token(TokenClass.ROUND, "..", numberOfString, position);
                } else {
                    return new Token(TokenClass.POINT, ".", numberOfString, position);
                }
            case ',':
                return new Token(TokenClass.COMMA, ",", numberOfString, position);
            case '\"':
                String lexemeQuotes;
                if ((lexemeQuotes = patternMatchingString(patternStringVariable, string, position)) != null) {
                    return new Token(TokenClass.STRINGVARIABLE, lexemeQuotes, numberOfString, position);
                } else {
                    return new Token(TokenClass.QUOTES, "\"", numberOfString, position);
                }

            case '>':
                return new Token(TokenClass.OPERATORMORE, ">", numberOfString, position);
            case '<':
                return new Token(TokenClass.OPERATORLESS, "<", numberOfString, position);

            case '&':
                if (position < string.length() && string.charAt(position) == '&') {
                    return new Token(TokenClass.OPERATORAND, "&&", numberOfString, position);
                } else {
                    if (patternMatching(patternString, string, position)) {
                        return new Token(TokenClass.STRING, "&str", numberOfString, position);
                    }
                }
            case '|':
                if (position < string.length() && string.charAt(position) == '|') {
                    return new Token(TokenClass.OPERATOROR, "||", numberOfString, position);
                }

            case '0':
                String lexemeVarZero;
                if ((lexemeVarZero = patternMatchingString(patternHexVariable, string, position)) != null) {
                    return new Token(TokenClass.HEXVARIABLE, lexemeVarZero, numberOfString, position);
                } else if ((lexemeVarZero = patternMatchingString(patternOctalVariable, string, position)) != null) {
                    return new Token(TokenClass.OCTALVARIABLE, lexemeVarZero, numberOfString, position);
                } else if ((lexemeVarZero = patternMatchingString(patternBinaryVariable, string, position)) != null) {
                    return new Token(TokenClass.BINARYVARIABLE, lexemeVarZero, numberOfString, position);
                } else if ((lexemeVarZero = patternMatchingString(patternFloatVariable, string, position)) != null) {
                    return new Token(TokenClass.FLOATVARIABLE, lexemeVarZero, numberOfString, position);
                } else if ((lexemeVarZero = patternMatchingString(patternIntVariable, string, position)) != null) {
                    return new Token(TokenClass.INTVARIABLE, lexemeVarZero, numberOfString, position);
                }

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                String lexemeVar;
                if ((lexemeVar = patternMatchingString(patternFloatVariable, string, position)) != null) {
                    return new Token(TokenClass.FLOATVARIABLE, lexemeVar, numberOfString, position);
                } else if ((lexemeVar = patternMatchingString(patternIntVariable, string, position)) != null) {
                    return new Token(TokenClass.INTVARIABLE, lexemeVar, numberOfString, position);
                }

            case 'i':
                if (patternMatching(patternIf, string, position)) {
                    return new Token(TokenClass.IF, "if", numberOfString, position);
                } else if (patternMatching(patternIn, string, position)) {
                    return new Token(TokenClass.IN, "in", numberOfString, position);
                } else if (patternMatching(patternInt, string, position)) {
                    return new Token(TokenClass.INT, "i32", numberOfString, position);
                }

            case 'e':
                if (patternMatching(patternElse, string, position)) {
                    return new Token(TokenClass.ELSE, "else", numberOfString, position);
                }

            case 'f':
                if (patternMatching(patternFor, string, position)) {
                    return new Token(TokenClass.FOR, "for", numberOfString, position);
                } else if (patternMatching(patternFunctionDefinition, string, position)) {
                    return new Token(TokenClass.FUNCTIONDEFINITION, "fn", numberOfString, position);
                } else if (patternMatching(patternFloat, string, position)) {
                    return new Token(TokenClass.FLOAT, "f64", numberOfString, position);
                }

            case 'w':
                if (patternMatching(patternWhile, string, position)) {
                    return new Token(TokenClass.WHILE, "while", numberOfString, position);
                }

            case 'm':
                if (patternMatching(patternMutable, string, position)) {
                    return new Token(TokenClass.MUTABLE, "mut", numberOfString, position);
                } else if (patternMatching(patternMain, string, position)) {
                    return new Token(TokenClass.MAIN, "main", numberOfString, position);
                }

            case 'r':
                if (patternMatching(patternReturn, string, position)) {
                    return new Token(TokenClass.RETURN, "return", numberOfString, position);
                }

            case 'l':
                if (patternMatching(patternVariableDefinition, string, position)) {
                    return new Token(TokenClass.VARIABLEDEFINITION, "let", numberOfString, position);
                }

            case 'a':
                if (patternMatching(patternAssertMacro, string, position)) {
                    return new Token(TokenClass.ASSERTMACRO, "assert!", numberOfString, position);
                }

            case 'p':
                if (patternMatching(patternPrintlnMacro, string, position)) {
                    return new Token(TokenClass.PRINTLNMACRO, "println!", numberOfString, position);
                }


            default:
                String lexeme;
                if ((lexeme = patternMatchingString(patternFunctionId, string, position)) != null) {
                    return new Token(TokenClass.FUNCTIONID, lexeme.substring(0, lexeme.length() - 1), numberOfString, position);
                } else if ((lexeme = patternMatchingString(patternIdLexeme, string, position)) != null) {
                    return new Token(TokenClass.ID, lexeme, numberOfString, position);
                }
                return null;
        }
    }

    public static boolean patternMatching(Pattern pattern, String string, Integer position) {
        Matcher matcher = pattern.matcher(string);
        if (position < string.length() && matcher.find(position - 1) && matcher.start() == position - 1) {
            return true;
        }
        return false;
    }

    public static String patternMatchingString(Pattern pattern, String string, Integer position) {
        Matcher matcher = pattern.matcher(string);
        if (position < string.length() && matcher.find(position - 1) && matcher.start() == position - 1) {
            return string.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.getType() &&
                Objects.equals(lexeme, token.getLexeme()) &&
                string == token.getString() &&
                position == token.getPosition();
    }
}