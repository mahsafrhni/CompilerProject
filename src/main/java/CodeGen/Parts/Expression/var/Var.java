package CodeGen.Parts.Expression.var;

import CodeGen.Parts.Expression.Expression;
import CodeGen.SymTab.DSCP.DCSP;
import CodeGen.SymTab.SymTabHandler;
import org.objectweb.asm.Type;

public abstract class Var extends Expression {
    String name;

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return getDSCP().getType();
    }

    public DCSP getDSCP() {
        return SymTabHandler.getInstance().getDescriptor(name);
    }
}

