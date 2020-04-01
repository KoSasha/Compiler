import org.junit.Assert;
import org.junit.Test;

public class TokenTest {
    @Test
    public void getNextTokenParanthesis() {
        Token tokenExpected = new Token(TokenClass.LPARENTHESIS, "(", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("(", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenBrace() {
        Token tokenExpected = new Token(TokenClass.LBRACE, "{", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("{", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenBracket() {
        Token tokenExpected = new Token(TokenClass.LSQUAREBRACKET, "[", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("[", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenArray() {
        Token tokenExpected = new Token(TokenClass.ARRAY, "[1, 2, 3, 4]", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("[1, 2, 3, 4]", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenOperatorEquality() {
        Token tokenExpected = new Token(TokenClass.OPERATOREQUALITY, "==", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("==", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextOperatorAssignment() {
        Token tokenExpected = new Token(TokenClass.OPERATORASSIGNMENT, "=", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("=", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenOperatorInequality() {
        Token tokenExpected = new Token(TokenClass.OPERATORINEQUALITY, "!=", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("!=", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenOperatorNot() {
        Token tokenExpected = new Token(TokenClass.OPERATORNOT, "!", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("!", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFunctionReturn() {
        Token tokenExpected = new Token(TokenClass.FUNCTIONRETURN, "->", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("->", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenComments() {
        Token tokenExpected = new Token();
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("//pampampam", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenDivition() {
        Token tokenExpected = new Token(TokenClass.OPERATORDIVISION, "/", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("/10", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenRound() {
        Token tokenExpected = new Token(TokenClass.ROUND, "..", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("..", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenStringVariable() {
        String string = "\"pampampam\"";
        Token tokenExpected = new Token(TokenClass.STRINGVARIABLE, string, 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken(string, 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenOperatorAnd() {
        Token tokenExpected = new Token(TokenClass.OPERATORAND, "&&", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("&&", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenString() {
        Token tokenExpected = new Token(TokenClass.STRING, "&str", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("&str ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenBinaryVariable() {
        Token tokenExpected = new Token(TokenClass.BINARYVARIABLE, "0b010110101011", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("0b010110101011", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenHexVariable() {
        Token tokenExpected = new Token(TokenClass.HEXVARIABLE, "0x12abf635", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("0x12abf635", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenOctalVariable() {
        Token tokenExpected = new Token(TokenClass.OCTALVARIABLE, "0o0436", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("0o0436", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenIntVariable() {
        Token tokenExpected = new Token(TokenClass.INTVARIABLE, "0", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("0\\o0436", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFloatVariable() {
        Token tokenExpected = new Token(TokenClass.FLOATVARIABLE, "0.0436", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("0.0436", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenElse() {
        Token tokenExpected = new Token(TokenClass.ELSE, "else", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("else ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFor() {
        Token tokenExpected = new Token(TokenClass.FOR, "for", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("for ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFunctionDefinition() {
        Token tokenExpected = new Token(TokenClass.FUNCTIONDEFINITION, "fn", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("fn ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFloat() {
        Token tokenExpected = new Token(TokenClass.FLOAT, "f64", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("f64 ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenWhile() {
        Token tokenExpected = new Token(TokenClass.WHILE, "while", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("while ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenMutable() {
        Token tokenExpected = new Token(TokenClass.MUTABLE, "mut", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("mut ", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenMain() {
        Token tokenExpected = new Token(TokenClass.MAIN, "main", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("main()", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenPrintlnMacro() {
        Token tokenExpected = new Token(TokenClass.PRINTLNMACRO, "println!", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("println!", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenFunctionId() {
        Token tokenExpected = new Token(TokenClass.FUNCTIONID, "gdc", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("gdc()", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }

    @Test
    public void getNextTokenId() {
        Token tokenExpected = new Token(TokenClass.ID, "pam", 1, 1);
        Token tokenActual = new Token();
        tokenActual = tokenActual.getNextToken("pam\\", 1, 0);
        Assert.assertEquals(tokenExpected, tokenActual);
    }
}
