package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;

public class Equal extends ConditionalExpression {
    public Equal(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(IFNE, IF_ICMPNE, mv, cw);
    }
}