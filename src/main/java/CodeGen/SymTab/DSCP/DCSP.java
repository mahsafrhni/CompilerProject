package CodeGen.SymTab.DSCP;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class DCSP {
    protected Type type;
    protected boolean isValid;
    protected boolean constant;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public DCSP(Type type, boolean isValid) {
        this.type = type;
        this.isValid = isValid;
    }
}