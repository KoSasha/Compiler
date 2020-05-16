import lombok.*;

import java.util.ArrayList;

@AllArgsConstructor@NoArgsConstructor
@Setter@Getter
public class Sema {
    private Integer level;

    private Character SubLevel;

    public String analyze(AST ast, IdTable idTable) {
        if (ast != null) {
            String lvl, log;
            if (ast.getChildren() != null) {
                for (AST astChild : ast.getChildren()) {
                    analyze(astChild, idTable);
                }
            } else if (ast.getNodeType() == ASTNodeType.LPARENTHESIS &&
                    ast.getParent().getParent() != null &&
                    ast.getParent().getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
                this.setLevel(this.getLevel() + 1);
                char sublvl = (char) (this.getSubLevel().charValue() + 1);
                this.setSubLevel(sublvl);
            } else if (ast.getNodeType() == ASTNodeType.LBRACE &&
                    (ast.getParent().getNodeType() != ASTNodeType.PHRASEFUNCTIONDEFINITION)) {
                this.setLevel(this.getLevel() + 1);
                this.setSubLevel('a');
            } else if (ast.getNodeType() == ASTNodeType.RBRACE) {
                this.setLevel(this.getLevel() - 1);
            } else if (ast.getNodeType() == ASTNodeType.ID) {
                log = this.idAnalyze(ast, idTable);
                if (log != null) {
                    System.out.println(log);
                    System.exit(0);
                }
                return log;
            } else if (ast.getNodeType() == ASTNodeType.FUNCTIONID) {
                log = this.functionIdAnalyze(ast, idTable);
                if (log != null) {
                    System.out.println(log);
                    System.exit(0);
                }
                return log;
            } else if (ast.getNodeType() == ASTNodeType.INTVARIABLE ||
                    ast.getNodeType() == ASTNodeType.SINTVARIABLE ||
                    ast.getNodeType() == ASTNodeType.HEXVARIABLE ||
                    ast.getNodeType() == ASTNodeType.OCTALVARIABLE ||
                    ast.getNodeType() == ASTNodeType.BINARYVARIABLE ||
                    ast.getNodeType() == ASTNodeType.FLOATVARIABLE ||
                    ast.getNodeType() == ASTNodeType.STRINGVARIABLE ||
                    ast.getNodeType() == ASTNodeType.ARRAY) {
                log = this.functionNumericalVariableAnalyze(ast, idTable);
                if (log != null) {
                    System.out.println(log);
                    System.exit(0);
                }
                return log;
            }
        }
        return null;
    }

