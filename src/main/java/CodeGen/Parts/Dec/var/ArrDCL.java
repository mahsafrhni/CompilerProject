package CodeGen.Parts.Dec.var;

import CodeGen.Parts.Expression.Const.IntegerConst;
import CodeGen.Parts.Expression.Expression;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.GlobalArrDCSP;
import CodeGen.SymTab.DSCP.LocalArrDCSP;
import CodeGen.SymTab.SymTabHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ASTORE;

@Data
public class ArrDCL extends VarDCL {
    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }

    private List<Expression> dimensions;
    private int dimNum;

    public ArrDCL(String name, Type type, boolean global, int dimNum) {
        this.name = name;
        this.type = type;
        this.global = global;
        dimensions = new ArrayList<>(dimNum);
        this.dimNum = dimNum;
    }

    public ArrDCL(String name, String stringType, boolean global, Integer dimNum, Type type, List<Expression> dimensions) {
        this.name = name;
        if (!stringType.equals("auto")) {
            if (!SymTabHandler.getTypeFromName(stringType).equals(type)) {
                throw new RuntimeException("Error! the types of array doesn't match");
            }
        } else if (dimensions == null) {
            throw new RuntimeException("Error! auto variables must have been initialized");
        }
        if (dimNum != null) {
            if (dimNum != dimensions.size()) {
                throw new RuntimeException("Error! dimensions are't correct");
            }
            this.dimNum = dimNum;
        }
        this.type = type;
        this.global = global;
        this.dimensions = dimensions;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if (global) {
            executeGlobalExp(cw, mv);
            String repeatedArray = new String(new char[dimensions.size()]).replace("\0", "[");
            Type arrayType = Type.getType(repeatedArray + type.getDescriptor());
            cw.visitField(ACC_STATIC, name, arrayType.getDescriptor(), null, null).visitEnd();
        } else {
            for (Expression dim : dimensions) {
                dim.codegen(mv, cw);
            }
            if (dimensions.size() == 0) {
                new IntegerConst(1000).codegen(mv, cw);
            }
            if (dimNum == 1) {
                if (type.getDescriptor().endsWith(";"))
                    mv.visitTypeInsn(ANEWARRAY, getType().getElementType().getInternalName());
                else
                    mv.visitIntInsn(NEWARRAY, SymTabHandler.getTType(getType().getElementType()));
            } else {
                String t = "";
                for (int i = 0; i < dimNum; i++) {
                    t += "[";
                }
                t += type.getDescriptor();
                mv.visitMultiANewArrayInsn(t, dimensions.size());
            }
            mv.visitVarInsn(ASTORE, SymTabHandler.getInstance().getIndex());
        }
    }

    private void executeGlobalExp(ClassWriter cw, MethodVisitor mv) {
        for (Expression dim :
                dimensions) {
            dim.codegen(mv, cw);
        }
    }

    public static void declare(String name, Type type, List<Expression> dimensions, int dimNum, boolean global) {
        DCSP dscp;
        if (!global)
            dscp = new LocalArrDCSP(type, true, SymTabHandler.getInstance().getIndex(), dimensions, dimNum);
        else
            dscp = new GlobalArrDCSP(type, true, dimensions, dimNum);
        SymTabHandler.getInstance().addVariable(name, dscp);
    }
}