package CodeGen.Parts.Expression.unary;

import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.var.SimpleVar;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.GlobalVarDCSP;
import CodeGen.SymTab.DSCP.LocalVarDCSP;
import CodeGen.SymTab.SymTabHandler;

abstract public class UnaryExpression extends Expression {
    protected Expression operand;
    UnaryExpression(Expression operand){
        this.operand = operand;
    }
    //This is just for postpp,prepp,...
    protected void checkConst(Var variable) {
        boolean isConst = false;
        if (variable instanceof SimpleVar) {
            DCSP dscp = SymTabHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDCSP) ? ((GlobalVarDCSP) dscp).isConstant() : ((LocalVarDCSP) dscp).isConstant();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign");
    }
}

