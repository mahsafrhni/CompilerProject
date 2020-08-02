package CodeGen.Parts.St;

import CodeGen.Parts.Expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Println extends Statement {

    private Expression expression;

    public Println(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (expression == null) {
            mv.visitLdcInsn("\n");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V", false);
        } else {
            expression.codegen(mv, cw);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(" + expression.getType() + ")V", false);
        }
    }
}

