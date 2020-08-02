package CodeGen.Parts.Dec.function;
import CodeGen.Parts.Block.Block;
import CodeGen.SymTab.DSCP.LocalArrDCSP;
import CodeGen.SymTab.DSCP.LocalDCSP;
import CodeGen.Parts.Dec.Dec;
import CodeGen.Parts.St.ReturnFunc;
import CodeGen.SymTab.DSCP.LocalVarDCSP;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class FunctionDCL implements Dec {
    private Type type;
    private String name;
    private List<ParamPair> parameters;
    private List<Type> paramTypes = new ArrayList<>();
    private String signature;
    private Block block;
    private List<ReturnFunc> returns = new ArrayList<>();
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamPair> getParameters() {
        return parameters;
    }

    public String getSignature() {
        return signature;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<ReturnFunc> getReturns() {
        return returns;
    }

    public void addReturn(ReturnFunc funcReturn) {
        returns.add(funcReturn);
    }

    public void addParameter(String name, LocalDCSP dscp) {
        ParamPair param = new ParamPair(name, dscp);
        parameters.add(param);
        if (dscp instanceof LocalVarDCSP)
            paramTypes.add(dscp.getType());
        else if (dscp instanceof LocalArrDCSP)
            paramTypes.add(Type.getType("[" + dscp.getType()));
    }

    public FunctionDCL(Type type, String name, Block block, List<ParamPair> parameters) {
        this.type = type;
        this.name = name;
        this.block = block;
        this.parameters = parameters;
        // to fill paramTypes and make signature
        setSig();
    }
    public void declare() {
        SymTabHandler.getInstance().addFunction(this);
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        setSig();
        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                name, this.signature, null, null);
        //Add current function's symbol table to stackScope
        SymTabHandler.getInstance().addScope(Scope.FUNCTION);
        parameters.forEach((paramPair) -> SymTabHandler.getInstance().addVariable(paramPair.name, paramPair.dscp));
        SymTabHandler.getInstance().setLastFunction(this);
        methodVisitor.visitCode();
        block.codegen(methodVisitor, cw);
        if (returns.size() == 0)
            throw new RuntimeException("You must use at least one return statement in function!");
//        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        SymTabHandler.getInstance().popScope();
        SymTabHandler.getInstance().setLastFunction(null);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FunctionDCL && checkIfEqual(((FunctionDCL) o).name, ((FunctionDCL) o).paramTypes);
    }

    // check if two functions are the same
    public boolean checkIfEqual(String name, List<Type> paramTypes) {
        if (!this.name.equals(name))
            return false;
        if (paramTypes.size() != this.paramTypes.size())
            return false;
        for (int i = 0; i < paramTypes.size(); i++) {
            if (!this.paramTypes.get(i).equals(paramTypes.get(i)))
                return false;
        }
        return true;
    }

    private void setSig() {
        paramTypes = new ArrayList<>();
        // to fill paramTypes and make signature
        StringBuilder signature = new StringBuilder("(");
        for (ParamPair param :
                parameters) {
            Type type = param.dscp.getType();
            if (param.dscp instanceof LocalArrDCSP)
                type = Type.getType("[" + param.dscp.getType());
            paramTypes.add(type);
            signature.append(type);
        }
        signature.append(")");
        signature.append(type.toString());
        this.signature = signature.toString();
    }
}

class ParamPair {
    String name;
    LocalDCSP dscp;

    public ParamPair(String name, LocalDCSP dscp) {
        this.name = name;
        this.dscp = dscp;
    }
}