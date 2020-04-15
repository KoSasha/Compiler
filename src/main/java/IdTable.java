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
                ast.getLexeme(), lvl, null));
        this.idTable.put(ast.getLexeme(), lvl);
    }

    public void addToIdTableFromExpressionVariableDefinition(AST ast, String lvl) {
        AST sibling, cousin;
        sibling = ast.getParent().getChildren().get(1);
        if (sibling.getLexeme() != ast.getLexeme()) {
            if (sibling.getNodeType() == ASTNodeType.BINDING) {
                cousin = sibling.getChildren().get(1);
                if (cousin.getNodeType() == ASTNodeType.INT ||
                        cousin.getNodeType() == ASTNodeType.FLOAT) {
                    idDeclarationDescriptions.add(new IdDeclarationDescription(cousin.getNodeType(), ast.getNodeType(),
                            ast.getLexeme(), lvl, null));
                } else if (cousin.getNodeType() == ASTNodeType.OPERATORASSIGNMENT) {
                    idDeclarationDescriptions.add(new IdDeclarationDescription(cousin.getChildren().get(0).getNodeType(),
                            ast.getNodeType(), ast.getLexeme(), lvl, null));
                }
            } else if (sibling.getNodeType() == ASTNodeType.STRINGVARIABLE) {
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.STRING,
                        ast.getNodeType(), ast.getLexeme(), lvl, null));
            } else if (sibling.getNodeType() == ASTNodeType.SINTVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.INTVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.HEXVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.OCTALVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.BINARYVARIABLE ||
                    sibling.getNodeType() == ASTNodeType.ID) {
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.INT,
                        ast.getNodeType(), ast.getLexeme(), lvl, null));
            } else if (sibling.getNodeType() == ASTNodeType.FLOATVARIABLE) {
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.FLOAT,
                        ast.getNodeType(), ast.getLexeme(), lvl, null));
            } else if (sibling.getNodeType() == ASTNodeType.ARRAY) {
                idDeclarationDescriptions.add(new IdDeclarationDescription(ASTNodeType.INT,
                        ASTNodeType.ARRAY, ast.getLexeme(), lvl, null));
            }
            this.idTable.put(ast.getLexeme(), lvl);
        }
    }

    public void addToIdTableFromSentenceFunctionParam(AST ast, String lvl, AST grandfather) {
        AST sibling, cousin;
        sibling = ast.getParent().getChildren().get(1);
        cousin = sibling.getChildren().get(1);
        String functionName = grandfather.getParent().getChildren().get(0).getLexeme();
        System.out.println(functionName);
        IdDeclarationDescription param = null;
        if (cousin.getNodeType() == ASTNodeType.STRING ||
                cousin.getNodeType() == ASTNodeType.INT ||
                cousin.getNodeType() == ASTNodeType.FLOAT) {
            param = new IdDeclarationDescription(cousin.getNodeType(), ast.getNodeType(), ast.getLexeme(), lvl, null);
            this.idDeclarationDescriptions.add(param);
        } else if (cousin.getNodeType() == ASTNodeType.LSQUAREBRACKET) {
            param = new IdDeclarationDescription(sibling.getChildren().get(2).getChildren().get(0).getNodeType(),
                    ASTNodeType.ARRAY, ast.getLexeme(), lvl, null);
            this.idDeclarationDescriptions.add(param);
        }
        for (IdDeclarationDescription idDeclarationDescription: this.idDeclarationDescriptions) {
            if (idDeclarationDescription.getType() == ASTNodeType.FUNCTIONID &&
                    idDeclarationDescription.getLexeme().equals(functionName)) {
                idDeclarationDescription.getFunctionParam().add(param);
                break;
            }
        }
        this.idTable.put(ast.getLexeme(), lvl);
    }

    public void addToIdTableFromPhraseFunctionDefinition(AST ast, String lvl) {
        int size = ast.getParent().getChildren().size();
        AST sibling =  ast.getParent().getChildren().get(size - 1);
        idDeclarationDescriptions.add(new IdDeclarationDescription(sibling.getNodeType(), ast.getNodeType(),
                ast.getLexeme(), lvl, new ArrayList<>()));
        this.idTable.put(ast.getLexeme(), lvl);
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

    public String toJSON(String address_to) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(address_to), this);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
