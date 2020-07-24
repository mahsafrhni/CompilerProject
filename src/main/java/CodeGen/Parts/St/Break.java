package CodeGen.Parts.St;

import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTab;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Break extends Statement {
    public Break() {
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if (SymTabHandler.getInstance().canHaveBreak()) {
            int i = SymTabHandler.getInstance().getStackScopes().size() - 1;
            for (; i >= 0; i--) {
                SymTab scope = SymTabHandler.getInstance().getStackScopes().get(i);
                if (scope.getTypeOfScope() == Scope.LOOP) {
                    mv.visitJumpInsn(GOTO, SymTabHandler.getInstance().getInnerLoop().getEnd());
                    return;
                } else if (scope.getTypeOfScope() == Scope.SWITCH) {
                    mv.visitJumpInsn(GOTO, SymTabHandler.getInstance().getLastSwitch().getEnd());
                    return;
                }
            }
        } else
            throw new RuntimeException("This part is not switch nor Loop");
    }
}
