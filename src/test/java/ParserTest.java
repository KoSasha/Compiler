import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ParserTest {
    @Test
    public void parserFunctionDefinition() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.FUNCTIONDEFINITION, "fn", 1, 1);
        Token nextToken = new Token(TokenClass.FUNCTIONID, "pam", 1, 4);

        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program",1, null, null);
        ArrayList<Integer> pathToTokenParent = new ArrayList<>();

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserFunctionDefinitionError() {
        String expextedLog = "ERROR";
        String actualLog = null;

        Token currentToken = new Token(TokenClass.FUNCTIONDEFINITION, "fn", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 4);

        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        ArrayList<Integer> pathToTokenParent = new ArrayList<>();

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog.substring(0, 5));
    }

    @Test
    public void parserFunctionDeclartion() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.FUNCTIONID, "pam", 1, 1);
        Token nextToken = new Token(TokenClass.LPARENTHESIS, "(", 1, 2);

        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        ArrayList<Integer> pathToTokenParent = new ArrayList<>();

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserFunctionDeclartionError() {
        String expextedLog = "ERROR";
        String actualLog = null;

        Token currentToken = new Token(TokenClass.FUNCTIONID, "pam", 1, 1);
        Token nextToken = new Token(TokenClass.ROUND, "..", 1, 2);

        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        System.out.println(nextToken.getType());
        Assert.assertEquals(expextedLog, actualLog.substring(0, 5));
    }

    @Test
    public void parserFunctionParam() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.LPARENTHESIS, "(", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.FUNCTIONDECLARATION, "program", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserFunctionMutParam() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.LPARENTHESIS, "(", 1, 1);
        Token nextToken = new Token(TokenClass.MUTABLE, "mut", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.FUNCTIONDECLARATION, "program", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserVariableDefinition() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.VARIABLEDEFINITION, "let", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserVariableDefinitionMut() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.VARIABLEDEFINITION, "let", 1, 1);
        Token nextToken = new Token(TokenClass.MUTABLE, "mut", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserFor() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.FOR, "for", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserConditionFor() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.ID, "pam", 1, 1);
        Token nextToken = new Token(TokenClass.IN, "in", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.PHRASEFOR, "expression_for", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserCycleInFor() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.IN, "pam", 1, 1);
        Token nextToken = new Token(TokenClass.INTVARIABLE, "0", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.CONDITIONFOR, "expression_for", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserIf() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.IF, "if", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.PHRASEIF, "expression_if", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserCoditionIf() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.ID, "pam", 1, 1);
        Token nextToken = new Token(TokenClass.OPERATOREQUALITY, "==", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.PHRASEIF, "expression_if", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserCoditionIfEquality() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.OPERATOREQUALITY, "==", 1, 1);
        Token nextToken = new Token(TokenClass.ID, "pam", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.CONDITIONIF, "expression_comdition_if", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserPrintlnMacro() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.PRINTLNMACRO, "println!", 1, 1);
        Token nextToken = new Token(TokenClass.LPARENTHESIS, "(", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserPrintlnMacroParam() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.LPARENTHESIS, "(", 1, 1);
        Token nextToken = new Token(TokenClass.STRINGVARIABLE, "\"{}\"", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.EXPRESSIONPRINTLNMACRO, "expression_println_macros", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void parserRparenthesisInFunctionDeclaration() {
        String expextedLog = null;
        String actualLog = null;

        Token currentToken = new Token(TokenClass.RPARENTHESIS, ")", 1, 1);
        Token nextToken = new Token(TokenClass.SEMICOLON, ";", 1, 2);

        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Parser parser = new Parser();
        AST ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
        AST parentNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", 1, null, null);

        ast.add(parentNode);
        pathToTokenParent.add(0);

        actualLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
        Assert.assertEquals(expextedLog, actualLog);
    }
}
