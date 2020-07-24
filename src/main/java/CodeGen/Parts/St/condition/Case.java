package CodeGen.Parts.St.condition;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.St.Statement;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Case extends Statement {
    IntegerConst exp;
    private Block block;
    Label StartCase = new Label();
    Label jump;
    public Case(IntegerConst exp, Block block){
        this.exp = exp;
        this.block = block;
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        mv.visitLabel(StartCase);
        SymTabHandler.getInstance().addScope(Scope.SWITCH);
        block.codegen(mv, cw);
        SymTabHandler.getInstance().popScope();
        mv.visitJumpInsn(GOTO,jump);
    }
}

