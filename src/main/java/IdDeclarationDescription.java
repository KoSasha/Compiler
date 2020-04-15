import lombok.*;

import java.util.ArrayList;

@AllArgsConstructor@NoArgsConstructor
@Setter@Getter
public class IdDeclarationDescription {
    private ASTNodeType dataType;

    private ASTNodeType type;

    private String lexeme;

    private String level;

    private ArrayList<IdDeclarationDescription> functionParam;
}
