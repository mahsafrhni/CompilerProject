package CodeGen.Parts.St;

import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Continue extends Statement {

    public Continue() {}

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if(SymTabHandler.getInstance().getInnerLoop() != null)
            mv.visitJumpInsn(GOTO, SymTabHandler.getInstance().getInnerLoop().getStartLoop());
        else
            throw new RuntimeException("This part is not switch nor Loop");
    }
}
