package CodeGen.Parts.Expression.Const;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;

public class BoolConst extends ConstExpression {
    private Boolean value;

    public BoolConst(Boolean value) {
        this.value = value;
        type = Type.BOOLEAN_TYPE;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        mv.visitInsn(value ? ICONST_1 : ICONST_0);
    }
}
