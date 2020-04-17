import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class SemaTest {

    @Test
    public void analyzeIdPrintlnParam() {
        String expextedLog = null;
        String actualLog = null;

        AST ast = new AST(ASTNodeType.SENTENCEPRINTLNPARAM, "expression_println_param",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.DESCRIPTIONSTRING, "&str",1, ast, null);
        AST child2 = new AST(ASTNodeType.COMMA, ",",1, ast, null);
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(child1);
        ast.getChildren().add(child2);
        ast.getChildren().add(id);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.ID,
                "pam", "1a", null);
        idTable.getIdTable().put(id.getLexeme(), "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        actualLog = sema.idAnalyze(ast.getChildren().get(2), idTable);

        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void analyzeIdOperand() {
        String expextedLog = null;
        String actualLog = null;

        AST ast = new AST(ASTNodeType.OPERATORINEQUALITY, "!=",1, null, new ArrayList<>());
        AST child2 = new AST(ASTNodeType.INTVARIABLE, "10",1, ast, null);
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(id);
        ast.getChildren().add(child2);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.ID,
                "pam", "1a", null);
        idTable.getIdTable().put(id.getLexeme(), "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        actualLog = sema.idAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void analyzeIdOperandNotDefinitionError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.OPERATORINEQUALITY, "!=",1, null, new ArrayList<>());
        AST child2 = new AST(ASTNodeType.INTVARIABLE, "10",1, ast, null);
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(id);
        ast.getChildren().add(child2);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.ID,
                "m", "1a", null);
        idTable.getIdTable().put("m", "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        System.out.println(ast.getChildren().get(0).getLexeme());
        actualLog = sema.idAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeIdOperandUnequalTypeError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.OPERATORLESS, "<",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.DESCRIPTIONINT, "i32",1, ast, null);
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(child1);
        ast.getChildren().add(id);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.FLOAT, ASTNodeType.ID,
                "pam", "1a", null);
        idTable.getIdTable().put("pam", "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        actualLog = sema.idAnalyze(ast.getChildren().get(1), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeIdIndex() {
        String expextedLog = null;
        String actualLog = null;

        AST ast = new AST(ASTNodeType.SENTENCEINDEX, "index",1, null, new ArrayList<>());
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(id);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.ID,
                "pam", "1a", null);
        idTable.getIdTable().put(id.getLexeme(), "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        actualLog = sema.idAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog);
    }

    @Test
    public void analyzeIdIndexInvalidValueError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.SENTENCEINDEX, "index",1, null, new ArrayList<>());
        AST id = new AST(ASTNodeType.ID, "pam",1, ast, null);
        ast.getChildren().add(id);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.FLOAT, ASTNodeType.ID,
                "pam", "1a", null);
        idTable.getIdTable().put(id.getLexeme(), "1a");
        idTable.getIdDeclarationDescriptions().add(idDeclarationDescription);

        Sema sema = new Sema(1, 'a');
        actualLog = sema.idAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeFunctionIdError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST parent = new AST(ASTNodeType.OPERATORASSIGNMENT, "=",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.INT, "i32",1, parent, new ArrayList<>());
        AST ast = new AST(ASTNodeType.FUNCTIONDECLARATION, "expression_function_declaration",1, null, new ArrayList<>());
        parent.getChildren().add(child1);
        parent.getChildren().add(ast);
        AST functionId = new AST(ASTNodeType.FUNCTIONID, "pam",1, ast, new ArrayList<>());
        AST sibling1 = new AST(ASTNodeType.LPARENTHESIS, "(",1, ast, null);
        AST sibling2 = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param",1, ast, new ArrayList<>());
        ast.getChildren().add(functionId);
        ast.getChildren().add(sibling1);
        ast.getChildren().add(sibling2);

        AST param1 = new AST(ASTNodeType.ID, "om",1, sibling2, new ArrayList<>());
        ast.getChildren().get(2).getChildren().add(param1);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.FUNCTIONID,
                "om", "1a", null);
        idTable.getIdTable().put(param1.getLexeme(), "1a");

        Sema sema = new Sema(2, 'a');
        actualLog = sema.functionIdAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeFunctionIdCountParamError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST parent = new AST(ASTNodeType.OPERATORASSIGNMENT, "=",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.INT, "i32",1, parent, new ArrayList<>());
        AST ast = new AST(ASTNodeType.FUNCTIONDECLARATION, "expression_function_declaration",1, parent, new ArrayList<>());
        parent.getChildren().add(child1);
        parent.getChildren().add(ast);
        AST functionId = new AST(ASTNodeType.FUNCTIONID, "pam",1, ast, new ArrayList<>());
        AST sibling1 = new AST(ASTNodeType.LPARENTHESIS, "(",1, ast, null);
        AST sibling2 = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param",1, ast, new ArrayList<>());
        ast.getChildren().add(functionId);
        ast.getChildren().add(sibling1);
        ast.getChildren().add(sibling2);

        AST param1 = new AST(ASTNodeType.ID, "om",1, sibling2, new ArrayList<>());
        sibling2.getChildren().add(param1);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 2, 'a', new ArrayList<>());
        IdDeclarationDescription functionIdDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.FUNCTIONID,
                "pam", "2a", new ArrayList<>());
        idTable.getIdTable().put(functionId.getLexeme(), "2a");
        idTable.getIdDeclarationDescriptions().add(functionIdDeclarationDescription);
        AST sentenceParam = ast.getChildren().get(0).getParent().getChildren().get(2);

        Sema sema = new Sema(2, 'a');
        actualLog = sema.functionIdAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeFunctionIdTypeParamError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST parent = new AST(ASTNodeType.OPERATORASSIGNMENT, "=",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.INT, "i32",1, parent, new ArrayList<>());
        AST ast = new AST(ASTNodeType.FUNCTIONDECLARATION, "expression_function_declaration",1, parent, new ArrayList<>());
        parent.getChildren().add(child1);
        parent.getChildren().add(ast);
        AST functionId = new AST(ASTNodeType.FUNCTIONID, "pam",1, ast, new ArrayList<>());
        AST sibling1 = new AST(ASTNodeType.LPARENTHESIS, "(",1, ast, null);
        AST sibling2 = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param",1, ast, new ArrayList<>());
        ast.getChildren().add(functionId);
        ast.getChildren().add(sibling1);
        ast.getChildren().add(sibling2);

        AST param1 = new AST(ASTNodeType.ID, "om",1, sibling2, new ArrayList<>());
        sibling2.getChildren().add(param1);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 2, 'a', new ArrayList<>());
        IdDeclarationDescription idDeclaration = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.FUNCTIONID,
                "om", "1a", null);
        IdDeclarationDescription paramDeclaration = new IdDeclarationDescription(ASTNodeType.FLOAT, ASTNodeType.FUNCTIONID,
                "op", "1a", null);
        IdDeclarationDescription functionIdDeclarationDescription = new IdDeclarationDescription(ASTNodeType.INT, ASTNodeType.FUNCTIONID,
                "pam", "0a", new ArrayList<>());
        functionIdDeclarationDescription.getFunctionParam().add(paramDeclaration);

        idTable.getIdTable().put(idDeclaration.getLexeme(), "1a");
        idTable.getIdTable().put(paramDeclaration.getLexeme(), "1a");
        idTable.getIdTable().put(functionId.getLexeme(), "0a");

        idTable.getIdDeclarationDescriptions().add(idDeclaration);
        idTable.getIdDeclarationDescriptions().add(functionIdDeclarationDescription);
        idTable.getIdDeclarationDescriptions().add(paramDeclaration);

        AST sentenceParam = ast.getChildren().get(0).getParent().getChildren().get(2);

        Sema sema = new Sema(2, 'a');
        actualLog = sema.functionIdAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeVariableAssignmentError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.OPERATORASSIGNMENT, "expression_assignment",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.DESCRIPTIONFLOAT, "f64",1, ast, null);
        AST var = new AST(ASTNodeType.INTVARIABLE, "10",1, ast, null);
        ast.getChildren().add(child1);
        ast.getChildren().add(var);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());

        Sema sema = new Sema(1, 'a');
        actualLog = sema.functionNumericalVariableAnalyze(ast.getChildren().get(1), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeVariableOperandError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.OPERATORSUMMING, "expression_assignment",1, null, new ArrayList<>());
        AST child1 = new AST(ASTNodeType.DESCRIPTIONFLOAT, "f64",1, ast, null);
        AST var = new AST(ASTNodeType.INTVARIABLE, "10",1, ast, null);
        ast.getChildren().add(child1);
        ast.getChildren().add(var);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());

        Sema sema = new Sema(1, 'a');
        actualLog = sema.functionNumericalVariableAnalyze(ast.getChildren().get(1), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }

    @Test
    public void analyzeVariableArrayIndexError() {
        String expextedLog = "TYPEERROR";
        String actualLog = null;

        AST ast = new AST(ASTNodeType.ARRAYINDEX, "expression_assignment",1, null, new ArrayList<>());
        AST var = new AST(ASTNodeType.FLOATVARIABLE, "10",1, ast, null);
        ast.getChildren().add(var);

        IdTable idTable = new IdTable(new IdentityHashMap<>(), 1, 'a', new ArrayList<>());

        Sema sema = new Sema(1, 'a');
        actualLog = sema.functionNumericalVariableAnalyze(ast.getChildren().get(0), idTable);

        Assert.assertEquals(expextedLog, actualLog.substring(0, 9));
    }
}
