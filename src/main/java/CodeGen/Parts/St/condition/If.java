package CodeGen.Parts.St.condition;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.binary.condition.NotEqual;
import CodeGen.Parts.St.Statement;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;

@Data
public class If extends Statement {

    private Expression expression;
    private Block ifBlock;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Block getIfBlock() {
        return ifBlock;
    }

    public void setIfBlock(Block ifBlock) {
        this.ifBlock = ifBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(Block elseBlock) {
        this.elseBlock = elseBlock;
    }

    public Label getStartElse() {
        return startElse;
    }

    public void setStartElse(Label startElse) {
        this.startElse = startElse;
    }

    public Label getEndElse() {
        return endElse;
    }

    public void setEndElse(Label endElse) {
        this.endElse = endElse;
    }

    private Block elseBlock;
    private Label startElse = new Label();
    private Label endElse = new Label();

    public If(Expression expression, Block ifBlock, Block elseBlock) {
        this.expression = expression;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymTabHandler.getInstance().addScope(Scope.IF);
        NotEqual notEqual = new NotEqual(expression, new IntegerConst(0));
        notEqual.codegen(mv, cw);
        mv.visitJumpInsn(IFEQ, startElse);
        ifBlock.codegen(mv, cw);
        mv.visitJumpInsn(GOTO, endElse);
        SymTabHandler.getInstance().popScope();
        if (elseBlock != null) {
            SymTabHandler.getInstance().addScope(Scope.IF);
            mv.visitLabel(startElse);
            elseBlock.codegen(mv, cw);
            SymTabHandler.getInstance().popScope();
        }
        else
            mv.visitLabel(startElse);
        mv.visitLabel(endElse);
    }
}
