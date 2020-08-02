package CodeGen.Parts.Expression;

import CodeGen.Parts.Dec.function.FunctionDCL;
import CodeGen.Parts.Op;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class FuncCall extends Expression implements Op {
    private String id;
    private List<Expression> parameters;

    public FuncCall(String id, ArrayList<Expression> parameters) {
        this.id = id;
        this.parameters = parameters;
    }

    public void addParam(Expression exp) {
        if (parameters == null)
            parameters = new ArrayList<>();
        parameters.add(exp);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        for (Expression parameter : parameters) {
            parameter.codegen(mv, cw);
        }
        ArrayList<Type> paramTypes = new ArrayList<>();
        for (Expression exp : parameters) {
            paramTypes.add(exp.getType());
        }
        FunctionDCL func = SymTabHandler.getInstance().getFunction(id, paramTypes);
        this.type = func.getType();
        if (parameters.size() != func.getParameters().size())
            throw new RuntimeException("Error! error in function parameter");
        mv.visitMethodInsn(INVOKESTATIC, "Main", func.getName(), func.getSignature(), false);
    }
}
