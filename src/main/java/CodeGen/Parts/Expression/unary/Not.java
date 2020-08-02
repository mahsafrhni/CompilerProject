package CodeGen.Parts.Expression.unary;

import CodeGen.Parts.Expression.Const.ConstExpression;
import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;

public class Not extends UnaryExpression { //not
    public Not(Expression operand) {
        super(operand);
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        operand.codegen(mv,cw);
        type = operand.getType();
        if(type != Type.INT_TYPE && type != Type.LONG_TYPE && type != Type.BOOLEAN_TYPE)
            throw new RuntimeException("Error! It's not real or integer or bool .so you can't not it!");
        Object res = ((ConstExpression)operand).getValue();
        if(res instanceof Boolean)
            mv.visitInsn(((Boolean)res) ? ICONST_1:ICONST_0);
        if(res instanceof Integer)
            mv.visitInsn(((Integer)res) != 0 ? ICONST_1:ICONST_0);
        if(res instanceof Long)
            mv.visitInsn(((Long)res) != 0 ? ICONST_1:ICONST_0);
    }
}

