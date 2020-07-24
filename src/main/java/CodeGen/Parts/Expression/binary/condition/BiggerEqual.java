package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BiggerEqual extends ConditionalExpression {
    public BiggerEqual(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(Opcodes.IFLT, Opcodes.IF_ICMPLT, mv, cw);
    }
}
