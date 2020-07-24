package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class And extends ConditionalExpression {
    public And(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        AndOr(true, mv, cw);
    }
}
