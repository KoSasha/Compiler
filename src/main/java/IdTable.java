import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.*;
import java.util.*;

@NoArgsConstructor@AllArgsConstructor
@Setter@Getter
public class IdTable {
    private Map<String, String> idTable;

    @JsonIgnore
    private Integer level;

    @JsonIgnore
    private Character subLevel;

    @JsonIgnore
    private ArrayList<IdDeclarationDescription> idDeclarationDescriptions;

    public void formATable(AST ast) {
        if (ast != null) {
            String lvl;
            if (ast.getChildren() != null) {
                for (AST astChild : ast.getChildren()) {
                    formATable(astChild);
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
            } else if (ast.getNodeType() == ASTNodeType.ID ||
                    ast.getNodeType() == ASTNodeType.FUNCTIONID ||
                    ast.getNodeType() == ASTNodeType.MAIN) {
                AST grandfather = ast.getParent().getParent();
                if (grandfather != null) {
                    lvl = this.getLevel().toString() + this.getSubLevel().toString();
                    if (grandfather.getNodeType() == ASTNodeType.EXPRESSIONVARIABLEDEFINITION) {
                        addToIdTableFromExpressionVariableDefinition(ast, lvl);
                    } else if (grandfather.getNodeType() == ASTNodeType.MUTABLEDEFINITION) {
                        addToIdTableFromMutableDefinition(ast, lvl, grandfather);
                    } else if (grandfather.getNodeType() == ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM) {
                        addToIdTableFromSentenceFunctionParam(ast, lvl, grandfather);
                    } else if (grandfather.getNodeType() == ASTNodeType.PHRASEMAINFUNCTIONDEFINITION ||
                            grandfather.getNodeType() == ASTNodeType.PHRASEFOR) {
                        addToIdTable(ast, lvl);
                    } else if (grandfather.getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
                        addToIdTableFromPhraseFunctionDefinition(ast, lvl);
                    }
                }
            }
        }
    }

    public void addToIdTable(AST ast, String lvl) {
        idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.INT, ast.getNodeType(),
                ast.getLexeme(), lvl, 1, null, null));
        this.idTable.put(ast.getLexeme(), lvl);
        addDescriptionNode(ast, "i32", ASTNodeType.INT);
    }

    public void addToIdTableFromExpressionVariableDefinition(AST ast, String lvl) {
        AST sibling, cousin;
        ASTNodeType declarationType = null;
        sibling = ast.getParent().getChildren().get(1);
        String descriptionLexeme = null;
        if (sibling.getLexeme() != ast.getLexeme()) {
            if (sibling.getNodeType() == ASTNodeType.BINDING) {
                cousin = sibling.getChildren().get(1);
                if (cousin.getNodeType() == ASTNodeType.INT ||
                        cousin.getNodeType() == ASTNodeType.FLOAT) {
                    descriptionLexeme = cousin.getLexeme();
                    declarationType = cousin.getNodeType();
                    idDeclarationDescriptions.add(new IdDeclarationDescription(cousin.getNodeType(), ast.getNodeType(),
                            ast.getLexeme(), lvl, 1, cousin.getLexeme(), null));
                } else if (cousin.getNodeType() == ASTNodeType.OPERATORASSIGNMENT) {
                    descriptionLexeme = cousin.getChildren().get(0).getLexeme();
                    declarationType = cousin.getChildren().get(0).getNodeType();
                    idDeclarationDescriptions.add(new IdDeclarationDescription(cousin.getChildren().get(0).getNodeType(),
                            ast.getNodeType(), ast.getLexeme(), lvl, 1, cousin.getChildren().get(1).getLexeme(), null));
                }
            } else if (sibling.getNodeType() == ASTNodeType.STRINGVARIABLE) {
                descriptionLexeme = "&str";
                declarationType = ASTNodeType.STRING;
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.STRING,
                        ast.getNodeType(), ast.getLexeme(), lvl, sibling.getLexeme().length(), sibling.getLexeme(), null));
            } else if (sibling.getNodeType() == ASTNodeType.SINTVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.INTVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.HEXVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.OCTALVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.BINARYVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.ID) {
                descriptionLexeme = "i32";
                declarationType = ASTNodeType.INT;
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.INT,
                        ast.getNodeType(), ast.getLexeme(), lvl, 1, sibling.getLexeme(), null));
            } else if (sibling.getNodeType() == ASTNodeType.FLOATVARIABLE) {
                descriptionLexeme = "f64";
                declarationType = ASTNodeType.FLOAT;
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.FLOAT,
                        ast.getNodeType(), ast.getLexeme(), lvl, 1, sibling.getLexeme(), null));
            } else if (sibling.getNodeType() == ASTNodeType.ARRAY) {
                descriptionLexeme = "i32";
                declarationType = ASTNodeType.INT;
                Integer arraySize = sibling.getLexeme().split(",").length;
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.INT,
                        ASTNodeType.ARRAY, ast.getLexeme(), lvl, arraySize, sibling.getLexeme(), null));
            }
            this.idTable.put(ast.getLexeme(), lvl);
            addDescriptionNode(ast, descriptionLexeme, declarationType);
        }
    }

    public void addToIdTableFromSentenceFunctionParam(AST ast, String lvl, AST grandfather) {
        AST sibling, cousin;
        ASTNodeType declarationType = null;
        String descriptionLexeme = null;
        sibling = ast.getParent().getChildren().get(1);
        cousin = sibling.getChildren().get(1);
        String functionName = grandfather.getParent().getChildren().get(0).getChildren().get(0).getLexeme();
        IdDeclarationDescription param = null;
        if (cousin.getNodeType() == ASTNodeType.STRING ||
                cousin.getNodeType() == ASTNodeType.INT ||
                cousin.getNodeType() == ASTNodeType.FLOAT) {
            descriptionLexeme = cousin.getLexeme();
            declarationType = cousin.getNodeType();
            param = new IdDeclarationDescription(cousin.getNodeType(), ast.getNodeType(), ast.getLexeme(), lvl, 1,
                    null, null);
            this.idDeclarationDescriptions.add(param);
        } else if (cousin.getNodeType() == ASTNodeType.LSQUAREBRACKET) {
            descriptionLexeme = sibling.getChildren().get(2).getChildren().get(0).getLexeme();
            declarationType = sibling.getChildren().get(2).getChildren().get(0).getNodeType();
            String arraySize = cousin.getParent().getChildren().get(2).getChildren().get(2).getLexeme();
            param = new IdDeclarationDescription(sibling.getChildren().get(2).getChildren().get(0).getNodeType(),
                    ASTNodeType.ARRAY, ast.getLexeme(), lvl, Integer.parseInt(arraySize), null, null);
            this.idDeclarationDescriptions.add(param);
        }
        for (IdDeclarationDescription idDeclarationDescription: this.idDeclarationDescriptions) {
            if (idDeclarationDescription.getType() == ASTNodeType.FUNCTIONID &&
                    idDeclarationDescription.getLexeme().equals(functionName)) {
                idDeclarationDescription.getFunctionParam().add(param);
                idDeclarationDescription.setCount(idDeclarationDescription.getCount() + 1);
                break;
            }
        }
        this.idTable.put(ast.getLexeme(), lvl);
        addDescriptionNode(ast, descriptionLexeme, declarationType);
    }

    public void addToIdTableFromPhraseFunctionDefinition(AST ast, String lvl) {
        int size = ast.getParent().getChildren().size();
        AST sibling =  ast.getParent().getChildren().get(size - 1);
        idDeclarationDescriptions.add(new IdDeclarationDescription(sibling.getNodeType(), ast.getNodeType(),
                ast.getLexeme(), lvl, 0, null, new ArrayList<>()));
        this.idTable.put(ast.getLexeme(), lvl);
        addDescriptionNode(ast, sibling.getLexeme(), sibling.getNodeType());
    }

    public void addToIdTableFromMutableDefinition(AST ast, String lvl, AST grandfather) {
        if (ast.getParent().getNodeType() == ASTNodeType.CONDITIONFOR) {
            addToIdTable(ast, lvl);
        } else if (grandfather.getParent().getNodeType() == ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM) {
            addToIdTableFromSentenceFunctionParam(ast, lvl, grandfather.getParent());
        } else {
            addToIdTableFromExpressionVariableDefinition(ast, lvl);
        }
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

    public String toJSON(String address_to) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(address_to), this);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
