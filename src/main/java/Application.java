import java.io.*;
import java.util.*;

public class Application {
    public static String option;

    public static String inputFileAddress;

    public static String asmFileAddress;

    public static Parser parser;

    public static Assembler assembler;

    public static Sema semanticAnalysis;

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void compiler(Scanner in) throws IOException, InterruptedException {
        parser = new Parser();
        ast = new AST(ASTNodeType.PROGRAM, "program", 1, null, null);
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
        IdTable idTable = new IdTable(new IdentityHashMap<>(), 0, 'a', new ArrayList<>());
        idTable.formATable(ast);
        idTable.toJSON("src/main/resources/idTable.json");
        semanticAnalysis = new Sema(0, 'a');
        String semaLog = semanticAnalysis.analyze(ast, idTable);
        if (semaLog != null) {
            System.out.println(semaLog);
            System.exit(0);
        }
        String annotatedAstJson = ast.toJSON("src/main/resources/annotatedast.json");
        assembler = new Assembler(new ArrayList<>(), new ArrayList<>(), 8, -1, "main", ".LFB0");
        assembler.asm(ast, idTable);
        if (option != null && option.equals("--dump-asm")) {
            assembler.printAsmFile();
        }
        asmFileAddress = assembler.getAsmFileName();
        asmComlile(asmFileAddress);
    }

    public static void asmComlile(String asmFileAddress) throws IOException {
        Process proc = Runtime.getRuntime().exec("gcc " + asmFileAddress + " -o src/main/resources/app");
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = "";
        while((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
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
