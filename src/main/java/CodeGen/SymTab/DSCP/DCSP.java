package CodeGen.SymTab.DSCP;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class DCSP {
    protected Type type;
    protected boolean isValid;
    protected boolean constant;

    public DCSP(Type type, boolean isValid) {
        this.type = type;
        this.isValid = isValid;
    }
}