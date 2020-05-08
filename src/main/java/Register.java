import lombok.*;

@AllArgsConstructor@NoArgsConstructor
@Setter@Getter
public class Register {
    private RegisterType type;

    private String lexeme;

    private String value;

    private String nameVariable;
}
