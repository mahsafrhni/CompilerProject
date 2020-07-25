package CodeGen.SymTab.DSCP;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class LocalDCSP extends DCSP {

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    protected int index;

    public LocalDCSP(Type type, boolean isValid, int index) {
        super(type, isValid);
        this.index = index;
    }
}