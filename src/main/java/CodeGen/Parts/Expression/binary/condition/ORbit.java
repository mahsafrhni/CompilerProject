package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.binary.BinaryExpression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IOR;

public class ORbit extends BinaryExpression {
    public ORbit(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if (!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("Error! types not match for " + this.getClass().getName());
        type = firstop.getType();
        mv.visitInsn(type.getOpcode(IOR));
    }
}
