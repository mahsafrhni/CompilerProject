package CodeGen.Parts.Expression;

import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Op;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class SizeOf extends Expression implements Op {
    private Integer value;

    public SizeOf(String id) {
        value = SymTabHandler.getSize(id);
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
