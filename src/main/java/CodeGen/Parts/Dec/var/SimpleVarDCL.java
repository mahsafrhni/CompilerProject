package CodeGen.Parts.Dec.var;
import CodeGen.Parts.Expression.Expression;
import CodeGen.Parts.Expression.var.SimpleVar;
import CodeGen.Parts.Expression.var.Var;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.DSCP.GlobalVarDCSP;
import CodeGen.SymTab.DSCP.LocalDCSP;
import CodeGen.SymTab.DSCP.LocalVarDCSP;
import CodeGen.SymTab.SymTabHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static jdk.internal.org.objectweb.asm.Opcodes.ISTORE;
import static jdk.internal.org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;

@Data
public class SimpleVarDCL extends VarDCL {
    private boolean constant;
    private Expression exp;
    private String stringType;
    public void setExp(Expression exp) {
        this.exp = exp;
        SymTabHandler.getInstance().getDescriptor(name).setValid(true);
    }
    public SimpleVarDCL(String varName, Type type, boolean constant, boolean global) {
        name = varName;
        this.type = type;
        this.constant = constant;
        this.global = global;
    }
    public SimpleVarDCL(String varName, String type, boolean constant, boolean global, Expression exp) {
        name = varName;
        stringType = type;
        if (!type.equals("auto"))
            this.type = SymTabHandler.getTypeFromName(type);
        else
            this.type = null;
        this.constant = constant;
        this.global = global;
        this.exp = exp;
        if (this.type == null)
            if (exp == null)
                throw new RuntimeException("the auto variable must be have expression");
            else
                phonyExpExe();
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        try{
            SymTabHandler.getInstance().getDescriptor(name);
        }catch (Exception e){
            declare();
        }
        if (global) {
            Expression value = null;
            int access = ACC_STATIC;
            access += constant ? ACC_FINAL : 0;
            cw.visitField(access, name, type.getDescriptor(),
                    null, value).visitEnd();
            if (exp != null) {
                executeGlobalExp(cw, mv);
            }
        } else if (exp != null) {
            exp.codegen(mv, cw);
            if (!exp.getType().equals(type))
                throw new RuntimeException("the type of variable and expression doesn't match" +
                        "   " + "the type of var " + type + "   " + "the type of exp " + exp.getType());
            LocalVarDCSP dscp = (LocalVarDCSP) SymTabHandler.getInstance().getDescriptor(name);
            mv.visitVarInsn(type.getOpcode(ISTORE), dscp.getIndex());
        }
    }
    private void phonyExpExe() {
        TempMethodVisitor tempMV = new TempMethodVisitor();
        TempClassWriter tempCW = new TempClassWriter();
        exp.codegen(tempMV, tempCW);
        type = exp.getType();
    }
    private void executeGlobalExp(ClassWriter cw, MethodVisitor mv) {
        assign(new SimpleVar(name, type), exp, mv, cw);
    }
    public void declare() {
        DCSP dscp;
        if (!global)
            dscp = new LocalVarDCSP(type, exp != null,
                    SymTabHandler.getInstance().getIndex(), constant);
        else
            dscp = new GlobalVarDCSP(type, exp != null, constant);
        SymTabHandler.getInstance().addVariable(name, dscp);
    }
    private void assign(Var variable, Expression expression,
                        MethodVisitor mv, ClassWriter cw) {
        DCSP dscp = variable.getDSCP();
        expression.codegen(mv, cw);
        if (variable.getType() != expression.getType())
            throw new RuntimeException("you should cast expression!");
        if (dscp instanceof LocalDCSP) {
            int index = ((LocalDCSP) dscp).getIndex();
            mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
        } else
            mv.visitFieldInsn(PUTSTATIC, "Test", variable.getName(), dscp.getType().toString());
        dscp.setValid(true);
    }
}


class TempMethodVisitor extends MethodVisitor {
    public TempMethodVisitor() {
        super(327680);
    }
}

class TempClassWriter extends ClassWriter {
    public TempClassWriter() {
        super(327680);
    }
}