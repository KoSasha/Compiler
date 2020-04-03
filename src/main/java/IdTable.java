import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor@AllArgsConstructor
@Setter@Getter
public class IdTable {
    private Map<String, String> idTable;

//    @JsonIgnore
//    private String lexeme;

    @JsonIgnore
    private Integer level;

    @JsonIgnore
    private Character subLevel;

    public void formATable(AST ast) {
        if (ast != null) {
            if (ast.getChildren() != null) {
                for (AST astChild: ast.getChildren()) {
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
                if (ast.getParent().getParent() != null &&
                        (ast.getParent().getParent().getNodeType() == ASTNodeType.PHRASEMAINFUNCTIONDEFINITION ||
                                ast.getParent().getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION ||
                                ast.getParent().getParent().getNodeType() == ASTNodeType.EXPRESSIONVARIABLEDEFINITION ||
                                ast.getParent().getParent().getNodeType() == ASTNodeType.SENTENCEFUNCTIONDEFINITIONPARAM ||
                                ast.getParent().getParent().getNodeType() == ASTNodeType.MUTABLEDEFINITION ||
                                ast.getParent().getParent().getNodeType() == ASTNodeType.PHRASEFOR)) {
                    String lvl = this.getLevel().toString() + this.getSubLevel().toString();
//                    this.setLexeme(ast.getLexeme());
                    this.idTable.put(ast.getLexeme(), lvl);
                }
            }
        }
    }

    public String toJSON(String address_to) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(address_to), this);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
