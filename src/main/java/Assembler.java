import lombok.*;

import java.io.*;
import java.util.*;

@AllArgsConstructor@NoArgsConstructor
@Setter@Getter
public class Assembler {

    private ArrayList<Register> registers;

    private ArrayList<Register> stack;

    private Integer stackOffset;

    private Integer levelCounter;

    private String currentFunctionMark;

    private String currentLevelMark;

    public static String globalLevelSection = ".LFB";

    public static String levelSection = ".L";

    public static String asmFileAddress = "src/main/resources/asm/asm1.s";

    public static String asmBufferFileAddress = "src/main/resources/asm/asm2.s";

    public static String textSection = "\t.text\n";

    public static String globalSection = "\t.global";

    public static String stackFrame = "\t.cfi_startproc\n" +
            "\tpushq\t%rbp\n" +
            "\t.cfi_def_cfa_offset 16\n" +
            "\t.cfi_offset 6, -16\n" +
            "\tmovq\t%rsp, %rbp\n" +
            "\t.cfi_def_cfa_register 6\n";

    public static String stackSmashing = "\tmovq\t%fs:40, %rax\n" +
            "\tmovq\t%rax, -8(%rbp)\n" +
            "\txorl\t%eax, %eax\n";

    public static String stackFail = "\tmovq\t-8(%rbp), %rdx\n" +
            "\txorq\t%fs:40, %rdx\n" +
            "\tje\t.L10\n" +
            "\tcall\t__stack_chk_fail@PLT\n" +
            ".L10:\n";

    public static Integer stackSmashingFlag = 0;

    public static Integer nestingFlag = 0;

    public static String printlnMacro = "\tmovl\t%eax, %esi\n" +
            "\tleaq\t.LC0(%rip), %rdi\n" +
            "\tmovl\t$0, %eax\n" +
            "\tcall\tprintf@PLT\n" +
            "\tmovl\t$0, %eax\n";

    public static String functionReturn = "\t.cfi_def_cfa 7, 8\n" +
            "\tret\n" +
            "\t.cfi_endproc\n";

    public static String leaveStackFrame = "\tleave\n";

    public static String mainMark = "main:\n";

    public static String typeSuffixByte = "b ";

    public static String typeSuffixWord = "w ";

    public static String typeSuffixLong = "l ";

    public static String typeSuffixQuad = "q ";

    public static String commandCopy = "\tmov";

    public static String commandCopyAddress = "\tlea";

    public static String commandCmp = "\tcmp";

    public static String commandPop = "\tpop";

    public static String commandUnconditionalTransition = "\tjmp ";

    public static String commandNotEqualityTransition = "\tjne ";

    public static String commandEqualityTransition = "\tje ";

    public static String commandLessTransition = "\tjge ";

    public static String commandMoreTransition = "\tjle ";

    public static String commandDifference = "\tsub";

    public static String commandSumming = "\tadd";

    public static String commandDivision = "\tdiv";

    public static String commandSDivision = "\tidiv";

    public static String commandFunctionCall = "\tcall";

    public static String commandReturn = "\tret";

    public static String commantConvertLongToDoable = "\tcltd\n";

    public static String commantConvertLongToQuad = "\tcltq\n";

    public static String printSection = ".LC0:\n\t.string\t\"%d\\n\"\n";

    public static String accumulatorRegister = "%rax";

    public static String dataRegister = "%rdx";

    public static String counterRegister = "%rcx";

    public static String dataSubRegister = "%edx";

    public static String accumulatorSubRegister = "%eax";

    public static String basePointerRegister = "%rbp";

    public static String basePointerSubRegister = "%ebp";

    public static String stackPointerRegister = "%rsp";

    public static String stackPointerSubRegister = "%esp";

    public static String sourceIndexRegister = "%rsi";

    public static String sourceIndexSubRegister = "%esi";

    public static String destinationIndexRegister = "%rdi";

    public static String destinationIndexSubRegister = "%edi";

