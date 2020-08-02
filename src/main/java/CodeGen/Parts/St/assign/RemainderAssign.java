package CodeGen.Parts.St.assign;

import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.LocalDCSP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class RemainderAssign extends Assignment {

    public RemainderAssign(Expression expression, Var variable) {
        super(expression, variable);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        checkConst();
        DCSP dscp = variable.getDSCP();
        variable.codegen(mv, cw);
        expression.codegen(mv, cw);
        if (variable.getType() != expression.getType())
            throw new RuntimeException("Error! you should cast!");
        mv.visitInsn(variable.getType().getOpcode(IREM));
        if (dscp instanceof LocalDCSP) {
            int index = ((LocalDCSP) dscp).getIndex();
            mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
        } else
            mv.visitFieldInsn(PUTSTATIC, "Main", variable.getName(), dscp.getType().toString());
    }
}

