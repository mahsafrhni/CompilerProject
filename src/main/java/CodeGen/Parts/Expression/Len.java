package CodeGen.Parts.Expression;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Expression.var.ArrVar;
import CodeGen.Parts.Op;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ARRAYLENGTH;

public class Len extends Expression implements Op {
    private Expression expression;

    public Len(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        expression.codegen(mv, cw);
        type = expression.getType();
        if (expression instanceof ArrVar) {
            mv.visitInsn(ARRAYLENGTH);
        } else
            throw new RuntimeException("input of len function is not iterable");
    }
}

