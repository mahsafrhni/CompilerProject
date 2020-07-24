package CodeGen.Parts.Expression.var;

import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.LocalVarDCSP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;

public class SimpleVar extends Var{

    public SimpleVar(String name, Type type){
        this.type = type;
        this.name = name;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        DCSP dscp = getDSCP();
        if(!dscp.isValid())
            throw new RuntimeException("you should set initial value to variable");
        if (dscp instanceof LocalVarDCSP) {
            int index = ((LocalVarDCSP) dscp).getIndex();
            mv.visitVarInsn(type.getOpcode(ILOAD), index);
        } else {
            mv.visitFieldInsn(GETSTATIC,"Main" , name, type.getDescriptor());
        }
    }
}