    public void asm(AST ast, IdTable idTable) throws IOException {
        if (ast != null) {
            String lvl, log;
            FileWriter fw = null;
            switch (ast.getNodeType()) {
                case PROGRAM:
                    startAsmProgram(fw);
                    break;

                case PHRASEMAINFUNCTIONDEFINITION:
                case PHRASEFUNCTIONDEFINITION:
                    writeToAsmFile(fw,textSection + globalSection + " ");
                    break;

                case MAIN:
                case FUNCTIONID:
                    startAsmFunction(fw, ast.getLexeme(), ast, idTable);
                    break;

                case SENTENCEFUNCTIONPARAM:
                    addFunctionParamsToStack(fw, ast, idTable);
                    break;

                case SENTENCEFUNCTIONDEFINITIONPARAM:
                    addFunctionDefinitionParamsToStack(fw, ast, idTable);
                    break;

                case PHRASEELSE:
                    addElseBody(fw, ast, idTable);
                    break;

                case OPERATORINEQUALITY:
                case OPERATOREQUALITY:
                case OPERATORAND:
                case OPERATOROR:
                    addConditionForInequalityTransition(fw, ast, idTable);
                    break;

                case OPERATORSUMMING:
                case OPERATORDIFFERENCE:
                case OPERATORDIVISION:
                case OPERATORMULTIPLICATION:
                    addOperation(fw, ast, idTable);
                    break;

                case OPERATORLESS:
                    addConditionForLessTransition(fw, ast, idTable);

                case OPERATORMORE:
                    addConditionForMoreTransition(fw, ast, idTable);
                    break;

                case OPERATORMODULO:
                    addModulo(fw, ast, idTable);
                    break;

                case EXPRESSIONVARIABLEDEFINITION:
                    addVariableDefinition(fw, ast, idTable);
                    break;

                case POINTFUNCTION:
                    takeACharacterFromAString(fw, ast);
                    break;

                case ROUND:
                    addFor(fw, ast, idTable);
                    break;

                case PRINTLNMACRO:
                    addAsmPrint(fw);
                    break;

                case RBRACE:
                    endBody(fw, ast);
                    break;

                default:
                    break;
            }

           if (ast.getChildren() != null) {
               for (AST astChild: ast.getChildren()) {
                   asm(astChild, idTable);
               }
           }

        }
    }

    public void startAsmProgram(FileWriter fw) throws IOException {
        fw = new FileWriter(asmFileAddress);
    }

    public void startAsmFunction(FileWriter fw, String functionName, AST ast, IdTable idTable) throws IOException {
        if (checkFunctionIdParents(ast)) {
            this.setLevelCounter(this.getLevelCounter() + 1);
            String currentLvlMark = globalLevelSection + this.getLevelCounter().toString();
            writeToAsmFile(fw,functionName + "\n" + functionName + ":\n" + currentLvlMark
                    + ":\n" + stackFrame);
            this.setCurrentLevelMark(currentLvlMark);
        } else {
            for (IdDeclarationDescription description: idTable.getIdDeclarationDescriptions()) {
                if (description.getType() == ASTNodeType.FUNCTIONID) {
                    this.setCurrentFunctionMark(description.getLexeme());
                    Integer count = 0;
                    for (IdDeclarationDescription param: description.getFunctionParam()) {
                        count += param.getCount();
                        if (param.getType() == ASTNodeType.ARRAY) {
                            count += 1;
                            if (count == 7) {
                                count -= 1;
                            }
                        } else if (param.getDataType() == ASTNodeType.STRING) {
                            count += 1;
                        }
                    }
                    count = count * 8;
                    String countParamBytes = count.toString();
                    writeToAsmFile(fw, commandDifference + typeSuffixQuad + "$" + countParamBytes + ", " + stackPointerRegister + "\n");
                }
            }
        }
    }

    public boolean checkFunctionIdParents(AST ast) {
        return ast.getParent().getParent().getParent() != null
                && (ast.getParent().getParent().getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION ||
                ast.getParent().getParent().getParent().getNodeType() == ASTNodeType.PHRASEMAINFUNCTIONDEFINITION);
    }

    public void addFunctionParamsToStack(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        String typeSuffix = null;
        Integer off = 0, cnt = 0;
        for (AST astChild: ast.getChildren()) {
            if (astChild.getNodeType() != ASTNodeType.COMMA) {
                typeSuffix = getTypeSuffix(astChild);
                cnt += 1;
                off = addParamToStack(astChild, typeSuffix, idTable, fw, cnt);
            }
        }
        this.setStackOffset(this.getStackOffset() - 4 * off);
        writeDefinitionFunctionParamToAsm(fw, typeSuffix, off);
    }

