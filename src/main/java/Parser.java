import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@Setter@Getter
public class Parser {

    public String parser(Token currentToken, Token nextToken, AST ast, ArrayList<Integer> pathToTokenParent) {
        AST parentNode;
        if (pathToTokenParent != null  && ast != null) {
            parentNode = ast.searchByPath(pathToTokenParent);
        } else {
            parentNode = ast;
        }
        switch (currentToken.getType()) {
            case COLON:
                checkTheConsistencyOfTheGrammarStartingWithColon(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case LBRACE:
                checkTheConsistencyOfTheGrammarStartingWithLbrace(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case LPARENTHESIS:
                checkTheConsistencyOfTheGrammarStartingWithLparenthesis(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case LSQUAREBRACKET:
                checkTheConsistencyOfTheGrammarStartingWithLsquarebracket(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case RBRACE:
                checkTheConsistencyOfTheGrammarStartingWithRbrace(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case RPARENTHESIS:
                checkTheConsistencyOfTheGrammarStartingWithRparenthesis(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case RSQUAREBRACKET:
                checkTheConsistencyOfTheGrammarStartingWithRsquarebracket(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case SEMICOLON:
                checkTheConsistencyOfTheGrammarStartingWithSemicolon(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;


            case OPERATORSUMMING:
            case OPERATORDIFFERENCE:
            case OPERATORMULTIPLICATION:
            case OPERATORDIVISION:
            case OPERATORMORE:
            case OPERATORLESS:
            case OPERATORMODULO:
            case OPERATOREQUALITY:
            case OPERATORINEQUALITY:
                checkTheConsistencyOfTheGrammarStartingWithOperator(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case OPERATORASSIGNMENT:
                if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.ARRAY ||
                        nextToken.getType() == TokenClass.STRINGVARIABLE ||
                        nextToken.getType() == TokenClass.ID) {
                    break;
                }
                break;

            case SINTVARIABLE:
            case INTVARIABLE:
            case FLOATVARIABLE:
            case HEXVARIABLE:
            case OCTALVARIABLE:
            case BINARYVARIABLE:
                checkTheConsistencyOfTheGrammarStartingWithVariable(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case STRINGVARIABLE:
            case ARRAY:
                checkTheConsistencyOfTheGrammarStartingWithStringVariableOrArray(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case STRING:
                checkTheConsistencyOfTheGrammarStartingWithString(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case INT:
            case FLOAT:
                checkTheConsistencyOfTheGrammarStartingWithType(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case ID:
                checkTheConsistencyOfTheGrammarStartingWithId(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case COMMA:
                checkTheConsistencyOfTheGrammarStartingWithComma(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case POINT:
                checkTheConsistencyOfTheGrammarStartingWithPoint(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case CHAR:
                checkTheConsistencyOfTheGrammarStartingWithChar(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case ROUND:
                checkTheConsistencyOfTheGrammarStartingWithRound(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case IN:
                checkTheConsistencyOfTheGrammarStartingWithIn(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case IF:
                checkTheConsistencyOfTheGrammarStartingWithIf(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case ELSE:
                checkTheConsistencyOfTheGrammarStartingWithElse(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case FOR:
                checkTheConsistencyOfTheGrammarStartingWithFor(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case WHILE:
                checkTheConsistencyOfTheGrammarStartingWithWhile(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case VARIABLEDEFINITION:
                checkTheConsistencyOfTheGrammarStartingWithVariableDefinition(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case ASSERTMACRO:
                checkTheConsistencyOfTheGrammarStartingWithAssertMacro(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case PRINTLNMACRO:
                checkTheConsistencyOfTheGrammarStartingWithPrintlnMacro(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case MUTABLE:
                checkTheConsistencyOfTheGrammarStartingWithMutable(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case FUNCTIONID:
            case MAIN:
                checkTheConsistencyOfTheGrammarStartingWithFunctionId(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case FUNCTIONDEFINITION:
                checkTheConsistencyOfTheGrammarStartingWithFunctionDefinition(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case FUNCTIONRETURN:
                checkTheConsistencyOfTheGrammarStartingWithFunctionReturn(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;

            case RETURN:
                checkTheConsistencyOfTheGrammarStartingWithReturn(nextToken, currentToken, ast, parentNode, pathToTokenParent);
                break;
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithColon(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newExprNode, newNode;
        if (nextToken.getType() == TokenClass.INT ||
                nextToken.getType() == TokenClass.FLOAT ||
                nextToken.getType() == TokenClass.STRING ||
                nextToken.getType() == TokenClass.LSQUAREBRACKET) {
            newExprNode = new AST(ASTNodeType.BINDING, "expression_binding", currentToken.getString(), parentNode, null);
            newNode = new AST(ASTNodeType.COLON, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
            parentNode.add(newExprNode);
            pathToTokenParent.add(parentNode.getChildren().size() - 1);
            ast.addByPath(newNode, pathToTokenParent);
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается тип данных";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithLbrace(Token nextToken, Token currentToken, AST ast,
                                                                    AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newExprNode, newNode;
        if (nextToken.getType() == TokenClass.VARIABLEDEFINITION ||
                nextToken.getType() == TokenClass.ASSERTMACRO ||
                nextToken.getType() == TokenClass.PRINTLNMACRO ||
                nextToken.getType() == TokenClass.WHILE ||
                nextToken.getType() == TokenClass.IF ||
                nextToken.getType() == TokenClass.FOR ||
                nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.RBRACE) {
            boolean added = addAOpeningTokenToTheAST(ast, parentNode, ASTNodeType.BODY, ASTNodeType.valueOf(currentToken.getType().name()),
                    "body", currentToken.getLexeme(), "PHRASE", pathToTokenParent, currentToken.getString());
            if (!added) {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" скобка ожидается в составе цикла или определения функции";
            }
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithLparenthesis(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newExprNode, newNode;
        if (parentNode != null) {
            if (parentNode.getNodeType() == ASTNodeType.FUNCTIONDECLARATION) {
                if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE) {
                    if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
                        return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" ожидается тип параметра";
                    } else {
                        newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", currentToken.getString(), parentNode, null);
                        newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                    }
                } else if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.MUTABLE) {
                    if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
                        newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM, "expression_function_definition_param", currentToken.getString(), parentNode, null);
                        newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                    } else {
                        newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", currentToken.getString(), parentNode, null);
                        newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                    }
                } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {
                    newExprNode = new AST(ASTNodeType.SENTENCEE, "e", currentToken.getString(), parentNode, null);
                    newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается параметр";
                }
            } else if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONASSERTMACRO) {
                if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.ID) {
                    newExprNode = new AST(ASTNodeType.SENTENCEASSERTPARAM, "expression_assert_param", currentToken.getString(), parentNode, null);
                    newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается параметр";
                }
            } else if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONPRINTLNMACRO) {
                if (nextToken.getType() == TokenClass.STRINGVARIABLE) {
                    newExprNode = new AST(ASTNodeType.SENTENCEPRINTLNPARAM, "expression_println_param", currentToken.getString(), parentNode, null);
                    newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается строка";
                }
            } else if (parentNode.getNodeType() == ASTNodeType.RPARENTHESIS) {
                newExprNode = new AST(ASTNodeType.SENTENCEE, "e", currentToken.getString(), parentNode, null);
                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
            } else if (parentNode.getNodeType() == ASTNodeType.POINTFUNCTION) {
                newExprNode = new AST(ASTNodeType.SENTENCEINDEX, "index", currentToken.getString(), parentNode, null);
                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" ожидается после идентификатора функции или макроса";
            }
            parentNode.add(newNode);
            parentNode.add(newExprNode);
            pathToTokenParent.add(parentNode.getChildren().size() - 1);
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithLsquarebracket(Token nextToken, Token currentToken, AST ast,
                                                                            AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.INT ||
                nextToken.getType() == TokenClass.FLOAT) {
            if (parentNode != null && parentNode.getNodeType() == ASTNodeType.BINDING) {
                ast.addByPath(new AST(ASTNodeType.LSQUAREBRACKET, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
                ast.addByPath(new AST(ASTNodeType.ARRAYDECLARATION, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
                pathToTokenParent.add(parentNode.getChildren().size() - 1);
            }
        } else if (nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.ID) {
            ast.addByPath(new AST(ASTNodeType.LSQUAREBRACKET, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
            ast.addByPath(new AST(ASTNodeType.ARRAYINDEX, "expression_array_index", currentToken.getString(), parentNode, null), pathToTokenParent);
            pathToTokenParent.add(parentNode.getChildren().size() - 1);
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается тип данных массива";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithRbrace(Token nextToken, Token currentToken, AST ast,
                                                                    AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (parentNode != null) {
            boolean added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "BODY", pathToTokenParent);
            if (!added) {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
            }
            pathToTokenParent.remove(pathToTokenParent.size() - 1);
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithRparenthesis(Token nextToken, Token currentToken, AST ast,
                                                                          AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (parentNode != null) {
            if (nextToken.getType() == TokenClass.LBRACE ||
                    nextToken.getType() == TokenClass.FUNCTIONRETURN) {
                boolean added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "SENTENCE", pathToTokenParent);
                if (!added) {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                }
            } else if (nextToken.getType() == TokenClass.SEMICOLON ||
                    nextToken.getType() == TokenClass.OPERATOREQUALITY ||
                    nextToken.getType() == TokenClass.OPERATORINEQUALITY ||
                    nextToken.getType() == TokenClass.OPERATORMORE ||
                    nextToken.getType() == TokenClass.OPERATORLESS) {
                if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.POINTFUNCTION) {
                    boolean added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "SENTENCE", pathToTokenParent);
                    if (!added) {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                    }
                    pathToTokenParent.remove(pathToTokenParent.size() - 1);
                    pathToTokenParent.remove(pathToTokenParent.size() - 1);
                } else {
                    boolean added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "SENTENCE", pathToTokenParent);
                    if (!added) {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                    }
                }
            } else {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" может быть только в составе объявления функции или макроса";
            }
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithRsquarebracket(Token nextToken, Token currentToken, AST ast,
                                                                          AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.RPARENTHESIS ||
                nextToken.getType() == TokenClass.SEMICOLON ||
                nextToken.getType() == TokenClass.LBRACE) {
            if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION ||
                    parentNode.getNodeType() == ASTNodeType.ARRAYINDEX)) {
                pathToTokenParent.remove(pathToTokenParent.size() - 1);
                ast.addByPath(new AST(ASTNodeType.RSQUAREBRACKET, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
            } else {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" не у дел";
            }
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается в качестве параметра функции";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithSemicolon(Token nextToken, Token currentToken, AST ast,
                                                                            AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (parentNode != null) {
            if (nextToken.getType() == TokenClass.RBRACE ||
                    nextToken.getType() == TokenClass.ASSERTMACRO ||
                    nextToken.getType() == TokenClass.PRINTLNMACRO ||
                    nextToken.getType() == TokenClass.IF ||
                    nextToken.getType() == TokenClass.WHILE ||
                    nextToken.getType() == TokenClass.FOR ||
                    nextToken.getType() == TokenClass.ID ||
                    nextToken.getType() == TokenClass.FUNCTIONID ||
                    nextToken.getType() == TokenClass.POINT ||
                    nextToken.getType() == TokenClass.VARIABLEDEFINITION) {
                ArrayList<Integer> path = new ArrayList<>(pathToTokenParent);//pathToTokenParent;
                boolean added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "EXPRESSION", pathToTokenParent);
                if (!added) {
                    added = addAClosingTokenToTheAST(ast, parentNode, currentToken, "BODY", path);
                    if (!added) {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" может использоваться только в составе выражения";
                    } else {
                        pathToTokenParent = path;
                    }
                }
            } else if (nextToken.getType() == TokenClass.INTVARIABLE ||
                    nextToken.getType() == TokenClass.FLOATVARIABLE ||
                    nextToken.getType() == TokenClass.SINTVARIABLE ||
                    nextToken.getType() == TokenClass.HEXVARIABLE ||
                    nextToken.getType() == TokenClass.OCTALVARIABLE ||
                    nextToken.getType() == TokenClass.BINARYVARIABLE) {
                if (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION) {
                    ast.addByPath(new AST(ASTNodeType.SEMICOLON, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается объявление массива";
                }
            } else {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" не у дел";
            }

        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithOperator(Token nextToken, Token currentToken, AST ast,
                                                                       AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.ID) {
            return null;
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается операнд";
        }
    }

    public String checkTheConsistencyOfTheGrammarStartingWithVariable(Token nextToken, Token currentToken, AST ast,
                                                                      AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        if (nextToken.getType() == TokenClass.SEMICOLON ||
                nextToken.getType() == TokenClass.LBRACE) {
            newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), currentToken.getString(), parentNode, null);
            ast.addByPath(newNode, pathToTokenParent);
        } else if (nextToken.getType() == TokenClass.COMMA) {
            ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), currentToken.getString(), parentNode, null),
                    pathToTokenParent);
        } else if (nextToken.getType() == TokenClass.ROUND) {
            if (parentNode != null && parentNode.getNodeType() == ASTNodeType.CYCLE) {
                addANodeToTheAST(ast, parentNode, ASTNodeType.ROUND, ASTNodeType.valueOf(currentToken.getType().name()),
                        "expression_round", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" ожидается в цикле";
            }
        } else if (nextToken.getType() == TokenClass.RSQUAREBRACKET) {
            if (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION ||
                    parentNode.getNodeType() == ASTNodeType.ARRAYINDEX) {
                ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), currentToken.getString(), parentNode, null)
                        , pathToTokenParent);
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: \"" + nextToken.getLexeme() + "\" нет открывающей скобки";
            }
        } else if (nextToken.getType() == TokenClass.OPERATORSUMMING ||
                nextToken.getType() == TokenClass.OPERATORDIFFERENCE ||
                nextToken.getType() == TokenClass.OPERATORMULTIPLICATION ||
                nextToken.getType() == TokenClass.OPERATORDIVISION ||
                nextToken.getType() == TokenClass.OPERATORMODULO ||
                nextToken.getType() == TokenClass.OPERATOROR ||
                nextToken.getType() == TokenClass.OPERATORAND) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                    nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {
            if (parentNode.getNodeType() == ASTNodeType.OPERATORSUMMING ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIFFERENCE ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIVISION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMULTIPLICATION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMODULO ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORAND ||
                    parentNode.getNodeType() == ASTNodeType.OPERATOROR ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMORE ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORLESS ||
                    parentNode.getNodeType() == ASTNodeType.OPERATOREQUALITY ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORINEQUALITY ||
                    parentNode.getNodeType() == ASTNodeType.SENTENCEINDEX) {
                ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                        currentToken.getString(), parentNode, null), pathToTokenParent);
            } else {
                ArrayList<Integer> path = new ArrayList<>(pathToTokenParent);
                boolean checkOpening = false;
                int size = path.size();
                for (int i = size - 1; i >= 0; i--) {
                    if (!parentNode.getNodeType().name().startsWith("SENTENCE")) {
                        parentNode = parentNode.getParent();
                        path.remove(i);
                    } else {
                        checkOpening = true;
                    }
                }
                if (checkOpening) {
                    ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                }
            }
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithStringVariableOrArray(Token nextToken, Token currentToken, AST ast,
                                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        if (nextToken.getType() == TokenClass.SEMICOLON ||
                nextToken.getType() == TokenClass.COMMA ||
                nextToken.getType() == TokenClass.RPARENTHESIS) {
            if (parentNode.getNodeType() != ASTNodeType.BODY) {
                newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                        currentToken.getString(), parentNode, null);
                ast.addByPath(newNode, pathToTokenParent);
            } else {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" одинокая переменная";
            }
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithString(Token nextToken, Token currentToken, AST ast,
                                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.COMMA ||
                nextToken.getType() == TokenClass.RPARENTHESIS) {
            ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null),
                    pathToTokenParent);
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithType(Token nextToken, Token currentToken, AST ast,
                                                                    AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.OPERATORASSIGNMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                    nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else if (nextToken.getType() == TokenClass.COMMA ||
                nextToken.getType() == TokenClass.LBRACE ||
                nextToken.getType() == TokenClass.RPARENTHESIS) {
            ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null),
                    pathToTokenParent);
        } else if (nextToken.getType() == TokenClass.SEMICOLON) {
            if (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION) {
                ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                                currentToken.getString(), parentNode, null),
                        pathToTokenParent);
            }
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" одинокий тип данных";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithId(Token nextToken, Token currentToken, AST ast,
                                                                  AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newNode, newExprNode;
        if (nextToken.getType() == TokenClass.SEMICOLON) {
            if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONASSIGNMENT ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORASSIGNMENT ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMULTIPLICATION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIFFERENCE ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORSUMMING ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIVISION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMODULO ||
                    parentNode.getNodeType() == ASTNodeType.EXPRESSIONRETURN) {
                newNode = new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
                parentNode.add(newNode);
            } else {
                return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" одинокая переменная";
            }
        } else if (nextToken.getType() == TokenClass.COLON) {
            if (parentNode != null) {
                if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONVARIABLEDEFINITION ||
                        parentNode.getNodeType() == ASTNodeType.MUTABLEDEFINITION ||
                        parentNode.getNodeType() == ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.TYPEBINDING, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_type_binding", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" связывание переменной возможно только при ее определении";
                }
            }
        } else if (nextToken.getType() == TokenClass.COMMA) {
            ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
        } else if (nextToken.getType() == TokenClass.POINT) {
            if (parentNode.getNodeType() == ASTNodeType.OPERATOREQUALITY) {
                ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
            } else if (parentNode.getNodeType() == ASTNodeType.PHRASEIF) {
                newExprNode = new AST(ASTNodeType.CONDITIONIF, "expression_condition_if", currentToken.getString(), parentNode, null);
                ast.addByPath(newExprNode, pathToTokenParent);
                pathToTokenParent.add(parentNode.getChildren().size() - 1);
                parentNode = parentNode.getChildren().get(parentNode.getChildren().size() - 1);
                addANodeToTheAST(ast, parentNode, ASTNodeType.OPERATOREQUALITY, ASTNodeType.valueOf(currentToken.getType().name()),
                        "==", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            } else if (parentNode.getNodeType() == ASTNodeType.CONDITIONIF) {
                pathToTokenParent.add(parentNode.getChildren().size() - 1);
                parentNode = parentNode.getChildren().get(0);
                ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
            }
        } else if (nextToken.getType() == TokenClass.OPERATORINEQUALITY ||
                nextToken.getType() == TokenClass.OPERATOREQUALITY ||
                nextToken.getType() == TokenClass.OPERATORMORE ||
                nextToken.getType() == TokenClass.OPERATORLESS) {
            if (parentNode != null) {
                if (parentNode.getNodeType() == ASTNodeType.PHRASEIF) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONIF, ASTNodeType.valueOf(nextToken.getType().name()),
                            "expression_condition_if", nextToken.getLexeme(), pathToTokenParent, currentToken.getString());
                    pathToTokenParent.add(0);
                    parentNode = ast.searchByPath(pathToTokenParent);
                    parentNode.add(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null));
                } else if (parentNode.getNodeType() == ASTNodeType.PHRASEWHILE) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONWHILE, ASTNodeType.valueOf(nextToken.getType().name()),
                            "expression_condition_while", nextToken.getLexeme(), pathToTokenParent, currentToken.getString());
                    pathToTokenParent.add(0);
                    parentNode = ast.searchByPath(pathToTokenParent);
                    parentNode.add(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null));
                } else if (parentNode.getNodeType() == ASTNodeType.SENTENCEASSERTPARAM) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                            nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
                }
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" ";
            }
        } else if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT ||
                nextToken.getType() == TokenClass.OPERATORSUMMING ||
                nextToken.getType() == TokenClass.OPERATORDIFFERENCE ||
                nextToken.getType() == TokenClass.OPERATORMULTIPLICATION ||
                nextToken.getType() == TokenClass.OPERATORDIVISION ||
                nextToken.getType() == TokenClass.OPERATORMODULO ||
                nextToken.getType() == TokenClass.OPERATOROR ||
                nextToken.getType() == TokenClass.OPERATORAND) {
            if (parentNode.getNodeType() == ASTNodeType.BODY) {
                addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONASSIGNMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                        "expression_assignment", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            } else {
                addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                        nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            }
        } else if (nextToken.getType() == TokenClass.IN) {
            if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.PHRASEFOR ||
                    (parentNode.getNodeType() == ASTNodeType.MUTABLEDEFINITION))) {
                addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONFOR, ASTNodeType.valueOf(currentToken.getType().name()),
                        "expression_condition_for", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: \"" + currentToken.getLexeme() + "\" c \"in\" может искользоваться только в цикле";
            }
        } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {
            if (parentNode.getNodeType() == ASTNodeType.OPERATORSUMMING ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIFFERENCE ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORDIVISION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMULTIPLICATION ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMODULO ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORAND ||
                    parentNode.getNodeType() == ASTNodeType.OPERATOROR ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORMORE ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORLESS ||
                    parentNode.getNodeType() == ASTNodeType.OPERATOREQUALITY ||
                    parentNode.getNodeType() == ASTNodeType.OPERATORINEQUALITY ||
                    parentNode.getNodeType() == ASTNodeType.SENTENCEINDEX) {
                ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
            } else {
                ArrayList<Integer> path = new ArrayList<>(pathToTokenParent);
                boolean checkOpening = false;
                int size = path.size();
                for (int i = size - 1; i >= 0; i--) {
                    if (!parentNode.getNodeType().name().startsWith("SENTENCE")) {
                        parentNode = parentNode.getParent();
                        path.remove(i);
                    } else {
                        checkOpening = true;
                    }
                }
                if (checkOpening) {
                    ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                }
            }
        } else if (nextToken.getType() == TokenClass.LSQUAREBRACKET) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.ARRAYELEMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_array_element", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else if (nextToken.getType() == TokenClass.RSQUAREBRACKET) {
            ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
        } else if (nextToken.getType() == TokenClass.LBRACE) {
            ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), currentToken.getString(), parentNode, null), pathToTokenParent);
        } else {
            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" не у дел";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithComma(Token nextToken, Token currentToken, AST ast,
                                                                AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.STRINGVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.MUTABLE) {
            int size = pathToTokenParent.size();
            for (int i = size - 1; i >= 0; i--) {
                if (!parentNode.getNodeType().name().startsWith("SENTENCE")) {
                    parentNode = parentNode.getParent();
                    pathToTokenParent.remove(i);
                } else {
                    newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                            currentToken.getString(), parentNode, null);
                    ast.addByPath(newNode, pathToTokenParent);
                    break;
                }
            }
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается перечисление параметров";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithPoint(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.CHAR) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.POINTFUNCTION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_point_function", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается после идентификатора";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithChar(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.LPARENTHESIS) {
            ast.addByPath(new AST(ASTNodeType.CHAR, currentToken.getLexeme(), currentToken.getString(),  parentNode, null), pathToTokenParent);
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: \"" + currentToken.getLexeme() + "\" ожидается скобка";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithRound(Token nextToken, Token currentToken, AST ast,
                                                                  AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE) {
            return null;
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
        }
    }

    public String checkTheConsistencyOfTheGrammarStartingWithIn(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (parentNode != null && parentNode.getNodeType() == ASTNodeType.CONDITIONFOR) {
            if (nextToken.getType() == TokenClass.ID ||
                    nextToken.getType() == TokenClass.INTVARIABLE ||
                    nextToken.getType() == TokenClass.SINTVARIABLE ||
                    nextToken.getType() == TokenClass.FLOATVARIABLE ||
                    nextToken.getType() == TokenClass.HEXVARIABLE ||
                    nextToken.getType() == TokenClass.OCTALVARIABLE ||
                    nextToken.getType() == TokenClass.BINARYVARIABLE) {
                addANodeToTheAST(ast, parentNode, ASTNodeType.CYCLE, ASTNodeType.valueOf(currentToken.getType().name()),
                        "expression_in", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
            } else {
                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                        ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
            }
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " может использоваться только в цикле";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithIf(Token nextToken, Token currentToken, AST ast,
                                                                AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.STRINGVARIABLE) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEIF, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_if", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithElse(Token nextToken, Token currentToken, AST ast,
                                                                AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.LBRACE) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEELSE, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_else", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithFor(Token nextToken, Token currentToken, AST ast,
                                                                  AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.MUTABLE) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEFOR, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_for", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithWhile(Token nextToken, Token currentToken, AST ast,
                                                                 AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.STRINGVARIABLE) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEWHILE, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_while", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithVariableDefinition(Token nextToken, Token currentToken, AST ast,
                                                                   AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.MUTABLE) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONVARIABLEDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_variable_definition", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается идентификатор или \"mut\"";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithAssertMacro(Token nextToken, Token currentToken, AST ast,
                                                                                AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.LPARENTHESIS) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONASSERTMACRO, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression__assert_macros", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithPrintlnMacro(Token nextToken, Token currentToken, AST ast,
                                                                         AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.LPARENTHESIS) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONPRINTLNMACRO, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_println_macros", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithMutable(Token nextToken, Token currentToken, AST ast,
                                                                          AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.MUTABLEDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_mutable_definition", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithFunctionId(Token nextToken, Token currentToken, AST ast,
                                                                     AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.LPARENTHESIS) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.FUNCTIONDECLARATION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_function_declaration", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithFunctionDefinition(Token nextToken, Token currentToken, AST ast,
                                                                        AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.FUNCTIONID) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEFUNCTIONDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_function_definition", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else if (nextToken.getType() == TokenClass.MAIN) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEMAINFUNCTIONDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_main_function_definition", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается имя функции";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithFunctionReturn(Token nextToken, Token currentToken, AST ast,
                                                                                AST parentNode, ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        if (nextToken.getType() == TokenClass.INT ||
                nextToken.getType() == TokenClass.FLOAT ||
                nextToken.getType() == TokenClass.STRING) {
            newNode = new AST(ASTNodeType.FUNCTIONRETURN, currentToken.getLexeme(), currentToken.getString(), parentNode, null);
            ast.addByPath(newNode, pathToTokenParent);
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается возвращаемый тип данных";
        }
        return null;
    }

    public String checkTheConsistencyOfTheGrammarStartingWithReturn(Token nextToken, Token currentToken, AST ast,
                                                                            AST parentNode, ArrayList<Integer> pathToTokenParent) {
        if (nextToken.getType() == TokenClass.ID ||
                nextToken.getType() == TokenClass.INTVARIABLE ||
                nextToken.getType() == TokenClass.SINTVARIABLE ||
                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                nextToken.getType() == TokenClass.HEXVARIABLE ||
                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                nextToken.getType() == TokenClass.BINARYVARIABLE ||
                nextToken.getType() == TokenClass.STRINGVARIABLE ||
                nextToken.getType() == TokenClass.ARRAY ||
                nextToken.getType() == TokenClass.FUNCTIONID) {
            addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONRETURN, ASTNodeType.valueOf(currentToken.getType().name()),
                    "expression_return", currentToken.getLexeme(), pathToTokenParent, currentToken.getString());
        } else {
            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                    ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение (число или идентификатор)";
        }
        return null;
    }

    public static void addANodeToTheAST(AST ast, AST parentNode, ASTNodeType typeExprNode, ASTNodeType typeChildNode,
                                        String exprNodeLexeme, String childNodeLexeme, ArrayList<Integer> pathToTokenParent,
                                        Integer line) {
        AST newNode, newExprNode;
        newExprNode = new AST(typeExprNode, exprNodeLexeme, line, parentNode, null);
        newNode = new AST(typeChildNode, childNodeLexeme, line, newExprNode, null);
        parentNode.add(newExprNode);
        pathToTokenParent.add(parentNode.getChildren().size() - 1);
        ast.addByPath(newNode, pathToTokenParent);
    }

    public static boolean addAOpeningTokenToTheAST(AST ast, AST parentNode, ASTNodeType typeExprNode, ASTNodeType typeChildNode,
                                                   String exprNodeLexeme, String childNodeLexeme, String startWith,
                                                   ArrayList<Integer> pathToTokenParent, Integer line) {
        AST newNode, newExprNode;
        int size = pathToTokenParent.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!parentNode.getNodeType().name().startsWith(startWith)) {
                parentNode = parentNode.getParent();
                pathToTokenParent.remove(i);
            } else {
                newExprNode = new AST(typeExprNode, exprNodeLexeme, line, parentNode, null);
                newNode = new AST(typeChildNode, childNodeLexeme, line, parentNode, null);
                parentNode.add(newNode);
                parentNode.add(newExprNode);
                pathToTokenParent.add(parentNode.getChildren().size() - 1);
                return true;
            }
        }
        return false;
    }

    public static boolean addAClosingTokenToTheAST(AST ast, AST parentNode, Token currentToken, String startWith,
                                                   ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        int size = pathToTokenParent.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!parentNode.getNodeType().name().startsWith(startWith)) {
                parentNode = parentNode.getParent();
                pathToTokenParent.remove(i);
            } else {
                newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(),
                        currentToken.getString(), parentNode.getParent(), null);
                if (startWith.equals("EXPRESSION")) {
                    ast.addByPath(newNode, pathToTokenParent);
                    pathToTokenParent.remove(i);
                    return true;
                }
                pathToTokenParent.remove(i);
                ast.addByPath(newNode, pathToTokenParent);
                return true;
            }
        }

        return false;
    }
}
