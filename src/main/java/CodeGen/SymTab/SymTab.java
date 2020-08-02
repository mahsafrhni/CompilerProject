package CodeGen.SymTab;

import CodeGen.SymTab.DSCP.DCSP;
import lombok.Data;

import java.util.HashMap;

@Data
public class SymTab extends HashMap<String, DCSP> {
    private int index = 0;
    private Scope typeOfScope;

    public void setIndex(int index) {
        this.index = index;
    }

    public Scope getTypeOfScope() {
        return typeOfScope;
    }

    public void setTypeOfScope(Scope typeOfScope) {
        this.typeOfScope = typeOfScope;
    }

    public void addIndex() {
        index++;
    }

    public int getIndex() {
        return index;
    }
}