    public String idAnalyze(AST ast, IdTable idTable) {
        if (ast.getParent().getNodeType().name().startsWith("DESCRIPTION")) {
            return null;
        }
        if (this.getLevel() == 0) {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: переменная '" + ast.getLexeme() + "' не у дел";
        }
        for (IdDeclarationDescription description: idTable.getIdDeclarationDescriptions()) {
            if (description.getLexeme().equals(ast.getLexeme()) && (description.getType() == ASTNodeType.ARRAY ||
                    description.getType() == ASTNodeType.ID)) {
                String lv = description.getLevel().substring(0, 1);
                String subLv = description.getLevel().substring(1, 2);
                if (Integer.parseInt(lv) <= this.getLevel() &&
                        Character.getNumericValue(this.getSubLevel()) <= Character.getNumericValue(subLv.charAt(0))) {
                    String descriptionLexeme = null;
                    if (description.getDataType() == ASTNodeType.INT) {
                        descriptionLexeme = "i32";
                    } else if (description.getDataType() == ASTNodeType.FLOAT) {
                        descriptionLexeme = "f64";
                    } else if (description.getDataType() == ASTNodeType.STRING) {
                        descriptionLexeme = "&str";
                    }
                    if (ast.getParent().getNodeType().name().startsWith("OPERATOR")) {
                        return checkTheTypeOfTheOperand(ast, description, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("EXPRESSIONASSIGNMENT")) {
                        return checkTheTypeOfTheAssignment(ast, description, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("SENTENCEPRINTLNPARAM")) {
                        return checkTheTypeOfThePrintlnParam(ast, description, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("SENTENCEINDEX") ||
                            ast.getParent().getNodeType().name().startsWith("ARRAYINDEX")) {
                        return checkTheTypeOfTheIndex(ast, description, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("ARRAYELEMENT")) {
                        return checkTheTypeOfTheArrayElement(ast, description, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("EXPRESSIONRETURN")) {
                        return checkTheTypeOfTheReturn(ast, description, idTable, descriptionLexeme);
                    } else if (ast.getParent().getNodeType().name().startsWith("SENTENCEFUNCTIONPARAM")) {
                        return checkTheTypeOfTheReturn(ast, description, idTable, descriptionLexeme);
                    }
                    return null;
                }
            }
        }
        return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: переменная '" + ast.getLexeme() + "' не была объявлена";
    }

    public String checkTheTypeOfTheOperand(AST ast, IdDeclarationDescription description, String descriptionLexeme) {
        String declarationNodeType = "DESCRIPTION" + description.getDataType().name();
        ASTNodeType declarationType = ASTNodeType.valueOf(declarationNodeType);
        if (ast.getParent().getChildren().get(0).equals(ast)) {
            if (ast.getParent().getParent() != null && ast.getParent().getParent().getNodeType() == ASTNodeType.EXPRESSIONASSIGNMENT) {

                if (ast.getParent().getParent().getChildren().get(0).getNodeType() == declarationType) {

                    addDescriptionNode(ast, descriptionLexeme, description.getDataType());
                    return null;
                } else {
                    return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' несоответствующий тип присваивания";
                }
            } else {
                addDescriptionNode(ast, descriptionLexeme, description.getDataType());
                return null;
            }
        } else if (ast.getParent().getChildren().get(0).getNodeType() == declarationType) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: операнд '" + ast.getLexeme() + "' несоответствующего типа";
        }
    }

    public String checkTheTypeOfTheAssignment(AST ast, IdDeclarationDescription description, String descriptionLexeme) {
        String declarationNodeType = "DESCRIPTION" + description.getDataType().name();
        ASTNodeType declarationType = ASTNodeType.valueOf(declarationNodeType);
        if (ast.getParent().getChildren().get(0).equals(ast)) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else if (ast.getParent().getChildren().get(0).getNodeType() == declarationType) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: операнд '" + ast.getLexeme() + "' несоответствующего типа";
        }
    }

    public String checkTheTypeOfThePrintlnParam(AST ast, IdDeclarationDescription description, String descriptionLexeme) {
        if (ast.getParent().getChildren().get(0).getNodeType() == ASTNodeType.DESCRIPTIONSTRING) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: некорректные параметры для макроса 'println'";
        }
    }

    public String checkTheTypeOfTheIndex(AST ast, IdDeclarationDescription description, String descriptionLexeme) {
        if (description.getDataType() == ASTNodeType.INT) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' некорректный тип индекса";
        }
    }

    public String checkTheTypeOfTheArrayElement(AST ast, IdDeclarationDescription description, String descriptionLexeme) {
        String declarationNodeType = "DESCRIPTION" + description.getDataType().name();
        ASTNodeType declarationType = ASTNodeType.valueOf(declarationNodeType);
        if (ast.getParent().getParent().getNodeType() == ASTNodeType.EXPRESSIONASSIGNMENT) {
            if (ast.getParent().getParent().getChildren().get(0).getNodeType() == declarationType) {
                addDescriptionNode(ast, descriptionLexeme, description.getDataType());
                return null;
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() +
                        "' присваивание несоответствующего типа";
            }
        } else if (ast.getParent().getParent().getNodeType().name().startsWith("OPERATORASSIGNMENT")) {
            if (ast.getParent().getParent().getChildren().get(0).getNodeType() == description.getDataType()) {
                addDescriptionNode(ast, descriptionLexeme, description.getDataType());
                return null;
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: операнд '" + ast.getLexeme() +
                        "' несоответствующего типа";
            }
        } else if (ast.getParent().getParent().getNodeType().name().startsWith("OPERATOR")) {
            if (ast.getParent().getParent().getChildren().get(0).getNodeType() == declarationType) {
                addDescriptionNode(ast, descriptionLexeme, description.getDataType());
                return null;
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: операнд '" + ast.getLexeme() +
                        "' несоответствующего типа";
            }
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: операнд '" + ast.getLexeme() + "' несоответствующего типа";
        }
    }

    public String checkTheTypeOfTheReturn(AST ast, IdDeclarationDescription description, IdTable idTable, String descriptionLexeme) {
        ASTNodeType functionReturnType = null;
        for (IdDeclarationDescription descriptionFunction: idTable.getIdDeclarationDescriptions()) {
            if (descriptionFunction.getType() == ASTNodeType.FUNCTIONID) {
                functionReturnType = descriptionFunction.getDataType();
                break;
            }
        }
        if (functionReturnType != null && functionReturnType == description.getDataType()) {
            addDescriptionNode(ast, descriptionLexeme, description.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' некорректный возвращаемый тип";
        }
    }

    public String functionIdAnalyze(AST ast, IdTable idTable) {
        if (ast.getParent().getNodeType().name().startsWith("DESCRIPTION")) {
            return null;
        }
        for (IdDeclarationDescription description: idTable.getIdDeclarationDescriptions()) {
            if (description.getLexeme().equals(ast.getLexeme()) && description.getType() == ASTNodeType.FUNCTIONID) {
                String lv = description.getLevel().substring(0, 1);
                String subLv = description.getLevel().substring(1, 2);
                if (Integer.parseInt(lv) <= this.getLevel() &&
                        Character.getNumericValue(this.getSubLevel()) <= Character.getNumericValue(subLv.charAt(0))) {
                    String descriptionLexeme = null;
                    if (description.getDataType() == ASTNodeType.INT) {
                        descriptionLexeme = "i32";
                    } else if (description.getDataType() == ASTNodeType.FLOAT) {
                        descriptionLexeme = "f64";
                    } else if (description.getDataType() == ASTNodeType.STRING) {
                        descriptionLexeme = "&str";
                    }
                    if (ast.getParent().getParent().getChildren().get(0).getNodeType() == description.getDataType()) {
                        return checkTheParametersForCorrectness(ast, description, descriptionLexeme, idTable);
                    } else {
                        return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() +
                                "' тип возвращаемого функцией значения не соответствует переменной присваивания";
                    }
                }
            }
        }
        return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() +
                "' функция не была объявлена";
    }

    public String checkTheParametersForCorrectness(AST ast, IdDeclarationDescription description, String descriptionLexeme, IdTable idTable) {
        AST sentenceParam = ast.getParent().getChildren().get(2);
        String log = null;
        if (sentenceParam.getNodeType() == ASTNodeType.SENTENCEFUNCTIONPARAM) {
            int size = sentenceParam.getChildren().size();
            int numberOfParam = size - (size / 2);
            if (numberOfParam == description.getFunctionParam().size()) {
                numberOfParam = 0;
                for (IdDeclarationDescription param: description.getFunctionParam()) {
                    if (sentenceParam.getChildren().get(numberOfParam).getNodeType() == ASTNodeType.ID) {
                        log = checkIdParam(sentenceParam.getChildren().get(numberOfParam), param, descriptionLexeme, idTable);

                    } else {
                        log = checkVariableParam(sentenceParam.getChildren().get(numberOfParam), param, descriptionLexeme, idTable);
                    }
                    if (log != null) {
                        return log;
                    }
                    numberOfParam += 2;
                }
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: количество передаваемых функции '" + ast.getLexeme() +
                        "' параметров не соответствует объявленному";
            }
        } else if (sentenceParam.getNodeType() == ASTNodeType.SENTENCEE) {
            if (description.getFunctionParam().size() != 0) {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: в функции '" + ast.getLexeme() +
                        "' ожидаются параметры";
            }
        }
        addDescriptionNode(ast, descriptionLexeme, description.getDataType());
        return null;
    }

    public String checkIdParam(AST ast, IdDeclarationDescription param, String descriptionLexeme, IdTable idTable) {
        for (IdDeclarationDescription descriptionParam: idTable.getIdDeclarationDescriptions()) {
            if (descriptionParam.getLexeme().equals(ast.getLexeme())) {
                String lv = descriptionParam.getLevel().substring(0, 1);
                String subLv = descriptionParam.getLevel().substring(1, 2);
                if (Integer.parseInt(lv) <= this.getLevel() &&
                        Character.getNumericValue(this.getSubLevel()) <= Character.getNumericValue(subLv.charAt(0))) {
                    if (param.getDataType() == descriptionParam.getDataType()) {
                        String paramTypeLexeme = null;
                        if (descriptionParam.getDataType() == ASTNodeType.INT) {
                            paramTypeLexeme = "i32";
                            if (descriptionParam.getType() == ASTNodeType.ARRAY) {
                                if (param.getCount() != descriptionParam.getCount()) {
                                    return  "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() +
                                            "' количество элементов массива, ожидаемое функцией, не соответствует передаваемому";
                                }
                            }
                        } else if (descriptionParam.getDataType() == ASTNodeType.FLOAT) {
                            paramTypeLexeme = "f64";
                        } else if (descriptionParam.getDataType() == ASTNodeType.STRING) {
                            paramTypeLexeme = "&str";
                        }
                        addDescriptionNode(ast, paramTypeLexeme, descriptionParam.getDataType());
                        return null;
                    } else {
                        return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: тип передаваемого параметра функции '" + ast.getLexeme() +
                                "' не соответствует объявленному";
                    }
                }
            }
        }
        return null;
    }

    public String checkVariableParam(AST ast, IdDeclarationDescription param, String descriptionLexeme, IdTable idTable) {
        if (param.getDataType() == ASTNodeType.INT &&
                (ast.getNodeType() == ASTNodeType.INTVARIABLE || ast.getNodeType() == ASTNodeType.SINTVARIABLE ||
                ast.getNodeType() == ASTNodeType.HEXVARIABLE || ast.getNodeType() == ASTNodeType.OCTALVARIABLE ||
                ast.getNodeType() == ASTNodeType.BINARYVARIABLE) ) {
            addDescriptionNode(ast, descriptionLexeme, param.getDataType());
            return null;
        } else if (ast.getNodeType().name().startsWith(param.getDataType().name())) {
            addDescriptionNode(ast, descriptionLexeme, param.getDataType());
            return null;
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: тип передаваемого параметра функции '" + ast.getLexeme() +
                    "' не соответствует объявленному";
        }
    }

    public String functionNumericalVariableAnalyze(AST ast, IdTable idTable) {
        if (ast.getParent().getNodeType().name().startsWith("DESCRIPTION")) {
            return null;
        }
        if (this.getLevel() == 0) {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: переменная '" + ast.getLexeme() + "' не у дел";
        }
        String descriptionLexeme = null;
        if (ast.getNodeType() == ASTNodeType.INTVARIABLE || ast.getNodeType() == ASTNodeType.SINTVARIABLE ||
                ast.getNodeType() == ASTNodeType.HEXVARIABLE || ast.getNodeType() == ASTNodeType.OCTALVARIABLE ||
                ast.getNodeType() == ASTNodeType.BINARYVARIABLE) {
            descriptionLexeme = "i32";
        } else if (ast.getNodeType() == ASTNodeType.ARRAY) {
            descriptionLexeme = "i32";
            String error = checkTypeOfTheArrayElement(ast);
            if (error != null) {
                return error;
            }
        } else if (ast.getNodeType() == ASTNodeType.FLOATVARIABLE) {
            descriptionLexeme = "f64";
        } else if (ast.getNodeType() == ASTNodeType.STRINGVARIABLE) {
            descriptionLexeme = "&str";
        }
        if (descriptionLexeme != null) {
            if (ast.getParent().getNodeType().name().startsWith("OPERATOR")) {
                return checkTheTypeOfTheVariableOperand(ast, descriptionLexeme);
            } else if (ast.getParent().getNodeType().name().startsWith("ROUND")) {
                return checkTheTypeOfTheRound(ast, descriptionLexeme);
            } else if (ast.getParent().getNodeType().name().startsWith("EXPRESSIONASSIGNMENT")) {
                return checkTheTypeOfTheAssignmentVariable(ast, descriptionLexeme);
            } else if (ast.getParent().getNodeType().name().startsWith("ARRAYDECLARATION")) {
                return checkTheTypeOfTheArrayDeclaration(ast, descriptionLexeme);
            } else if (ast.getParent().getNodeType().name().startsWith("ARRAYINDEX")) {
                return checkTheTypeOfTheArrayIndex(ast, descriptionLexeme);
            } else if (ast.getParent().getNodeType().name().startsWith("SENTENCEPRINTLNPARAM")) {
                return checkTheTypeOfTheVariablePrintlnParam(ast, descriptionLexeme);
            }
        }

        return null;
    }

    public String checkTypeOfTheArrayElement(AST ast) {
        String[] arrayElements = ast.getLexeme().substring(1, ast.getLexeme().length() - 1).split(", ");
        for (String element: arrayElements) {
            try {
                Integer.parseInt(element);
            } catch (NumberFormatException e) {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: тип элемента массива '" + element +
                "' не соответствует объявленному";
            }
        }
        return null;
    }

    public String checkTheTypeOfTheVariableOperand(AST ast, String descriptionLexeme) {
        AST sibling = ast.getParent().getChildren().get(0);
        if (sibling.getNodeType() == ASTNodeType.INT || sibling.getNodeType() == ASTNodeType.DESCRIPTIONINT) {
            if (descriptionLexeme.equals("i32")) {
                addDescriptionNode(ast, descriptionLexeme, ASTNodeType.INT);
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' ожидается целочисленное значение";
            }
        } else if (sibling.getNodeType() == ASTNodeType.FLOAT || sibling.getNodeType() == ASTNodeType.DESCRIPTIONFLOAT) {
            if (descriptionLexeme.equals("f64")) {
                addDescriptionNode(ast, descriptionLexeme, ASTNodeType.FLOAT);
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' ожидается значение c плавающей точкой";
            }
        } else if (sibling.getNodeType() == ASTNodeType.STRING || sibling.getNodeType() == ASTNodeType.DESCRIPTIONSTRING) {
            if (ast.getParent().getNodeType() == ASTNodeType.OPERATORASSIGNMENT) {
                if (descriptionLexeme.equals("&str")) {
                    addDescriptionNode(ast, descriptionLexeme, ASTNodeType.STRING);
                } else {
                    return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' ожидается строка";
                }
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' строка не может быть операндом в этой операции";
            }
        }
        return null;
    }

    public String checkTheTypeOfTheRound(AST ast, String descriptionLexeme) {
        if (descriptionLexeme.equals("i32") && ast.getNodeType() != ASTNodeType.ARRAY) {
            addDescriptionNode(ast, descriptionLexeme, ASTNodeType.INT);
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' недопустимый тип значения в цикле";
        }
        return null;
    }

    public String checkTheTypeOfTheAssignmentVariable(AST ast, String descriptionLexeme) {
        if (ast.getParent().getChildren().get(0).equals(ast)) {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' происходит присваивание значению";
        }
        AST sibling = ast.getParent().getChildren().get(0);
        if (sibling.getNodeType() == ASTNodeType.DESCRIPTIONINT) {
            if (descriptionLexeme.equals("i32")) {
                addDescriptionNode(ast, descriptionLexeme, ASTNodeType.INT);
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' несовместимое с целочисленной переменной значение";
            }
        } else if (sibling.getNodeType() == ASTNodeType.DESCRIPTIONFLOAT) {
            if (descriptionLexeme.equals("f64")) {
                addDescriptionNode(ast, descriptionLexeme, ASTNodeType.FLOAT);
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' несовместимое с переменной c плавающей точкой значение";
            }
        } else if (sibling.getNodeType() == ASTNodeType.DESCRIPTIONSTRING) {
            if (descriptionLexeme.equals("&str")) {
                addDescriptionNode(ast, descriptionLexeme, ASTNodeType.STRING);
            } else {
                return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' несовместимое со строковой переменной значение";
            }
        }
        return null;
    }

    public String checkTheTypeOfTheArrayDeclaration(AST ast, String descriptionLexeme) {
        if (descriptionLexeme.equals("i32")) {
            addDescriptionNode(ast, descriptionLexeme, ASTNodeType.INT);
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' недопустимый тип значения количества элементов в массиве";
        }
        return null;
    }

    public String checkTheTypeOfTheArrayIndex(AST ast, String descriptionLexeme) {
        if (descriptionLexeme.equals("i32")) {
            addDescriptionNode(ast, descriptionLexeme, ASTNodeType.INT);
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' недопустимый тип значения индекса массива";
        }
        return null;
    }

    public String checkTheTypeOfTheVariablePrintlnParam(AST ast, String descriptionLexeme) {
        if (descriptionLexeme.equals("&str") && ast.getParent().getChildren().get(0).equals(ast)) {
            addDescriptionNode(ast, descriptionLexeme, ASTNodeType.STRING);
        } else {
            return "TYPEERROR:<LINE_" + ast.getLine().toString() + ">: '" + ast.getLexeme() + "' недопустимый тип, передаваемый в макрос 'println'";
        }
        return null;
    }

    public void addDescriptionNode(AST ast, String descriptionLexeme, ASTNodeType definitionType) {
        String declarationNodeType = "DESCRIPTION" + definitionType.name();
        ASTNodeType declarationType = ASTNodeType.valueOf(declarationNodeType);
        AST idNode = new AST(ast.getNodeType(), ast.getLexeme(), ast.getLine(), ast, null);
        ast.setNodeType(declarationType);
        ast.setLexeme(descriptionLexeme);
        ast.setChildren(new ArrayList<>());
        ast.getChildren().add(idNode);
    }
}
