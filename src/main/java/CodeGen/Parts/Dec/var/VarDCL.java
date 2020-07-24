package CodeGen.Parts.Dec.var;

import CodeGen.Parts.Dec.Dec;
import CodeGen.Parts.Op;
import CodeGen.Parts.St.loop.InitExp;
import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class VarDCL implements Op, InitExp, Dec {
    protected String name;
    protected Type type = null;
    protected boolean global = true;
}
