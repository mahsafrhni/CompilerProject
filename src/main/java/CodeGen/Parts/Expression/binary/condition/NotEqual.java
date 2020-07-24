package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;

public class NotEqual extends ConditionalExpression {
    public NotEqual(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(IFEQ, IF_ICMPEQ, mv, cw);
    }
}