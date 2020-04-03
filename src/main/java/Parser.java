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
                        nextToken.getType() == TokenClass.STRING ||
                        nextToken.getType() == TokenClass.LSQUAREBRACKET) {
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
                            if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
                                return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                        ">: \"" + currentToken.getLexeme() + "\" ожидается тип параметра";
                            } else {
                                newExprNode = new AST(ASTNodeType.SENTENCEFUNCTIONPARAM, "expression_function_param", parentNode, null);
                                newNode = new AST(ASTNodeType.LPARENTHESIS, currentToken.getLexeme(), parentNode, null);
                            }
                        } else if (nextToken.getType() == TokenClass.ID ||
                                nextToken.getType() == TokenClass.MUTABLE) {
                            if (parentNode.getParent() != null && parentNode.getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
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
                    } else if (parentNode.getNodeType() == ASTNodeType.POINTFUNCTION) {
                        newExprNode = new AST(ASTNodeType.SENTENCEINDEX, "index", parentNode, null);
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
                if (nextToken.getType() == TokenClass.VARIABLEDEFINITION ||
                        nextToken.getType() == TokenClass.ASSERTMACRO ||
                        nextToken.getType() == TokenClass.PRINTLNMACRO ||
                        nextToken.getType() == TokenClass.WHILE ||
                        nextToken.getType() == TokenClass.IF ||
                        nextToken.getType() == TokenClass.FOR ||
                        nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.RBRACE) {
                    boolean added = addAOpeningTokenToTheAST(ast, parentNode, ASTNodeType.BODY, ASTNodeType.valueOf(currentToken.getType().name()),
                            "body", currentToken.getLexeme(), "PHRASE", pathToTokenParent);
                    if (!added) {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" скобка ожидается в составе цикла или определения функции";
                    }
                }
                  else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" скобка не у дел";
                }
                break;

            case RBRACE:
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
                break;

            case RPARENTHESIS:
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
                break;

            case RSQUAREBRACKET:
                if (nextToken.getType() == TokenClass.RPARENTHESIS ||
                        nextToken.getType() == TokenClass.SEMICOLON ||
                        nextToken.getType() == TokenClass.LBRACE) {
                    if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION ||
                            parentNode.getNodeType() == ASTNodeType.ARRAYINDEX)) {
                        pathToTokenParent.remove(pathToTokenParent.size() - 1);
                        ast.addByPath(new AST(ASTNodeType.RSQUAREBRACKET, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                    } else {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" не у дел";
                    }
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается в качестве параметра функции";
                }
                break;

            case LSQUAREBRACKET:
                if (nextToken.getType() == TokenClass.INT ||
                        nextToken.getType() == TokenClass.FLOAT) {
                    if (parentNode != null && parentNode.getNodeType() == ASTNodeType.BINDING) {
                        ast.addByPath(new AST(ASTNodeType.LSQUAREBRACKET, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                        ast.addByPath(new AST(ASTNodeType.ARRAYDECLARATION, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                        pathToTokenParent.add(parentNode.getChildren().size() - 1);
                    }
                } else if (nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE ||
                        nextToken.getType() == TokenClass.ID) {
                    ast.addByPath(new AST(ASTNodeType.LSQUAREBRACKET, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                    ast.addByPath(new AST(ASTNodeType.ARRAYINDEX, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                    pathToTokenParent.add(parentNode.getChildren().size() - 1);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается тип данных массива";
                }
                break;

            case SEMICOLON:
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
                            ast.addByPath(new AST(ASTNodeType.SEMICOLON, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                        } else {
                            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" ожидается объявление массива";
                        }
                    } else {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" не у дел";
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
            case OPERATOREQUALITY:
            case OPERATORINEQUALITY:
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
                }
                break;

            case SINTVARIABLE:
            case INTVARIABLE:
            case FLOATVARIABLE:
            case HEXVARIABLE:
            case OCTALVARIABLE:
            case BINARYVARIABLE:
                if (nextToken.getType() == TokenClass.SEMICOLON ||
                        nextToken.getType() == TokenClass.LBRACE) {
                    newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null);
                    ast.addByPath(newNode, pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.COMMA) {
                    ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null),
                            pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.ROUND) {
                    if (parentNode != null && parentNode.getNodeType() == ASTNodeType.CYCLE) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.ROUND, ASTNodeType.valueOf(currentToken.getType().name()),
                                "expression_round", currentToken.getLexeme(), pathToTokenParent);
                    } else {
                        return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" ожидается в цикле";
                    }
                } else if (nextToken.getType() == TokenClass.RSQUAREBRACKET) {
                    if (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION ||
                            parentNode.getNodeType() == ASTNodeType.ARRAYINDEX) {
                        ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null)
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
                                nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
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
                        ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null), pathToTokenParent);
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
                            ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                        } else {
                            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                        }
                    }
                }
                break;

            case STRINGVARIABLE:
            case ARRAY:
                if (nextToken.getType() == TokenClass.SEMICOLON ||
                        nextToken.getType() == TokenClass.COMMA ||
                        nextToken.getType() == TokenClass.RPARENTHESIS) {
                    if (parentNode.getNodeType() != ASTNodeType.BODY) {
                        newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null);
                        ast.addByPath(newNode, pathToTokenParent);
                    } else {
                        return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                ">: \"" + currentToken.getLexeme() + "\" одинокая переменная";
                    }
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" не у дел";
                }
                break;

            case STRING:
                if (nextToken.getType() == TokenClass.COMMA ||
                        nextToken.getType() == TokenClass.RPARENTHESIS) {
                    ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null),
                            pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" не у дел";
                }
                break;

            case INT:
            case FLOAT:
                if (nextToken.getType() == TokenClass.OPERATORASSIGNMENT) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.OPERATORASSIGNMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                            nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.COMMA ||
                        nextToken.getType() == TokenClass.LBRACE ||
                        nextToken.getType() == TokenClass.RPARENTHESIS) {
                    ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null),
                            pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.SEMICOLON) {
                    if (parentNode.getNodeType() == ASTNodeType.ARRAYDECLARATION) {
                        ast.addByPath(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null),
                                pathToTokenParent);
                    }
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" одинокий тип данных";
                }
                break;

            case ID:
                if (nextToken.getType() == TokenClass.SEMICOLON) {
                    if (parentNode.getNodeType() == ASTNodeType.EXPRESSIONASSIGNMENT ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORASSIGNMENT ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORMULTIPLICATION ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORDIFFERENCE ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORSUMMING ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORDIVISION ||
                            parentNode.getNodeType() == ASTNodeType.OPERATORMODULO ||
                            parentNode.getNodeType() == ASTNodeType.EXPRESSIONRETURN) {
                        newNode = new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null);
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
                                    "expression_type_binding", currentToken.getLexeme(), pathToTokenParent);
                        } else {
                            return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" связывание переменной возможно только при ее определении";
                        }
                    }
                } else if (nextToken.getType() == TokenClass.COMMA) {
                    ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.POINT) {
                    if (parentNode.getNodeType() == ASTNodeType.OPERATOREQUALITY) {
                        ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                    } else if (parentNode.getNodeType() == ASTNodeType.PHRASEIF) {
                        newExprNode = new AST(ASTNodeType.CONDITIONIF, "expression_condition_if", parentNode, null);
                        ast.addByPath(newExprNode, pathToTokenParent);
                        pathToTokenParent.add(parentNode.getChildren().size() - 1);
                        parentNode = parentNode.getChildren().get(parentNode.getChildren().size() - 1);
                        addANodeToTheAST(ast, parentNode, ASTNodeType.OPERATOREQUALITY, ASTNodeType.valueOf(currentToken.getType().name()),
                                "==", currentToken.getLexeme(), pathToTokenParent);
                    } else if (parentNode.getNodeType() == ASTNodeType.CONDITIONIF) {
                        pathToTokenParent.add(parentNode.getChildren().size() - 1);
                        parentNode = parentNode.getChildren().get(0);
                        ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                    }
                } else if (nextToken.getType() == TokenClass.OPERATORINEQUALITY ||
                        nextToken.getType() == TokenClass.OPERATOREQUALITY ||
                        nextToken.getType() == TokenClass.OPERATORMORE ||
                        nextToken.getType() == TokenClass.OPERATORLESS) {
                    if (parentNode != null) {
                        if (parentNode.getNodeType() == ASTNodeType.PHRASEIF) {
                            addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONIF, ASTNodeType.valueOf(nextToken.getType().name()),
                                    "expression_condition_if", nextToken.getLexeme(), pathToTokenParent);
                            pathToTokenParent.add(0);
                            parentNode = ast.searchByPath(pathToTokenParent);
                            parentNode.add(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null));
                        } else if (parentNode.getNodeType() == ASTNodeType.PHRASEWHILE) {
                            addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONWHILE, ASTNodeType.valueOf(nextToken.getType().name()),
                                    "expression_condition_while", nextToken.getLexeme(), pathToTokenParent);
                            pathToTokenParent.add(0);
                            parentNode = ast.searchByPath(pathToTokenParent);
                            parentNode.add(new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null));
                        } else if (parentNode.getNodeType() == ASTNodeType.SENTENCEASSERTPARAM) {
                            addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                                    nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
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
                                "expression_assignment", currentToken.getLexeme(), pathToTokenParent);
                    } else {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.valueOf(nextToken.getType().name()), ASTNodeType.valueOf(currentToken.getType().name()),
                                nextToken.getLexeme(), currentToken.getLexeme(), pathToTokenParent);
                    }
                } else if (nextToken.getType() == TokenClass.IN) {
                    if (parentNode != null && (parentNode.getNodeType() == ASTNodeType.PHRASEFOR ||
                            (parentNode.getNodeType() == ASTNodeType.MUTABLEDEFINITION))) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.CONDITIONFOR, ASTNodeType.valueOf(currentToken.getType().name()),
                                "expression_condition_for", currentToken.getLexeme(), pathToTokenParent);
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
                        ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
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
                            ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                        } else {
                            return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                                    ">: \"" + currentToken.getLexeme() + "\" нет открывающей скобки";
                        }
                    }
                } else if (nextToken.getType() == TokenClass.LSQUAREBRACKET) {
                        addANodeToTheAST(ast, parentNode, ASTNodeType.ARRAYELEMENT, ASTNodeType.valueOf(currentToken.getType().name()),
                                "expression_array_element", currentToken.getLexeme(), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.RSQUAREBRACKET) {
                    ast.addByPath(new AST(ASTNodeType.LSQUAREBRACKET, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                } else if (nextToken.getType() == TokenClass.LBRACE) {
                    ast.addByPath(new AST(ASTNodeType.ID, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + currentToken.getString().toString() + "," + currentToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" не у дел";
                }
                break;

            case COMMA:
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
                            newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode, null);
                            ast.addByPath(newNode, pathToTokenParent);
                            break;
                        }
                    }
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается перечисление параметров";
                }
                break;

            case POINT:
                if (nextToken.getType() == TokenClass.CHAR) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.POINTFUNCTION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_point_function", currentToken.getLexeme(), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается после идентификатора";
                }
                break;

            case CHAR:
                if (nextToken.getType() == TokenClass.LPARENTHESIS) {
                    ast.addByPath(new AST(ASTNodeType.CHAR, currentToken.getLexeme(), parentNode, null), pathToTokenParent);
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: \"" + currentToken.getLexeme() + "\" ожидается скобка";
                }
                break;

            case ROUND:
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.INTVARIABLE ||
                        nextToken.getType() == TokenClass.SINTVARIABLE ||
                        nextToken.getType() == TokenClass.FLOATVARIABLE ||
                        nextToken.getType() == TokenClass.HEXVARIABLE ||
                        nextToken.getType() == TokenClass.OCTALVARIABLE ||
                        nextToken.getType() == TokenClass.BINARYVARIABLE) {
                    break;
                } else {
                    return "ERROR;LOC<" + nextToken.getString().toString() + "," + nextToken.getPosition().toString() +
                            ">: после " + currentToken.getLexeme() + " ожидается возвращаемое значение или идентификатор";
                }

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
                if (nextToken.getType() == TokenClass.ID ||
                        nextToken.getType() == TokenClass.MUTABLE) {
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

            case FUNCTIONID:
            case MAIN:
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
                } else if (nextToken.getType() == TokenClass.MAIN) {
                    addANodeToTheAST(ast, parentNode, ASTNodeType.PHRASEMAINFUNCTIONDEFINITION, ASTNodeType.valueOf(currentToken.getType().name()),
                            "expression_main_function_definition", currentToken.getLexeme(), pathToTokenParent);
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

    public static boolean addAOpeningTokenToTheAST(AST ast, AST parentNode, ASTNodeType typeExprNode, ASTNodeType typeChildNode,
                                                   String exprNodeLexeme, String childNodeLexeme, String startWith, ArrayList<Integer> pathToTokenParent) {
        AST newNode, newExprNode;
        int size = pathToTokenParent.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!parentNode.getNodeType().name().startsWith(startWith)) {
                parentNode = parentNode.getParent();
                pathToTokenParent.remove(i);
            } else {
                newExprNode = new AST(typeExprNode, exprNodeLexeme, parentNode, null);
                newNode = new AST(typeChildNode, childNodeLexeme, parentNode, null);
                parentNode.add(newNode);
                parentNode.add(newExprNode);
                pathToTokenParent.add(parentNode.getChildren().size() - 1);
                return true;
            }
        }
        return false;
    }

    public static boolean addAClosingTokenToTheAST(AST ast, AST parentNode, Token currentToken, String startWith, ArrayList<Integer> pathToTokenParent) {
        AST newNode;
        int size = pathToTokenParent.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!parentNode.getNodeType().name().startsWith(startWith)) {
                parentNode = parentNode.getParent();
                pathToTokenParent.remove(i);
            } else {
                newNode = new AST(ASTNodeType.valueOf(currentToken.getType().name()), currentToken.getLexeme(), parentNode.getParent(), null);
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
