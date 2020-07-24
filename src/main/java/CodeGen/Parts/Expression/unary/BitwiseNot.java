package CodeGen.Parts.Expression.unary;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IXOR;

public class BitwiseNot extends UnaryExpression {  //~
    public BitwiseNot(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        operand.codegen(mv, cw);
        if(operand.getType() != Type.INT_TYPE && operand.getType() != Type.LONG_TYPE)
            throw new RuntimeException("It's not real or integer.so I can't not it!");
        mv.visitInsn(ICONST_M1);
        type = operand.getType();
        mv.visitInsn(type.getOpcode(IXOR));
    }
}
