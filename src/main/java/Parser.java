import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@Setter@Getter
public class Parser {

    public String parser(Token currentToken, Token nextToken, AST ast, ArrayList<Integer> pathToTokenParent) {
        AST parentNode, newNode, newExprNode;
        if (pathToTokenParent != null  && ast != null) {
            parentNode = ast.searchByPath(pathToTokenParent);
        } else {
            parentNode = ast;
        }
        switch (currentToken.getType()) {
            case COLON:
                if (nextToken.getType() == TokenClass.INT ||
                        nextToken.getType() == TokenClass.FLOAT ||
                        nextToken.getType() == TokenClass.STRING) {
                    newExprNode = new AST(ASTNodeType.BINDING, "expression_binding", parentNode, null);
                    newNode = new AST(ASTNodeType.COLON, currentToken.getLexeme(), parentNode, null);
                    parentNode.add(newExprNode);
                    pathToTokenParent.add(parentNode.getChildren().size() - 1);
                    ast.addByPath(newNode, pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается тип данных";
                }
                break;

            case LPARENTHESIS:
                if (parentNode != null) {
                    if (parentNode.getNodeType() == ASTNodeType.FUNCTIONDECLARATION) {
                        if (nextToken.getType() == TokenClass.INTVARIABLE ||
                                nextToken.getType() == TokenClass.FLOATVARIABLE ||
                                nextToken.getType() == TokenClass.SINTVARIABLE ||
                                nextToken.getType() == TokenClass.HEXVARIABLE ||
                                nextToken.getType() == TokenClass.OCTALVARIABLE ||
                                nextToken.getType() == TokenClass.BINARYVARIABLE) {
                            if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.FUNCTIONDEFINITION) {
                                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                        ">: \"" + currentToken.getLexeme() + "\" ожидается тип параметра";
                            } else {
                                newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", parentNode, null);
                                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                            }
                        } else if (nextToken.getType() == TokenClass.ID ||
                                nextToken.getType() == TokenClass.MUTABLE) {
                            if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.FUNCTIONDEFINITION) {
                                newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM, "expression_function_definition_param", parentNode, null);
                                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                            } else {
                                newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", parentNode, null);
                                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                            }
                        } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {
                            newExprNode = new AST(ASTNodeType.SENTENCEE, "e", parentNode, null);
                            newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
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
                            newExprNode = new AST(ASTNodeType.SENTENCEASSERTPARAM, "expression_assert_param", parentNode, null);
                            newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                        } else {
                            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" ожидается параметр";
                        }
                    } else if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONPRINTLNMACRO) {
                        if (nextToken.getType() == TokenClass.STRINGVARIABLE) {
                            newExprNode = new AST(ASTNodeType.SENTENCEPRINTLNPARAM, "expression_println_param", parentNode, null);
                            newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                        } else {
                            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" ожидается строка";
                        }
                    } else if (parentNode.getNodeType() == ASTNodeType.RPARENTHESIS) {
                        newExprNode = new AST(ASTNodeType.SENTENCEE, "e", parentNode, null);
                        newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
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
                break;

            case LBRACE:
                if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION ||
                        parentNode.getNodeType() == ASTNodeType.PHRASEFOR ||
                        parentNode.getNodeType() == ASTNodeType.PHRASEIF ||
                        parentNode.getNodeType() == ASTNodeType.PHRASEWHILE ||
                        parentNode.getNodeType() == ASTNodeType.PHRASEELSE)) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.BODY, ASTNodeType.valueOf(currentToken.getType().name()),
                            "body", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
                }
                break;

            case LSQUAREBRACKET:
