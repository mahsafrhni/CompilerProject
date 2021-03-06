package CodeGen;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Block.GlobalBlock;
import CodeGen.Parts.Dec.Dec;
import CodeGen.Parts.Dec.function.FunctionDCL;
import CodeGen.Parts.Dec.var.ArrDCL;
import CodeGen.Parts.Dec.var.SimpleVarDCL;
import CodeGen.Parts.Expression.*;
import CodeGen.Parts.Expression.Const.*;
import CodeGen.Parts.Expression.binary.arithmetic.*;
import CodeGen.Parts.Expression.binary.condition.*;
import CodeGen.Parts.Expression.unary.*;
import CodeGen.Parts.Expression.var.ArrVar;
import CodeGen.Parts.Expression.var.RecordVar;
import CodeGen.Parts.Expression.var.SimpleVar;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.Parts.Node;
import CodeGen.Parts.Op;
import CodeGen.Parts.St.Break;
import CodeGen.Parts.St.Continue;
import CodeGen.Parts.St.Println;
import CodeGen.Parts.St.ReturnFunc;
import CodeGen.Parts.St.assign.*;
import CodeGen.Parts.St.condition.Case;
import CodeGen.Parts.St.condition.If;
import CodeGen.Parts.St.condition.Switch;
import CodeGen.Parts.St.loop.For;
import CodeGen.Parts.St.loop.InitExp;
import CodeGen.Parts.St.loop.Repeat;
import CodeGen.Parts.St.loop.StepExp;
import CodeGen.SymTab.DSCP.*;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import Parser.Lexical;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator implements Parser.CodeGenerator {
    private Lexical lexical;
    private SS semanticStack;
    Object temp = null;
    int counter = 1;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SS();
        semanticStack.push(GlobalBlock.getInstance());
    }

    public Node getResult() {
        return (Node) semanticStack.getFirst();
    }

    public void doSemantic(String sem) {
        switch (sem) {
            case "push": {
                semanticStack.push(lexical.currentToken().getValue());
                temp = lexical.currentToken().getValue();
                // System.out.println(temp + " salam salam");
                break;
            }
            case "pop": {
                semanticStack.pop();
                break;
            }
            case "createFlag": {
                Byte flag = 0;
                semanticStack.push(flag);
                break;
            }
            case "createBlock": {
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }
            case "mkFuncDCL": {
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                FunctionDCL functionDcl = new FunctionDCL(type,
                        (String) lexical.currentToken().getValue(), null, new ArrayList<>());
                semanticStack.push(functionDcl);
                SymTabHandler.getInstance().setLastFunction(functionDcl);
                SymTabHandler.getInstance().addScope(Scope.FUNCTION);
                break;
            }
            case "addParameter": {
                String name = ((NOP) semanticStack.pop()).name;
                LocalDCSP dscp = (LocalDCSP) SymTabHandler.getInstance().getDescriptor(name);
                dscp.setValid(true);
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.addParameter(name, dscp);
                semanticStack.push(function);
                break;
            }
            case "completeFuncDCL": {
                Block block = (Block) semanticStack.pop();
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.setBlock(block);
                semanticStack.push(function);
                SymTabHandler.getInstance().setLastFunction(null);
                SymTabHandler.getInstance().popScope();
                break;
            }
            case "addFuncDCL": {
                FunctionDCL function = (FunctionDCL) semanticStack.pop();
                function.declare();
                semanticStack.push(function);
                break;
            }
            case "mkSimpleVarDCL": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    SymTabHandler.getInstance().addVariable(name, new GlobalVarDCSP(type, false, false));
                else
                    SymTabHandler.getInstance().addVariable(name, new LocalVarDCSP(type, false,
                            SymTabHandler.getInstance().getIndex(), false));
                semanticStack.push(new NOP(name));
                break;
            }
            case "constTrue": {
                String varName = ((NOP) semanticStack.pop()).name;
                DCSP dscp = SymTabHandler.getInstance().getDescriptor(varName);
                dscp.setConstant(true);
                semanticStack.push(new NOP(varName));
                break;
            }
            case "pushBlock": {
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }
            case "addBlock": {
                if (counter == 1) {
                    Op operation = (Op) semanticStack.pop();
                    Block block = (Block) semanticStack.pop();
                    block.addOperation(operation);
                    semanticStack.push(block);
                    break;
                }
                if (counter == 2) {
                    Op operation = (Op) semanticStack.pop();
                    Op operation2 = (Op) semanticStack.pop();
                    Block block = (Block) semanticStack.pop();
                    block.addOperation(operation);
                    semanticStack.push(block);
                    break;
                }
            }
            case "addGlobalBlock": {
                Dec declaration = (Dec) semanticStack.pop();
                if (declaration instanceof FunctionDCL)
                    addFuncToGlobalBlock((FunctionDCL) declaration);
                else
                    GlobalBlock.getInstance().addDeclaration(declaration);
                break;
            }
            case "setValueToVar": {
                Expression exp = (Expression) semanticStack.pop();
                String name = ((NOP) semanticStack.pop()).name;
                DCSP dscp = SymTabHandler.getInstance().getDescriptor(name);
                SimpleVarDCL varDcl = new SimpleVarDCL(name, dscp.getType(), dscp.isConstant(), dscp instanceof GlobalDCSP);
                varDcl.setExp(exp);
                semanticStack.push(varDcl);
                break;
            }
            case "mkSimpleAutoVarDCL": {
                Expression exp = (Expression) semanticStack.pop();
                String varName = (String) semanticStack.pop();
                SimpleVarDCL varDcl;
                if (semanticStack.peek() instanceof GlobalBlock)
                    varDcl = new SimpleVarDCL(varName, "auto", false, true, exp);
                else
                    varDcl = new SimpleVarDCL(varName, "auto", false, false, exp);
                varDcl.declare();
                semanticStack.push(varDcl);
                break;
            }
            case "dimpp": {
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(flag);
                break;
            }
            case "arrDCL": {
                String name = (String) lexical.currentToken().getValue();
                Byte flag = (Byte) semanticStack.pop();
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    ArrDCL.declare(name, type, new ArrayList<>(), flag, true);
                else
                    ArrDCL.declare(name, type, new ArrayList<>(), flag, false);
                semanticStack.push(new NOP(name));
                break;
            }
            case "mkArrayVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                String name = ((NOP) semanticStack.pop()).name;
                DCSP dscp = SymTabHandler.getInstance().getDescriptor(name);
                if (!dscp.getType().equals(type))
                    throw new RuntimeException("Types don't match");
                ArrDCL arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    if (((GlobalArrDCSP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDCL(name, type, true, flag);
                    ((GlobalArrDCSP) dscp).setDimList(expressionList);
                } else {
                    if (((LocalArrDCSP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDCL(name, type, false, flag);
                    ((LocalArrDCSP) dscp).setDimList(expressionList);
                }
                semanticStack.push(arrDcl);
                break;
            }
            case "mkAutoArrVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                String name = (String) semanticStack.pop();
                ArrDCL arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    arrDcl = new ArrDCL(name, type, true, expressionList.size());
                    ArrDCL.declare(name, type, expressionList, expressionList.size(), true);
                } else {
                    arrDcl = new ArrDCL(name, type, false, expressionList.size());
                    ArrDCL.declare(name, type, expressionList, expressionList.size(), false);
                }
                arrDcl.setDimensions(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            case "div": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Divide(first, second));
                break;
            }
            case "minus": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Minus(first, second));
                break;
            }
            case "mult": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Multiply(first, second));
                break;
            }
            case "rmn": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Remainder(first, second));
                break;
            }
            case "sum": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Sum(first, second));
                break;
            }
            case "and": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new And(first, second));
                break;
            }
            case "andBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new ANDbit(first, second));
                break;
            }
            case "biggerAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BiggerEqual(first, second));
                break;
            }
            case "biggerThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BiggerThan(first, second));
                break;
            }
            case "equal": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Equal(first, second));
                break;
            }
            case "lessAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessEqual(first, second));
                break;
            }
            case "lessThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessThan(first, second));
                break;
            }
            case "notEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new NotEqual(first, second));
                break;
            }
            case "or": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new OR(first, second));
                break;
            }
            case "xor": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new XOR(first, second));
                break;
            }
            case "orBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new ORbit(first, second));
                break;
            }
            case "xorBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new XORbit(first, second));
                break;
            }
            case "cast": {
                Expression exp = (Expression) semanticStack.pop();
                Type newType = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                semanticStack.push(new Cast(exp, newType));
                break;
            }
            case "negative": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Negative(exp));
                break;
            }
            case "not": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Not(exp));
                break;
            }
            case "postmm": {
                Var var = (Var) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PostMM(var));
                break;
            }
            case "postpp": {
                Var var = (Var) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PostPP(var));
                break;
            }
            case "premm": {
                Var var = (Var) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PreMM(var));
                break;
            }
            case "prepp": {
                Var var = (Var) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PrePP(var));
                break;
            }
            case "pushReal": {
                Object realNum = lexical.currentToken().getValue();
                if (realNum instanceof Float)
                    semanticStack.push(new FloatConst((Float) realNum));
                else
                    semanticStack.push(new DoubleConst((Double) realNum));
                break;
            }
            case "pushInt": {
                Object integerNum = lexical.currentToken().getValue();
                if (integerNum instanceof Integer)
                    semanticStack.push(new IntegerConst((Integer) integerNum));
                else
                    semanticStack.push(new LongConst((Long) integerNum));
                break;
            }
            case "pushLong": {
                Object longNum = lexical.currentToken().getValue();
                long num = convertToLong(longNum);
                semanticStack.push(new LongConst(num));
                break;
            }
            case "pushFloat": {
                Object FloatNum = lexical.currentToken().getValue();
                Float num = convertToFloat(FloatNum);
                semanticStack.push(new FloatConst(num));
                break;
            }
            case "pushBool": {
                Object value = lexical.currentToken().getValue();
                semanticStack.push(new BoolConst((Boolean) value));
                break;
            }
            case "pushChar": {
                semanticStack.push(new CharConst((Character) lexical.currentToken().getValue()));
                break;
            }
            case "pushString": {
                semanticStack.push(new StringConst((String) lexical.currentToken().getValue()));
                break;
            }
            case "pushVar": {
                String name = (String) lexical.currentToken().getValue();
                if (SymTabHandler.getInstance().getFuncNames().contains(name)) {
                    semanticStack.push(name);
                    //  System.out.println("1");
                    break;
                }
                DCSP dscp = SymTabHandler.getInstance().getDescriptor(name);
                if (dscp instanceof GlobalVarDCSP || dscp instanceof LocalVarDCSP) {
                    //  System.out.println("2");
                    semanticStack.push(new SimpleVar(name, dscp.getType()));
                    System.out.println(new SimpleVar(name, dscp.getType()));
                } else if (dscp instanceof GlobalArrDCSP || dscp instanceof LocalArrDCSP) {
                    semanticStack.push(new ArrVar(name, new ArrayList<>(), dscp.getType()));
                    // System.out.println("3");
                }
                break;
            }
            case "flagpp": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "pushArrayVar": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                ArrVar var = (ArrVar) semanticStack.pop();
                semanticStack.push(new ArrVar(var.getName(), expressionList, var.getType()));
                break;
            }
            case "assign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new Assign(exp, var));
                break;
            }
            case "sumAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new SumAssign(exp, var));
                break;
            }
            case "minAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MinusAssign(exp, var));
                break;
            }
            case "divAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new DivideAssign(exp, var));
                break;
            }
            case "mulAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MultiplyAssign(exp, var));
                break;
            }
            case "rmnAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Var var = (Var) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new RemainderAssign(exp, var));
                break;
            }
            case "check2types": {
                Type type = SymTabHandler.getTypeFromName((String) semanticStack.pop());
                Var variable = (Var) semanticStack.pop();
                if (!(variable instanceof ArrVar))
                    throw new RuntimeException("You can't new a simple variable");
                if (variable.getType() != null && !type.equals(variable.getType()))
                    throw new RuntimeException("types don't match");
                semanticStack.push(variable);
                break;
            }
            case "setCheckDim": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                ArrVar var = (ArrVar) semanticStack.pop();
                if (var.getDSCP() instanceof GlobalArrDCSP)
                    if (((GlobalArrDCSP) var.getDSCP()).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                if (var.getDSCP() instanceof LocalArrDCSP)
                    if (((LocalArrDCSP) var.getDSCP()).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                var.setDimensions(expressionList);
                semanticStack.push(new NOP());
                break;
            }
            case "voidReturn": {
                Block block = (Block) semanticStack.pop();
                FunctionDCL functionDcl = SymTabHandler.getInstance().getLastFunction();
                ReturnFunc funcReturn = new ReturnFunc(null, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "return": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                FunctionDCL functionDcl = SymTabHandler.getInstance().getLastFunction();
                ReturnFunc funcReturn = new ReturnFunc(exp, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "break": {
                semanticStack.push(new Break());
                break;
            }
            case "continue": {
                semanticStack.push(new Continue());
                break;
            }
            case "funcCall": {
                String name = (String) semanticStack.pop();
                semanticStack.push(new FuncCall(name, new ArrayList<>()));
                break;
            }
            case "addParam": {
                Expression exp = (Expression) semanticStack.pop();
                FuncCall funcCall = (FuncCall) semanticStack.pop();
                funcCall.addParam(exp);
                semanticStack.push(funcCall);
                break;
            }
            case "changeTop": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "trueInitFlag": {
                InitExp initExp = (InitExp) semanticStack.pop();
                semanticStack.pop();
                Byte flag = 1;
                semanticStack.push(initExp);
                semanticStack.push(flag);
                break;
            }
            case "trueStepFlag": {
                StepExp stepExp = (StepExp) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                if (flag == 0)
                    flag = 2;
                else
                    flag = 3;
                semanticStack.push(stepExp);
                semanticStack.push(flag);
                break;
            }
            case "for": {
                Block block = (Block) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                InitExp initExp = null;
                Expression exp;
                StepExp stepExp = null;
                if (flag == 0) {
                    exp = (Expression) semanticStack.pop();
                } else if (flag == 1) {
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                } else if (flag == 2) {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                } else {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                }
                semanticStack.push(new For(block, initExp, exp, stepExp));
                break;
            }
            case "repeat": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                semanticStack.push(new Repeat(block, exp));
                break;
            }
            case "if": {
                Block block = (Block) semanticStack.pop();
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new If(exp, block, null));
                break;
            }
            case "else": {
                Block block = (Block) semanticStack.pop();
                If ifSt = (If) semanticStack.pop();
                ifSt.setElseBlock(block);
                semanticStack.push(ifSt);
                break;
            }
            case "switch": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Switch(exp, new ArrayList<>(), null));
                break;
            }
            case "addCase": {
                Block block = (Block) semanticStack.pop();
                IntegerConst intConst = (IntegerConst) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                Case caseSt = new Case(intConst, block);
                switchSt.addCase(caseSt);
                semanticStack.push(switchSt);
                break;
            }
            case "addDefault": {
                Block defaultBlock = (Block) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                switchSt.setDefaultBlock(defaultBlock);
                semanticStack.push(switchSt);
                break;
            }
            case "print": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Println(expression));
                break;
            }
            case "printLine": {
                semanticStack.push(new Println(null));
                break;
            }
            case "input": {
                String type = (String) lexical.currentToken().getValue();
                semanticStack.push(new Input(SymTabHandler.getTypeFromName(type)));
                break;
            }
            case "inputLine": {
                semanticStack.push(new Input(null));
                break;
            }
            case "len": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Len(expression));
                break;
            }
            case "sizeof": {
                String id = (String) semanticStack.pop();
                String base = SymTabHandler.getInstance().getDescriptor(id).getType().toString();
                if (base.equals("C")) {
                    base = "char";
                }
                if (base.equals("I")) {
                    base = "int";
                }
                if (base.equals("Z")) {
                    base = "bool";
                }
                if (base.equals("J")) {
                    base = "long";
                }
                if (base.equals("D")) {
                    base = "double";
                }
                if (base.equals("F")) {
                    base = "float";
                }
                if (base.equals("Ljava/lang/String;")) {
                    base = "string";
                }
                semanticStack.push(new SizeOf(base));
                break;
            }
            case "myPush": {
                //  if (flag = true) {
                semanticStack.push(temp);
                counter++;
                //  flag = false;
                break;
                // }
            }
            default:
                throw new RuntimeException("Illegal semantic function: " + sem);
        }
    }

    private void addFuncToGlobalBlock(FunctionDCL function) {
        if (GlobalBlock.getInstance().getDeclarationList().contains(function)) {
            int index = GlobalBlock.getInstance().getDeclarationList().indexOf(function);
            FunctionDCL lastFunc = (FunctionDCL) GlobalBlock.getInstance().getDeclarationList().get(index);
            if (lastFunc.getBlock() == null && function.getBlock() != null) {
                GlobalBlock.getInstance().getDeclarationList().remove(lastFunc);
                GlobalBlock.getInstance().addDeclaration(function);
            } else if (lastFunc.getBlock() != null && lastFunc.getBlock() == null) {
            } else if (function.getType().equals(lastFunc.getType())) {
                throw new RuntimeException("the function is duplicate!!!");
            }
        } else {
            GlobalBlock.getInstance().addDeclaration(function);
        }
    }

    private void checkAssign(Var variable) {
        if (variable instanceof ArrVar) {
            ArrVar var = (ArrVar) variable;
            int numberOfExp = var.getDimensions().size();
            DCSP dscp = SymTabHandler.getInstance().getDescriptor(var.getName());
            if (dscp instanceof GlobalArrDCSP) {
                if (((GlobalArrDCSP) dscp).getDimNum() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
            if (dscp instanceof LocalArrDCSP) {
                if (((LocalArrDCSP) dscp).getDimNum() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
        }
        //  System.out.println("ok");
    }

    public static Long convertToLong(Object o) {
        String stringToConvert = String.valueOf(o);
        Long convertedLong = Long.parseLong(stringToConvert);
        return convertedLong;
    }

    public static Float convertToFloat(Object o) {
        String stringToConvert = String.valueOf(o);
        Float convertedFloat = Float.parseFloat(stringToConvert);
        return convertedFloat;
    }
}

class NOP implements Op {
    String name;

    public NOP(String name) {
        this.name = name;
    }

    public NOP() {
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
    }
}