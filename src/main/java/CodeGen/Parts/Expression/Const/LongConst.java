package CodeGen.Parts.Expression.Const;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;

public class LongConst extends ConstExpression {
    private Long value;

    public LongConst(Long value) {
        this.value = value;
        type = Type.LONG_TYPE;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if (value == 0)
            mv.visitInsn(LCONST_0);
        else if (value == 1)
            mv.visitInsn(LCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}

