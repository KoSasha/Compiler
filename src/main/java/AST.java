import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.*;
import java.util.ArrayList;

@NoArgsConstructor@AllArgsConstructor
@Setter@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AST implements JSON {
    @JsonIgnore
    private ASTNodeType nodeType;

    private String lexeme;

    @JsonIgnore
    private Integer line;

    @JsonIgnore
    private AST parent;

    private ArrayList<AST> children;

    public void add(AST astNode) {
        if (astNode != null) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(astNode);
        }
    }

    public void addByPath(AST astNode, ArrayList<Integer> pathToTokenParent) {
        if (astNode != null) {
            if (pathToTokenParent != null) {
                searchByPath(pathToTokenParent).add(astNode);
            } else {
                add(astNode);
            }
        }
    }

    public AST searchByPath(ArrayList<Integer> pathToTokenParent) {
        if (pathToTokenParent != null && getChildren() != null) {
            AST node = this;
            for (Integer indexNextNode: pathToTokenParent) {
                node = node
                        .getChildren()
                        .get(indexNextNode);
            }
            return node;
        }
        return this;
    }

    @Override
    public String toJSON(String address_to) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(address_to), this);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
