package CodeGen.SymTab.DSCP;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class GlobalDCSP extends DCSP{

    public GlobalDCSP(Type type, boolean isValid) {
        super(type, isValid);
    }
}
