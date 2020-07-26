package CodeGen.SymTab.DSCP;

import CodeGen.Parts.Expression.Expression;
import lombok.Data;
import org.objectweb.asm.Type;

import java.util.List;

@Data
public class LocalArrDCSP extends LocalDCSP {
    public void setDimList(List<Expression> dimList) {
        this.dimList = dimList;
    }

    public int getDimNum() {
        return dimNum;
    }

    protected List<Expression> dimList;
    protected int dimNum;

    public LocalArrDCSP(Type type, boolean isValid, int index, List<Expression> dimList, int dimNum) {
        super(type, isValid, index);
        this.dimList = dimList;
        this.dimNum = dimNum;
    }

    public LocalArrDCSP(Type type, boolean isValid, int index, int dimNum) {
        super(type, isValid, index);
        this.dimNum = dimNum;
    }
}

