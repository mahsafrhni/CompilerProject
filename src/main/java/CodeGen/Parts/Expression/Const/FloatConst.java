package CodeGen.Parts.Expression.Const;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FCONST_1;

public class FloatConst extends ConstExpression {
    private Float value;

    public FloatConst(Float value) {
        this.value = value;
        type = Type.FLOAT_TYPE;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if (value == 0)
            mv.visitInsn(FCONST_0);
        else if (value == 1)
            mv.visitInsn(FCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}

