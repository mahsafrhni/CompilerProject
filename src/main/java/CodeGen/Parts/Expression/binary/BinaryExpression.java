package CodeGen.Parts.Expression.binary;

import CodeGen.Parts.Expression.Expression;

abstract public class BinaryExpression extends Expression {
    protected Expression firstop;
    protected Expression secondop;


    public BinaryExpression(Expression firstop, Expression secondop) {
        this.firstop = firstop;
        this.secondop = secondop;

    }
}