    public Integer addParamToStack(AST ast, String typeSuffix, IdTable idTable, FileWriter fw, Integer cnt) throws IOException {
        for (IdDeclarationDescription descriptionParam: idTable.getIdDeclarationDescriptions()) {
            if (searchIdInIdTable(ast.getChildren().get(0), ASTNodeType.ID, descriptionParam)) {
                if (descriptionParam.getDataType() != ASTNodeType.STRING) {
                    addRegisterToStack(RegisterType.RBP, basePointerRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
                    this.setStackOffset(this.getStackOffset() + 4);
                    return 1;
                } else {
                    addStringToStack(fw, descriptionParam, typeSuffix, cnt);
                    return 2;
                }
            } else if (searchIdInIdTable(ast.getChildren().get(0), ASTNodeType.ARRAY, descriptionParam)) {
                writeToAsmFile(fw, stackSmashing);
                stackSmashingFlag = 1;
                this.setStackOffset(this.getStackOffset() + 4);
                Integer arraySize = descriptionParam.getValue().length();
                String array[] = descriptionParam.getValue().substring(1, arraySize - 1).split(", ");
                for (String arrayElement: array) {
                    addRegisterToStack(RegisterType.RBP, basePointerRegister, arrayElement, descriptionParam.getLexeme());
                    this.setStackOffset(this.getStackOffset() + 4);
                }
                if (array.length == 6) {
                    this.setStackOffset(this.getStackOffset() - 4);
                }
                return 0;
            }
        }
        return 0;
    }

    public void addStringToStack(FileWriter fw, IdDeclarationDescription descriptionParam,  String typeSuffix, Integer cnt) throws IOException {
        if (cnt == 1) {
            writeToAsmFile(fw, stackSmashing + commandCopy + "absq $" + descriptionParam.getValue() + ", " + accumulatorRegister + "\n"
                            + commandCopy + typeSuffixQuad + accumulatorRegister + ", -19(" + basePointerRegister + ")\n");
            stackSmashingFlag = 1;
            addRegisterToRegisters(RegisterType.RAX, accumulatorRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
            addRegisterToRegisters(RegisterType.RDI, sourceIndexRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
            addRegisterToStack(RegisterType.RBP, basePointerRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
        } else {
            this.setStackOffset(22);
            writeToAsmFile(fw,commandCopy + typeSuffix + "$" + descriptionParam.getValue() + ", -11(" + basePointerRegister + ")\n"
                    + commandCopy + typeSuffixByte + "$0, -9(" + basePointerRegister + ")\n "
                    + commandCopy + typeSuffix + "$" + descriptionParam.getValue() + ", -" + this.getStackOffset() + "(" + basePointerRegister + ")\n"
                    + commandCopy + typeSuffixByte + "$0, -20(" + basePointerRegister + ")\n"
                    + commandCopyAddress + typeSuffixQuad + "-" + this.getStackOffset() + "(" + basePointerRegister + "), " + dataRegister + "\n"
                    + commandCopyAddress + typeSuffixQuad + "-19(" + basePointerRegister + "), " + accumulatorRegister + "\n"
                    + commandCopy + typeSuffixQuad + dataRegister + ", " + sourceIndexRegister + "\n"
                    + commandCopy + typeSuffixQuad + accumulatorRegister + ", " + destinationIndexRegister + "\n");
            addRegisterToStack(RegisterType.RBP, basePointerRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
            addRegisterToRegisters(RegisterType.RSI, sourceIndexRegister, descriptionParam.getValue(), descriptionParam.getLexeme());
        }
    }

    public void writeDefinitionFunctionParamToAsm(FileWriter fw, String typeSuffix, Integer off) throws IOException {
        Integer offset = 0;
        if (!typeSuffix.startsWith(typeSuffixWord)) {
            for (Register reg : this.getStack()) {
                offset = this.getStackOffset() - this.getStack().indexOf(reg) * 4;
                writeToAsmFile(fw, commandCopy + typeSuffix + "$" + reg.getValue() + ", "
                        + "-" + offset.toString() + "(" + basePointerRegister + ")\n");
            }
        }
        Integer stackSize = this.getStack().size();
        if (off == 0) {
            writeArrayParam(fw, stackSize);
            writeToAsmFile(fw, commandFunctionCall + " " + this.getCurrentFunctionMark() + "\n");
            offset = 36;
        } else if (off == 1) {
            writeSimpleParam(fw, stackSize, offset, typeSuffix);
            writeToAsmFile(fw, commandFunctionCall + " " + this.getCurrentFunctionMark() + "\n");
            offset = 4;
        } else {
            writeToAsmFile(fw, commandFunctionCall + " " + this.getCurrentFunctionMark() + "\n");
            offset = 28;
        }
        addDataForPrint(fw, offset);
    }

    public void writeArrayParam(FileWriter fw, Integer stackSize) throws IOException {
        addRegisterToRegisters(RegisterType.RAX, accumulatorRegister, this.getStack().get(stackSize - 1).getValue(),
                this.getStack().get(0).getNameVariable());
        Integer registersSize = this.getRegisters().size();
        addRegisterToRegisters(RegisterType.RDI, destinationIndexRegister, this.getRegisters().get(registersSize - 1).getValue(),
                this.getRegisters().get(registersSize - 1).getNameVariable());
        writeToAsmFile(fw, commandCopyAddress + typeSuffixQuad + " -" + this.getStackOffset().toString()
                + "(" + basePointerRegister + "), " + accumulatorRegister + "\n"
                + commandCopy + typeSuffixQuad + accumulatorRegister + ", " + destinationIndexRegister + "\n");
    }

    public void writeSimpleParam(FileWriter fw, Integer stackSize, Integer offset, String typeSuffix) throws IOException {
        if (stackSize == 2) {
            addRegisterToRegisters(RegisterType.EDX, dataSubRegister, this.getStack().get(stackSize - 1).getValue(),
                    this.getStack().get(stackSize - 1).getNameVariable());
            addRegisterToRegisters(RegisterType.EAX, accumulatorSubRegister, this.getStack().get(0).getValue(),
                    this.getStack().get(0).getNameVariable());
            addRegisterToRegisters(RegisterType.ESI, sourceIndexSubRegister, this.getStack().get(0).getValue(),
                    this.getStack().get(0).getNameVariable());
            addRegisterToRegisters(RegisterType.EDI, destinationIndexSubRegister, this.getStack().get(0).getValue(),
                    this.getStack().get(0).getNameVariable());
        } else if (stackSize == 1) {
            addRegisterToRegisters(RegisterType.EAX, accumulatorSubRegister, this.getStack().get(0).getValue(),
                    this.getStack().get(0).getNameVariable());
        }
        offset = this.getStackOffset() - 4;
        writeToAsmFile(fw, commandCopy + typeSuffix + " -" + offset.toString()
                + "(" + basePointerRegister + "), " + dataSubRegister + "\n");
        offset = this.getStackOffset();
        writeToAsmFile(fw, commandCopy + typeSuffix + " -" + offset.toString()
                + "(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                + commandCopy + typeSuffix + dataSubRegister + ", " + sourceIndexSubRegister + "\n"
                + commandCopy + typeSuffix + accumulatorSubRegister + ", " + destinationIndexSubRegister + "\n");
    }

    public void addRegisterToRegisters(RegisterType reg, String regLexeme, String value, String nameVariable) {
        Register reg1 = new Register(reg, regLexeme, value, nameVariable);
        this.getRegisters().add(reg1);
    }

    public void addRegisterToStack(RegisterType reg, String regLexeme, String value, String nameVariable) {
        Register reg1 = new Register(reg, regLexeme, value, nameVariable);
        this.getStack().add(reg1);
    }

    public void writeToAsmFile(FileWriter fw, String string) throws IOException {
        fw = new FileWriter(asmFileAddress, true);
        fw.write(string);
        fw.close();
    }

    public boolean searchIdInIdTable(AST ast, ASTNodeType type, IdDeclarationDescription param) {
        return param.getType() == type && param.getLexeme().startsWith(ast.getLexeme());
    }

    public String getTypeSuffix(AST ast) {
        switch (ast.getNodeType()) {
            case DESCRIPTIONINT:
                return typeSuffixLong;
            case DESCRIPTIONFLOAT:
                return typeSuffixQuad;
            case DESCRIPTIONSTRING:
                return typeSuffixWord;
        }
        return null;
    }

    public void addFunctionDefinitionParamsToStack(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        Integer offset;
        Integer countNotEmptyRegisters = this.getRegisters().size();
        if (this.getRegisters().get(countNotEmptyRegisters - 1).getType() == RegisterType.EDI) {
            copyToStack(fw, 8, destinationIndexSubRegister, typeSuffixLong, countNotEmptyRegisters - 1);
            copyToStack(fw, 4, sourceIndexSubRegister, typeSuffixLong, countNotEmptyRegisters - 2);
        } else if (this.getRegisters().get(countNotEmptyRegisters - 1).getType() == RegisterType.RDI) {
                this.setStackOffset(this.getStackOffset() - 8);
                writeToAsmFile(fw,commandCopy + typeSuffixQuad + " " + destinationIndexRegister + ", -"
                        + this.getStackOffset()  + "(" + basePointerRegister + ")\n"
                        + commandCopy + typeSuffixQuad + " -"
                        + this.getStackOffset()  + "(" + basePointerRegister + "), " + accumulatorRegister + "\n");

        } else {
            offset = this.getStackOffset() + 10;
            writeToAsmFile(fw,commandCopy + typeSuffixQuad + destinationIndexRegister + ", -" + offset.toString()
                    + "(" + basePointerRegister + ")\n");
            offset = offset + 8;
            writeToAsmFile(fw,commandCopy + typeSuffixQuad + sourceIndexRegister + ", -" + offset.toString()
                    + "(" + basePointerRegister + ")\n");
            offset = offset / 2;
            writeToAsmFile(fw,commandCopy + typeSuffixLong + "$-1, -" + offset.toString()
                    + "(" + basePointerRegister + ")\n");
            offset -= 4;
            writeToAsmFile(fw,commandCopy + typeSuffixLong + "$0, -" + offset.toString()
                    + "(" + basePointerRegister + ")\n");
            offset -= 4;
            writeToAsmFile(fw,commandCopy + typeSuffixLong + "$0, -" + offset.toString()
                    + "(" + basePointerRegister + ")\n");

        }
    }

    public void addElseBody(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        Integer level = this.levelCounter + 1;
        String string = commandCmp + typeSuffixByte + "%al, %dl\n" + commandNotEqualityTransition + " " + levelSection + level.toString() + "\n";
        level = this.levelCounter - 1;
        String mark = levelSection + level.toString();
        insertStringToAsmFileBeforeMark(string, mark);
        addNesting(fw, ast, mark);
        addAssignment(fw, ast);
    }

    public void addNesting(FileWriter fw, AST ast, String mark) throws IOException {
        this.setLevelCounter(this.levelCounter + 2);
        String string = commandCopy + typeSuffixLong + "-12(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                + commandCopy + typeSuffixLong + accumulatorSubRegister + ", -16(" + basePointerRegister + ")\n";
        string += operatorAdd(ast);
        string += commandCmp + typeSuffixLong + "$1, -4(" + basePointerRegister + ")\n"
                + commandNotEqualityTransition + levelSection + this.getLevelCounter() + "\n"
                + commandCopy + typeSuffixLong + "-16(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                + commandDifference + typeSuffixLong + "$1, " + accumulatorSubRegister + "\n";
        this.setLevelCounter(this.levelCounter + 1);
        Integer level = this.levelCounter - 2;
        string += commandUnconditionalTransition + levelSection + this.getLevelCounter() + "\n" + levelSection + level.toString() + ":\n";
        insertStringToAsmFileBeforeMark(string, mark);
        this.setCurrentLevelMark(levelSection + level);

    }

    public void addOperation(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        if (ast.getNodeType() == ASTNodeType.OPERATORDIVISION) {
            writeToAsmFile(fw, commandDivision);
        } else if (ast.getNodeType() == ASTNodeType.OPERATORDIFFERENCE) {
            writeToAsmFile(fw, commandDifference);
        }
    }

    public void addAssignment(FileWriter fw, AST ast) throws IOException {
        String string = commandCopy + typeSuffixLong + "$2, -4(" + basePointerRegister + ")\n"
                + commandCopy + typeSuffixLong + "$-1, -16(" + basePointerRegister + ")\n";
        Integer level = this.getLevelCounter() - 1;
        insertStringToAsmFileAfterMark(string + levelSection + level.toString() + ":\n" + commandSumming + typeSuffixLong
                + "$1, -4(" + basePointerRegister + ")\n", this.getCurrentLevelMark());
    }

    public void copyToStack(FileWriter fw, Integer offset, String source, String type, Integer regIndex) throws IOException {
        this.setStackOffset(this.getStackOffset() + offset);
        addRegisterToStack(RegisterType.RBP, basePointerRegister, getRegisters().get(regIndex).getValue(),
                getRegisters().get(regIndex).getNameVariable());
        writeToAsmFile(fw,commandCopy + type + " " + source + ", -" + this.getStackOffset()  + "(" + basePointerRegister + ")\n");
    }

    public void addDataForPrint(FileWriter fw, Integer offset) throws IOException {
        fw = new FileWriter(asmFileAddress, true);
        fw.write("\tmovl\t%eax, -" + offset.toString() + "(%rbp)\n" + "\tmovl\t-" + offset + "(%rbp), %eax\n");
        fw.close();
    }

    public void endBody(FileWriter fw, AST ast) throws IOException {
        if (ast.getParent().getNodeType() == ASTNodeType.PHRASEMAINFUNCTIONDEFINITION) {
            String string = "";
            if (stackSmashingFlag == 1) {
                string = stackFail;
            }
            string += leaveStackFrame + functionReturn;
            insertStringToAsmFileBeforeMark(string, ".LC0");
        } else if (ast.getParent().getNodeType() == ASTNodeType.PHRASEFUNCTIONDEFINITION) {
            writeToAsmFile(fw, functionReturn);
        }
    }

    public String operatorAdd(AST ast) {
        return commandSumming + typeSuffixLong + "$1, -8(" + basePointerRegister + ")\n"
                + commandSumming + typeSuffixLong +  "$1, -12(" + basePointerRegister + ")\n";
    }

    public void addConditionForInequalityTransition(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        if (ast.getParent().getNodeType() == ASTNodeType.SENTENCEASSERTPARAM) {
            addAsmAssert(fw, ast);
        } else if (ast.getParent().getNodeType() == ASTNodeType.CONDITIONWHILE) {
            addConditionWhile(fw, ast);
        }
    }

    public void addConditionForMoreTransition(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        if (ast.getChildren().get(1).getNodeType() == ASTNodeType.ARRAYELEMENT) {
            String string = commandCopy + typeSuffixLong + "-4(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                    + commantConvertLongToQuad + commandCopyAddress + typeSuffixQuad + "0(,"
                    + accumulatorRegister + ",4), " + dataRegister + "\n"
                    + commandCopy + typeSuffixQuad + "-" + this.getStackOffset() + "(" + basePointerRegister + "), "
                    + accumulatorRegister + "\n" + commandSumming + typeSuffixQuad + dataRegister + ", " + accumulatorRegister + "\n"
                    + commandCopy + typeSuffixLong + "(" + accumulatorRegister + "), " + accumulatorSubRegister + "\n";
            String cmp = commandCmp + typeSuffixLong + accumulatorSubRegister + ", -8(" + basePointerRegister + ")\n";
            this.setLevelCounter(this.levelCounter + 1);
            String mark = this.getCurrentLevelMark();
            this.setCurrentLevelMark(levelSection + this.getLevelCounter().toString());
            insertStringToAsmFileAfterMark(string + cmp + commandMoreTransition + this.getCurrentLevelMark() + "\n"
                    + this.getCurrentLevelMark() + ":\n" + commandSumming + typeSuffixLong + "$1, -4(" + basePointerRegister + ")\n", mark);
            insertStringToAsmFileBeforeMark(string + commandCopy + typeSuffixLong + accumulatorSubRegister + ", -8(" + basePointerRegister + ")\n", this.getCurrentLevelMark());
        }
    }

    public void takeACharacterFromAString(FileWriter fw, AST ast) throws IOException {
        if (ast.getParent().getChildren().get(1).equals(ast)) {
            String string = takeChar(dataRegister, counterRegister, dataSubRegister, 24);
            string += commandCopy + typeSuffixLong + " -4(" + basePointerRegister + "), " + accumulatorSubRegister + "\n";
            string += takeChar(counterRegister, accumulatorRegister, accumulatorSubRegister, 32);
            insertStringToAsmFileAfterMark(commandCopy + typeSuffixLong + "-8(" + basePointerRegister + "), "
                    + accumulatorSubRegister + "\n" + string, this.getCurrentLevelMark());
        }
    }

    public String takeChar(String reg1, String reg2, String reg3, Integer offset) {
        return commandCopy + "slq " + accumulatorSubRegister + ", " + reg1 + "\n" + commandCopy + typeSuffixQuad + "-" + offset.toString()
                + "(" + basePointerRegister + "), " + accumulatorRegister + "\n"
                + commandSumming + typeSuffixQuad + reg1 + ", " + accumulatorRegister + "\n"
                + commandCopy + "zbl (" + accumulatorRegister + "), " + reg3 + "\n";
    }

    public void addConditionForLessTransition(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        AST operand2 = ast.getChildren().get(1).getChildren().get(0);
        for (Register reg: this.getRegisters()) {
            if (reg.getLexeme().startsWith(accumulatorSubRegister)) {
                this.getRegisters().remove(reg);
                break;
            }
        }
        addRegisterToRegisters(RegisterType.EAX, accumulatorSubRegister, this.getStack().get(1).getValue(),
                this.getStack().get(1).getNameVariable());
        Integer offset = this.getStackOffset() - 4;
        String string = commandCopy + typeSuffixLong + " -" + this.getStackOffset()
                + "(" + basePointerRegister + "), " + accumulatorSubRegister + "\n" +
                commandCmp + typeSuffixLong + " -" + offset.toString() + "("
                + basePointerRegister + "), " + accumulatorSubRegister + "\n";
        this.setLevelCounter(this.levelCounter + 1);
        string += commandLessTransition + levelSection + this.getLevelCounter().toString() + "\n"
                + levelSection + this.getLevelCounter().toString() + ":\n";
        insertStringToAsmFileAfterMark(string, this.getCurrentLevelMark());
        this.setCurrentLevelMark(levelSection + this.levelCounter);
    }

    public void addAsmAssert(FileWriter fw, AST ast) throws IOException {
        AST operand2 = ast.getChildren().get(1).getChildren().get(0);
        if (operand2.getNodeType() == ASTNodeType.INTVARIABLE) {
            addRegisterToStack(RegisterType.RBP, basePointerRegister, operand2.getLexeme(), null);
            Integer offset = this.getStackOffset() - 4;
            this.setLevelCounter(this.levelCounter + 1);
            writeToAsmFile(fw, commandCmp + typeSuffixLong + " $" + operand2.getLexeme()
                    + ", -" + offset.toString() + "(" + basePointerRegister + ")\n"
                    + commandNotEqualityTransition + levelSection + this.levelCounter.toString() + "\n");
            this.setLevelCounter(this.levelCounter + 1);
            writeToAsmFile(fw, commandCopy + typeSuffixLong + "$0, " + accumulatorSubRegister + "\n"
                    + commandUnconditionalTransition + levelSection + this.levelCounter.toString() + "\n");
            this.setLevelCounter(this.levelCounter - 1);
            String currentLvlMark = levelSection + this.getLevelCounter();
            writeToAsmFile(fw, currentLvlMark + ":\n");
            this.setCurrentLevelMark(currentLvlMark);
            this.setLevelCounter(this.levelCounter + 1);
            writeToAsmFile(fw, levelSection + this.getLevelCounter() + ":\n"
                    + commandPop + typeSuffixQuad + " " + basePointerRegister + "\n");
        }
    }

    public void addConditionWhile(FileWriter fw, AST ast) throws IOException {
        AST operand2 = ast.getChildren().get(1).getChildren().get(0);
        if (operand2.getNodeType() == ASTNodeType.INTVARIABLE) {
            addRegisterToStack(RegisterType.RBP, basePointerRegister, operand2.getLexeme(), null);
            this.setLevelCounter(this.levelCounter + 1);
            String string = commandCmp + typeSuffixLong + " $" + operand2.getLexeme()
                    + ", -" + this.getStackOffset().toString() + "(" + basePointerRegister + ")\n"
                    + commandNotEqualityTransition + levelSection + this.levelCounter.toString() + "\n";
            Integer offset = this.getStackOffset() - 4;
            string += commandCopy + typeSuffixLong + "-" + offset.toString() + "(" + basePointerRegister + "), "
                    + accumulatorSubRegister + "\n";
            insertStringToAsmFileAfterMark(string, currentLevelMark);
            insertStringToAsmFileBeforeMark(levelSection + levelCounter + ":\n", currentLevelMark);
            this.setCurrentLevelMark(levelSection + levelCounter);
        }

    }

    public void addModulo(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        for (Register reg: this.getRegisters()) {
            if (reg.getLexeme().startsWith(accumulatorSubRegister)) {
                this.getRegisters().remove(reg);
                break;
            }
        }
        addRegisterToRegisters(RegisterType.EAX, accumulatorSubRegister, this.getStack().get(1).getValue(),
                this.getStack().get(1).getNameVariable());
        String string = commandCopy + typeSuffixLong + " -" + this.getStackOffset().toString() + "(" + basePointerRegister + "), "
                + accumulatorSubRegister + "\n" + commantConvertLongToDoable;
        Integer offset = this.getStackOffset() - 4;
        string += commandSDivision + typeSuffixLong + " -" + offset.toString() + "(" + basePointerRegister + ")\n"
                + commandCopy + typeSuffixLong + " " + dataSubRegister + ", -" + this.getStackOffset()
                + "(" + basePointerRegister + ")\n";
        insertStringToAsmFileAfterMark(string, currentLevelMark);
    }

    public void addFor(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        if (checkParentRound(ast)) {
            for (Register reg : registers) {
                if (reg.getLexeme().startsWith(accumulatorRegister)) {
                    addRegisterToRegisters(RegisterType.EAX, accumulatorSubRegister, reg.getValue(), reg.getNameVariable());
                    break;
                }
            }
            String string = commandCopy + typeSuffixLong + "(" + accumulatorRegister + "), " + accumulatorSubRegister + "\n"
                    + commandCopy + typeSuffixLong + accumulatorSubRegister + ", -8(" + basePointerRegister + ")" + "\n"
                    + commandCopy + typeSuffixLong + "$" + ast.getChildren().get(0).getChildren().get(0).getLexeme()
                    + ", -4(" + basePointerRegister + ")" + "\n";
            this.setLevelCounter(this.levelCounter + 1);
            string += commandUnconditionalTransition + levelSection + this.getLevelCounter().toString() + "\n"
                    + levelSection + this.getLevelCounter().toString() + ":\n";
            writeToAsmFile(fw, string);
            addConditionFor(fw, ast.getChildren().get(1).getChildren().get(0).getLexeme(), 4);
            writeToAsmFile(fw, commandCopy + typeSuffixLong + "-8(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                    + commandPop + typeSuffixQuad + " " + basePointerRegister + "\n");
        } else {
            if (nestingFlag == 0) {
                this.setLevelCounter(this.levelCounter + 1);
                writeToAsmFile(fw, commandUnconditionalTransition + levelSection + this.getLevelCounter().toString() + "\n"
                        + levelSection + this.getLevelCounter().toString() + ":\n");
                addConditionFor(fw, ast.getChildren().get(1).getChildren().get(0).getLexeme(), 8);
                writeToAsmFile(fw,commandCopy + typeSuffixLong + "-16(" + basePointerRegister + "), " + accumulatorSubRegister + "\n"
                        + levelSection + "8:\n" + commandPop + typeSuffixQuad + " " + basePointerRegister + "\n");
                nestingFlag += 1;
            } else {
                this.setLevelCounter(this.levelCounter + 1);
                String string = commandCopy + typeSuffixLong + "$0, -4(" + basePointerRegister + ")\n"
                        + commandUnconditionalTransition + levelSection + this.getLevelCounter() + "\n"
                        + levelSection + this.getLevelCounter() + ":\n";
                insertStringToAsmFileAfterMark(string, this.getCurrentLevelMark());
                addConditionFor(fw, ast.getChildren().get(1).getChildren().get(0).getLexeme(), 4);
                insertStringToAsmFileBeforeMark(commandSumming + typeSuffixLong + "$1, -12(" + basePointerRegister + ")\n"
                        + commandSumming + typeSuffixLong + "$1, -8(" + basePointerRegister + ")\n", ".L2");
            }
        }
    }

    public void addConditionFor(FileWriter fw, String lexeme, Integer offset) throws IOException {
        String mark = levelSection + this.getLevelCounter();
        this.setLevelCounter(this.levelCounter + 1);
        String string = commandCmp + typeSuffixLong + "$" + lexeme
                + ", -" + offset.toString() + "(" + basePointerRegister + ")\n "
                + commandMoreTransition + levelSection + this.getLevelCounter().toString() + "\n";
        this.setCurrentLevelMark(levelSection + this.getLevelCounter().toString());
        insertStringToAsmFileAfterMark(string, mark);
        insertStringToAsmFileBeforeMark(this.getCurrentLevelMark() + ":\n", mark);
    }

    public boolean checkParentRound(AST ast) {
        return ast.getParent().getParent().getParent().getNodeType() == ASTNodeType.PHRASEFOR;
    }

    public void addVariableDefinition(FileWriter fw, AST ast, IdTable idTable) throws IOException {
        if (ast.getParent().getParent().getNodeType() == ASTNodeType.PHRASEIF) {
            String string = commandCopy + typeSuffixLong + " -" + this.getStackOffset().toString() + "(" + basePointerRegister + "), "
                    + accumulatorSubRegister + "\n" + commandCopy + typeSuffixLong + " " + accumulatorSubRegister
                    + ", -4(" + basePointerRegister + ")\n";
            Integer offset = this.getStackOffset() - 4;
            string += commandCopy + typeSuffixLong + " -" + offset.toString() + "(" + basePointerRegister + "), "
                    + accumulatorSubRegister + "\n" + commandCopy + typeSuffixLong + " " + accumulatorSubRegister + ", -"
                    + this.getStackOffset().toString() + "(" + basePointerRegister + ")\n"
                    + commandCopy + typeSuffixLong + " -4(" + basePointerRegister + "), "
                    + accumulatorSubRegister + "\n" + commandCopy + typeSuffixLong + " " + accumulatorSubRegister + ", -"
                    + offset.toString() + "(" + basePointerRegister + ")\n";
            insertStringToAsmFileBeforeMark(string, currentLevelMark);
        }
    }

    public void insertStringToAsmFileAfterMark(String string, String mark) throws IOException {
        FileReader fr = new FileReader(asmFileAddress);
        Scanner in = new Scanner(fr);
        FileWriter fw = new FileWriter(asmBufferFileAddress);
        while (in.hasNextLine()) {
            String str = in.nextLine();
            fw.write(str + "\n");
            if (str.startsWith(mark)) {
                fw.write(string);
            }
        }
        fr.close();
        fw.close();
        String address = asmFileAddress;
        asmFileAddress = asmBufferFileAddress;
        asmBufferFileAddress = address;
        File file = new File(asmBufferFileAddress);
        file.delete();
    }

    public void insertStringToAsmFileBeforeMark(String string, String mark) throws IOException {
        FileReader fr = new FileReader(asmFileAddress);
        Scanner in = new Scanner(fr);
        FileWriter fw = new FileWriter(asmBufferFileAddress);
        while (in.hasNextLine()) {
            String str = in.nextLine();
            if (str.startsWith(mark)) {
                fw.write(string);
            }
            fw.write(str + "\n");
        }
        fr.close();
        fw.close();
        String address = asmFileAddress;
        asmFileAddress = asmBufferFileAddress;
        asmBufferFileAddress = address;
        File file = new File(asmBufferFileAddress);
        file.delete();
    }

    public void addAsmPrint(FileWriter fw) throws IOException {
        fw = new FileWriter(asmFileAddress, true);
        fw.write(printlnMacro);
        fw.write(printSection);
        fw.close();
    }

    public void printAsmFile() throws IOException {
        FileReader fr = new FileReader(asmFileAddress);
        Scanner in = new Scanner(fr);
        while (in.hasNextLine()) {
            System.out.println(in.nextLine());
        }
        fr.close();
    }

    public String getAsmFileName() {
        return asmFileAddress;
    }
}