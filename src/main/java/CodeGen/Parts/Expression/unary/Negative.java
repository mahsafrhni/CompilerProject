package CodeGen.Parts.Expression.unary;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INEG;

public class Negative extends UnaryExpression { //-
    public Negative(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        operand.codegen(mv, cw);
        if (type != Type.BOOLEAN_TYPE)
            throw new RuntimeException("Error! It's not a number!");
        type = operand.getType();
        mv.visitInsn(type.getOpcode(INEG));
    }
}
