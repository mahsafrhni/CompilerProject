package CodeGen.Parts.Expression.binary.condition;

import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.binary.BinaryExpression;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_1;

abstract class ConditionalExpression extends BinaryExpression {
    ConditionalExpression(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    void cmp(int notIntOpcode, int intOpcode, MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        System.out.println(firstop.getType());
        System.out.println(secondop.getType());
        if (!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("Error! types not match for " + this.getClass().getName());
        type = firstop.getType();
        int opcode;
        if (type == Type.FLOAT_TYPE) {
            mv.visitInsn(Opcodes.FCMPG);
            opcode = notIntOpcode;
        } else if (type == Type.DOUBLE_TYPE) {
            mv.visitInsn(Opcodes.DCMPG);
            opcode = notIntOpcode;
        } else if (type == Type.LONG_TYPE) {
            mv.visitInsn(Opcodes.LCMP);
            opcode = notIntOpcode;
        } else {
            opcode = intOpcode;
        }
        Label label1 = new Label();
        Label label2 = new Label();
        mv.visitJumpInsn(opcode, label1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, label2);
        mv.visitLabel(label1);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(label2);
    }

    void AndOr(boolean isAnd, MethodVisitor mv, ClassWriter cw) {
        Label label = new Label();
        Label EndLabel = new Label();
        firstop.codegen(mv, cw);
        mv.visitJumpInsn(isAnd ? IFEQ : IFNE, label);
        secondop.codegen(mv, cw);
        mv.visitJumpInsn(isAnd ? IFEQ : IFNE, label);
        mv.visitInsn(isAnd ? ICONST_1 : ICONST_0);
        mv.visitJumpInsn(GOTO, EndLabel);
        mv.visitLabel(label);
        mv.visitInsn(isAnd ? ICONST_0 : ICONST_1);
        mv.visitLabel(EndLabel);
        if (!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("Error! types not match for " + this.getClass().getName());
        type = firstop.getType();
        if (type != Type.BOOLEAN_TYPE && type != Type.INT_TYPE)
            throw new RuntimeException("Error! Only boolean and int Types Can Be Operands Of Conditional And");
    }
}

