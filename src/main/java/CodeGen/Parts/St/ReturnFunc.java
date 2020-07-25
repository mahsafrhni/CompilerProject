package CodeGen.Parts.St;

import CodeGen.Parts.Dec.function.FunctionDCL;
import CodeGen.Parts.Expression.Expression;
import CodeGen.SymTab.SymTab;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

public class ReturnFunc extends Statement {

    private Expression expression;
    private SymTab scope;

    public ReturnFunc(Expression expression, FunctionDCL funcDcl) {
        this.expression = expression;
        funcDcl.addReturn(this);
        if((expression == null && !funcDcl.getType().equals(Type.VOID_TYPE)) ||
                (expression != null && (funcDcl.getType().equals(Type.VOID_TYPE) ||
                        !funcDcl.getType().equals(expression.getType()) )))
            throw new RuntimeException("Return type mismatch");
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        FunctionDCL functionDcl = SymTabHandler.getInstance().getLastFunction();
        scope = SymTabHandler.getInstance().getLastScope();
        int index = functionDcl.getReturns().indexOf(this);
        for (int i = 0; i < index; i++)  {
            ReturnFunc funcReturn = functionDcl.getReturns().get(i);
            if(funcReturn.scope.equals(scope)) {
                throw new RuntimeException("more than one return in single scope -__-");
            }
        }
        if(expression == null) {
            mv.visitInsn(RETURN);
        }
        else {
            expression.codegen(mv, cw);
            //mv.visitInsn(Cast.getOpcode(expression.getType(),functionDcl.getType()));
            if(!expression.getType().equals(functionDcl.getType()))
                throw new RuntimeException("Return types don't match");
            mv.visitInsn(expression.getType().getOpcode(IRETURN));
        }

    }
}
