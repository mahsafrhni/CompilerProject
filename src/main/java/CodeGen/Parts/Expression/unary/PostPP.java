package CodeGen.Parts.Expression.unary;

import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.var.SimpleVar;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.Parts.Op;
import CodeGen.Parts.St.assign.SumAssign;
import CodeGen.Parts.St.loop.InitExp;
import CodeGen.Parts.St.loop.StepExp;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PostPP extends UnaryExpression implements InitExp, StepExp, Op { //post plus plus like a++
    public PostPP(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        type = operand.getType();
        if (!(operand instanceof Var) || (type != Type.INT_TYPE && type != Type.LONG_TYPE ))
            throw new RuntimeException("Error! the operand is wrong");
        Var var = (Var) operand;
        checkConst(var);
        // storing the last value in temporary variable
        new SimpleVar(var.getName(), var.getType()).codegen(mv, cw);
        new SumAssign(new IntegerConst(1), var).codegen(mv, cw);
    }
}
