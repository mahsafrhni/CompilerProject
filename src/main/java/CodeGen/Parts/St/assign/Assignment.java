package CodeGen.Parts.St.assign;

import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.var.SimpleVar;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.Parts.St.Statement;
import CodeGen.Parts.St.loop.InitExp;
import CodeGen.Parts.St.loop.StepExp;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.GlobalVarDCSP;
import CodeGen.SymTab.DSCP.LocalVarDCSP;
import CodeGen.SymTab.SymTabHandler;

public abstract class Assignment extends Statement implements InitExp, StepExp {
    protected Expression expression;
    protected Var variable;

    Assignment(Expression expression, Var variable) {
        this.expression = expression;
        this.variable = variable;
    }

    protected void checkConst() {
        boolean isConst = false;
        if (variable instanceof SimpleVar) {
            DCSP dscp = SymTabHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDCSP) ? ((GlobalVarDCSP) dscp).isConstant() : ((LocalVarDCSP) dscp).isConstant();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign");
    }

}

