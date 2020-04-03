import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Scanner;

public class Application {
    public static String option;

    public static String inputFileAddress;

    public static Parser parser;

    public static Token currentToken;

    public static Token nextToken;

    public static AST ast;

    public static void main(String[] args) {
        try {
            processingArguments(args);
            FileReader fr = new FileReader(inputFileAddress);
            Scanner in = new Scanner(fr);
            compiler(in);
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CommandLineArgumentsException e) {
            e.printStackTrace();
        }

    }

    public static void compiler(Scanner in) throws IOException {
        parser = new Parser();
        ast = new AST(ASTNodeType.PROGRAM, "program", null, null);
        ArrayList<Integer> pathToTokenParent = new ArrayList<>();
        Integer numberOfString = 1, position;
        while (in.hasNextLine()) {
            String str = in.nextLine();
            position = 0;
            while (position < str.length()) {
                Token token = new Token();
                token = token.getNextToken(str, numberOfString, position);
                if (token != null) {
                    if (token.getPosition() != null) {
                        if (option != null && option.equals("--dump-tokens")) {
                            System.out.println("Loc<" + token.getString().toString() + "," +
                                    token.getPosition().toString() + ">: " + token.getType() + " \"" + token.getLexeme() + "\"");
                        }
                        if (currentToken != null) {
                            nextToken = token;
                            String parserLog = parser.parser(currentToken, nextToken, ast, pathToTokenParent);
                            if (parserLog != null) {
                                System.out.println(parserLog);
                                System.exit(0);
                            }
                            currentToken = nextToken;
                        } else {
                            currentToken = token;
                        }
                        position = token.getPosition() + token.getLexeme().length() - 1;
                    } else {
                        position = str.length();
                    }
                } else {
                    position += 1;
                    if (option != null && option.equals("--dump-tokens")) {
                        System.out.println("Loc<" + numberOfString.toString() + "," +
                                position.toString() + ">: " + "UNKNOWN" + " \"" + str.charAt(position - 1) + "\"");
                    }
                }
            }
            numberOfString += 1;
        }
        String astJson = ast.toJSON("src/main/resources/ast.json");
        if (option != null && option.equals("--dump-ast")) {
            System.out.println(astJson);
        }
        IdTable idTable = new IdTable(new IdentityHashMap<>(), 0, 'a');
        idTable.formATable(ast);
        idTable.toJSON("src/main/resources/idTable.json");
        System.out.println(idTable.getIdTable().size());
    }

    public static void processingArguments(String[] args) throws CommandLineArgumentsException {
        if (args.length == 2) {
            option = args[0];
            inputFileAddress = args[1];
            if (!option.equals("--dump-tokens") && !option.equals("--dump-ast") && !option.equals("--dump-asm")) {
                throw new CommandLineArgumentsException();
            }
        } else {
            if (args.length == 1) {
                inputFileAddress = args[0];
            } else {
                throw new CommandLineArgumentsException();
            }
        }
    }

}
