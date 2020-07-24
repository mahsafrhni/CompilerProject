package CodeGen.Parts.Expression.binary.arithmetic;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.binary.BinaryExperession;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.IDIV;
public class Divide extends BinaryExperession {
    public Divide(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if(!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("types not match for " + this.getClass().getName());
        type = firstop.getType();
        mv.visitInsn(type.getOpcode(IDIV));
    }
}