//                if (nextToken.getNodeType() == TokenClass.) {
//
//                }
                break;

            case SEMICOLON:
                if (nextToken.getType() == TokenClass.RBRACE) {
                    int size = pathToTokenParent.size();
                    for (int i = size; i >= 0; i--) {
                        if (parentNode.getNodeType().name().compareTo("EXPRESSION") <= 0) {
                            parentNode = parentNode.getParent();
                            pathToTokenParent.remove(i);
                        } else {
                            newNode = new AST(ASTNodeType.SEMICOLON, currentToken.getLexeme(), parentNode, null);
                            ast.addByPath(newNode, pathToTokenParent);
                            break;
                        }
                    }
                } else if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE) {

                } else {
                    int size = pathToTokenParent.size();
                    for (int i = size - 1; i >= 0; i--) {
                        if (!parentNode.getNodeType().name().startsWith("EXPRESSION") &&
                                !parentNode.getNodeType().name().startsWith("PROGRAM")) {
                            parentNode = parentNode.getParent();
                            pathToTokenParent.remove(i);
                        } else {
                            newNode = new AST(ASTNodeType.SEMICOLON, currentToken.getLexeme(), parentNode, null);
                            ast.addByPath(newNode, pathToTokenParent);
                            pathToTokenParent.remove(i);
                            break;
                        }
                    }
                }
                break;


            case OPERATORSUMMING:
            case OPERATORDIFFERENCE:
            case OPERATORMULTIPLICATION:
            case OPERATORDIVISION:
            case OPERATORMORE:
            case OPERATORLESS:
            case OPERATORMODULO:
                if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.ID) {
                    break;
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается операнд";
                }

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
                } else if (nextToken.getType() == TokenClass.FUNCTIONID) {

                }
                break;

            case SINTVARIABLE:
            case INTVARIABLE:
            case FLOATVARIABLE:
            case HEXVARIABLE:
            case OCTALVARIABLE:
            case BINARYVARIABLE:
                if (nextToken.getType() == TokenClass.SEMICOLON) {
                    newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null);
                    ast.addByPath(newNode, pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.COMMA) {

                } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {

                } else if (nextToken.getType() == TokenClass.LBRACE) {

                } else if (nextToken.getType() == TokenClass.ROUND) {

                } else if (nextToken.getType() == TokenClass.RSQUAREBRACKET) {

                }  else if (nextToken.getType() == TokenClass.OPERATOREQUALITY) {

                } else if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT) {

                    newExprNode = new AST(ASTNodeType.EXPRESSIONASSIGNMENT, nextToken.getLexeme(), parentNode, null);
                    newNode = new AST(ASTNodeType.OPERATORASSIGNMENT, "=", parentNode, null);
                    parentNode.add(newExprNode);
                    pathToTokenParent.add(parentNode.getChildren().size() - 1);
                    ast.addByPath(newNode, pathToTokenParent);
                    pathToTokenParent.add(newExprNode.getChildren().size() - 1);
                    ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), newNode, null), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.OPERATORINEQUALITY) {

                } else if (nextToken.getType() == TokenClass.OPERATORSUMMING) {
                    // добавить в дерево токен суммы и его ребенком сделать этот ID
                } else if (nextToken.getType() == TokenClass.OPERATORDIFFERENCE) {

                } else if (nextToken.getType() == TokenClass.OPERATORMULTIPLICATION) {

                } else if (nextToken.getType() == TokenClass.OPERATORDIVISION) {

                } else if (nextToken.getType() == TokenClass.OPERATORMODULO) {

                } else if (nextToken.getType() == TokenClass.OPERATORMORE) {

                } else if (nextToken.getType() == TokenClass.OPERATORLESS) {

                } else if (nextToken.getType() == TokenClass.OPERATORAND) {

                } else if (nextToken.getType() == TokenClass.OPERATOROR) {

                } else if (nextToken.getType() == TokenClass.IN) {

                }
                break;

            case INT:
            case FLOAT:
                if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.OPERATORASSIGNMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                            nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.COMMA) {

                } else if (nextToken.getType() == TokenClass.SEMICOLON) {

                } else if (nextToken.getType() == TokenClass.LBRACE) {

                } else if (nextToken.getType() == TokenClass.RPARENTHESIS) {

                } else {

                }
                break;

            case ID:
                if (nextToken.getType() == TokenClass.SEMICOLON) {
                    newNode = new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null);
                    parentNode.add(newNode);
                } else if (nextToken.getType() == TokenClass.COLON) {
                    if (parentNode != null) {
                        if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONVARIABLEDEFINITION ||
                                (parentNode.getParent() != null &&
                                        parentNode.getParent().getNodeType() == ASTNodeType.EXPRESSIONVARIABLEDEFINITION)) {
                            addANodeToTheAST(ast, parentNode, ASTNodeType.TYPEBINDING, ASTNodeType.valueOf(currentToken.getType().name()),
                                    "expression_type_binding", currentToken.getLexeme(), pathToTokenParent);
                        } else {
                            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" связывание переменной возможно только при ее определении";
                        }
                    }
                } else if (nextToken.getType() == TokenClass.COMMA) {

                } else if (nextToken.getType() == TokenClass.POINT) {

                } else if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT) {

//                    addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONASSIGNMENT, ASTNodeType.valueOf(currentToken.getType().name()),
//                            "expression_type_binding", currentToken.getLexeme(), pathToTokenParent);

                    newExprNode = new AST(ASTNodeType.EXPRESSIONASSIGNMENT, nextToken.getLexeme(), parentNode, null);
                    newNode = new AST(ASTNodeType.OPERATORASSIGNMENT, "=", parentNode, null);
                    parentNode.add(newExprNode);
                    pathToTokenParent.add(parentNode.getChildren().size() - 1);
                    ast.addByPath(newNode, pathToTokenParent);
                    pathToTokenParent.add(newExprNode.getChildren().size() - 1);
                    ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), newNode, null), pathToTokenParent);
