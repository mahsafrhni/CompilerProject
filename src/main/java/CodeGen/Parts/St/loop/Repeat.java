package CodeGen.Parts.St.loop;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.binary.condition.NotEqual;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFNE;

public class Repeat extends Loop{
    private Expression expression;

    public Repeat(Block block, Expression expression) {
        super(block);
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymTabHandler.getInstance().addScope(Scope.LOOP);
        SymTabHandler.getInstance().setInnerLoop(this);
        mv.visitLabel(startLoop);
        block.codegen(mv, cw);
        NotEqual notEqual = new NotEqual(expression, new IntegerConst(0));
        notEqual.codegen(mv, cw);
        mv.visitJumpInsn(IFNE, startLoop);
        mv.visitLabel(end);
        SymTabHandler.getInstance().popScope();
        SymTabHandler.getInstance().setInnerLoop(null);
    }
}
