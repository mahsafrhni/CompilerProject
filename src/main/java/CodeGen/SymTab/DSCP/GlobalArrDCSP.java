package CodeGen.SymTab.DSCP;

import CodeGen.Parts.Expression.Expression;
import lombok.Data;
import org.objectweb.asm.Type;

import java.util.List;

@Data
public class GlobalArrDCSP extends GlobalDCSP {

    protected List<Expression> dimList;
    protected int dimNum;

    public GlobalArrDCSP(Type type, boolean isValid, List<Expression> dimList, int dimNum) {
        super(type, isValid);
        this.dimList = dimList;
        this.dimNum = dimNum;
    }

    public GlobalArrDCSP(Type type, boolean isValid, int dimNum) {
        super(type, isValid);
        this.dimNum = dimNum;
    }
}