//                    newExprNode.add();
                } else if (nextToken.getType() == TokenClass.OPERATORINEQUALITY ||
                        nextToken.getType() == TokenClass.OPERATOREQUALITY ||
                        nextToken.getType() == TokenClass.OPERATORMORE ||
                        nextToken.getType() == TokenClass.OPERATORLESS) {
                    if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.PHRASEIF ||
                            parentNode.getNodeType() == ASTNodeType.PHRASEWHILE ||
                            parentNode.getNodeType() == ASTNodeType.SENTENCEASSERTPARAM)) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                                nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
                    }
                } else if (nextToken.getType() == TokenClass.OPERATORSUMMING) {
                    // добавить в дерево токен суммы и его ребенком сделать этот ID
                } else if (nextToken.getType() == TokenClass.OPERATORDIFFERENCE) {

                } else if (nextToken.getType() == TokenClass.OPERATORMULTIPLICATION) {

                } else if (nextToken.getType() == TokenClass.OPERATORDIVISION) {

                } else if (nextToken.getType() == TokenClass.OPERATORMODULO) {

                } else if (nextToken.getType() == TokenClass.OPERATORAND) {

                } else if (nextToken.getType() == TokenClass.OPERATOROR) {

                } else if (nextToken.getType() == TokenClass.IN) {
                    if (parentNode != null && parentNode.getNodeType() == ASTNodeType.PHRASEFOR) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONFOR, ASTNodeType.valueOf(currentToken.getType().name()),
                                "expression_condition_for", currentToken.getLexeme(), pathToTokenParent);
                    } else {
                        return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" c \"in\" может искользоваться только в цикле";
                    }
                }
                break;

            case COMMA:
                if (nextToken.getType() == TokenClass.ID) {

                } else if (nextToken.getType() == TokenClass.MUTABLE) {

                }
                break;

            case POINT:
                break;

            case ROUND:
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE) {
                    newNode = new AST(ASTNodeType.ROUND, currentToken.getLexeme(), parentNode, null);
                    ast.addByPath(newNode, pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
                }
                break;

            case IN:
                if (parentNode != null && parentNode.getNodeType() == ASTNodeType.CONDITIONFOR) {
                    if (nextToken.getType() == TokenClass.ID ||
                            nextToken.getType() == TokenClass.INTVARIABLE ||
                            nextToken.getType() == TokenClass.SINTVARIABLE ||
                            nextToken.getType() == TokenClass.FLOATVARIABLE ||
                            nextToken.getType() == TokenClass.HEXVARIABLE ||
                            nextToken.getType() == TokenClass.OCTALVARIABLE ||
                            nextToken.getType() == TokenClass.BINARYVARIABLE) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.CYCLE, ASTNodeType.valueOf(currentToken.getType().name()),
                                "expression_in", currentToken.getLexeme(), pathToTokenParent);
                    } else {
                        return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
                    }
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " может использоваться только в цикле";
                }
                break;

            case IF:
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.STRINGVARIABLE) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEIF, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_if", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
                }
                break;

            case ELSE:
                if (nextToken.getType() == TokenClass.LBRACE) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEELSE, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_else", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
                }
                break;

            case FOR:
                if (nextToken.getType() == TokenClass.ID) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEFOR, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_for", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
                }
                break;

            case WHILE:
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.STRINGVARIABLE) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEWHILE, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_while", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
                }
                break;

            case VARIABLEDEFINITION:
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.MUTABLE) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONVARIABLEDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_variable_definition", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается идентификатор или \"mut\"";
                }
                break;

            case ASSERTMACRO:
                if (nextToken.getType() == TokenClass.LPARENTHESIS) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONASSERTMACRO, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression__assert_macros", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
                }
                break;

            case PRINTLNMACRO:
                if (nextToken.getType() == TokenClass.LPARENTHESIS) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.EXPRESSIONPRINTLNMACRO, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_println_macros", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
                }
                break;

            case MUTABLE:
                if (nextToken.getType() == TokenClass.ID) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.MUTABLEDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_mutable_definition", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается идентификатор";
                }
                break;

            case MAIN:
                if (nextToken.getType() == TokenClass.LPARENTHESIS) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.MAINDECLARATION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_declaration_main", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
                }
                break;

            case FUNCTIONID:
                if (nextToken.getType() == TokenClass.LPARENTHESIS) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.FUNCTIONDECLARATION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_function_declaration", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается \"(\"";
                }
                break;

            case FUNCTIONDEFINITION:
                if (nextToken.getType() == TokenClass.FUNCTIONID) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEFUNCTIONDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_function_definition", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается имя функции";
                }
                break;

            case FUNCTIONRETURN:
                if (nextToken.getType() == TokenClass.INT ||
                        nextToken.getType() == TokenClass.FLOAT ||
                        nextToken.getType() == TokenClass.STRING) {
                    newNode = new AST(ASTNodeType.FUNCTIONRETURN, currentToken.getLexeme(), parentNode, null);
                    ast.addByPath(newNode, pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемый тип данных";
                }
                break;

            case RETURN:
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
                            "expression_return", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение (число или идентификатор)";
                }
                break;
        }
        return null;
    }

    public static void addANodeToTheAST(AST ast, AST parentNode, ASTNodeType typeExprNode, ASTNodeType typeChildNode,
                                        String exprNodeLexeme, String childNodeLexeme, ArrayList<Integer> pathToTokenParent) {
        AST newNode, newExprNode;
        newExprNode = new AST(typeExprNode, exprNodeLexeme, parentNode, null);
        newNode = new AST(typeChildNode, childNodeLexeme, newExprNode, null);
        parentNode.add(newExprNode);
        pathToTokenParent.add(parentNode.getChildren().size() - 1);
        ast.addByPath(newNode, pathToTokenParent);
    }

    // обход дерева
}
