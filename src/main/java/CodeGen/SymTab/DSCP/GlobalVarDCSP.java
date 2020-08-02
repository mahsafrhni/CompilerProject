package CodeGen.SymTab.DSCP;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public class GlobalVarDCSP extends GlobalDCSP {
    public GlobalVarDCSP(Type type, boolean isValid, boolean constant) {
        super(type, isValid);
        this.constant = constant;
    }
}

