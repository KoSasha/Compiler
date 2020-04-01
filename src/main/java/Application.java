import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
                            System.out.println(currentToken.getType());
                            System.out.println(nextToken.getType());
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
        ast.toJSON("src/main/resources/ast.json");
    }

    public static void testAst(AST ast) {
        if (ast != null) {

            AST child1 = new AST();
            AST child2 = new AST();

            ast.setNodeType(ASTNodeType.ID);
            ast.setLexeme("id");

            child1.setNodeType(ASTNodeType.ARRAY);
            child1.setLexeme("[1, 2, 3]");

            child2.setNodeType(ASTNodeType.INT);
            child2.setLexeme("i32");

            ast.addByPath(child1,null);
            ast.addByPath(child2,null);

            AST child3 = new AST();
            AST child4 = new AST();

            child3.setNodeType(ASTNodeType.IF);
            child3.setLexeme("if");

            child4.setNodeType(ASTNodeType.INTVARIABLE);
            child4.setLexeme("45");

            ArrayList<Integer> path3 = new ArrayList<>();
            ArrayList<Integer> path4 = new ArrayList<>();

            path3.add(0);
            path4.add(1);

            ast.addByPath(child3, path3);
            ast.addByPath(child4, path4);

            System.out.println(ast.getChildren().get(0).getLexeme());
            System.out.println(ast.getChildren().get(1).getLexeme());

            System.out.println(ast.getChildren().get(0).getChildren().get(0).getLexeme());
            System.out.println(ast.getChildren().get(1).getChildren().get(0).getLexeme());
        }
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
