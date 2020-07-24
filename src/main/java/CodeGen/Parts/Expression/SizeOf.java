package CodeGen.Parts.Expression;

import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Op;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class Sizeof extends Expression implements Op {
    private Integer value;

    public Sizeof(String baseType) {
        value = SymTabHandler.getSize(baseType);
        type = Type.INT_TYPE;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        IntegerConst.storeIntValue(mv, value);
    }
}
