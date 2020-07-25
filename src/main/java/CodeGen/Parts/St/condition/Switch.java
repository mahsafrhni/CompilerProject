package CodeGen.Parts.St.condition;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.St.Statement;
import CodeGen.SymTab.Scope;
import CodeGen.SymTab.SymTabHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.GOTO;

@Data
public class Switch extends Statement {
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

    public void setCases(ArrayList<Case> cases) {
        this.cases = cases;
    }

    public Block getDefaultBlock() {
        return defaultBlock;
    }

    public void setDefaultBlock(Block defaultBlock) {
        this.defaultBlock = defaultBlock;
    }

    public Label getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(Label defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    public Label getLookUpTable() {
        return lookUpTable;
    }

    public void setLookUpTable(Label lookUpTable) {
        this.lookUpTable = lookUpTable;
    }

    public Label getEnd() {
        return end;
    }

    public void setEnd(Label end) {
        this.end = end;
    }

    private Expression expression;
    private ArrayList<Case> cases;
    private Block defaultBlock;
    private Label defaultLabel = new Label();
    private Label lookUpTable = new Label();
    private Label end = new Label();

    public Switch(Expression expression, ArrayList<Case> cases, Block defaultBlock){
        this.expression = expression;
        this.cases = cases;
        this.defaultBlock = defaultBlock;
    }

    public void addCase(Case caseSt){
        if(cases == null)
            cases = new ArrayList<>();
        cases.add(caseSt);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymTabHandler.getInstance().addScope(Scope.SWITCH);
        SymTabHandler.getInstance().setLastSwitch(this);
        Label [] labels = new Label[cases.size()];
        int [] keys = new int[cases.size()];
        int i = 0 ;
        expression.codegen(mv, cw);
        mv.visitJumpInsn(GOTO, lookUpTable);
        for(Case c : cases){
            c.jump = end;
            c.codegen(mv, cw);
            labels[i] = c.StartCase;
            keys[i++] = (int) c.exp.getValue();
        }
        mv.visitLabel(defaultLabel);
        if (defaultBlock != null) {
            SymTabHandler.getInstance().addScope(Scope.SWITCH);
            defaultBlock.codegen(mv, cw);
            SymTabHandler.getInstance().popScope();
        }
        mv.visitJumpInsn(GOTO, end);
        mv.visitLabel(lookUpTable);
        mv.visitLookupSwitchInsn(defaultLabel, keys, labels);
        mv.visitLabel(end);
        SymTabHandler.getInstance().popScope();
        SymTabHandler.getInstance().setLastSwitch(null);
    }
}

