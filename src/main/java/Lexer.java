interface Lexer {
    Token getNextToken(String string, Integer numberOfString, Integer position);
}
