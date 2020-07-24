package CodeGen.Parts.Expression;

import CodeGen.Parts.Node;
import lombok.Data;
import org.objectweb.asm.Type;

@Data
abstract public class Expression implements Node {
    protected Type type;

    public Type getType() {
        if (type == null)
            throw new RuntimeException("The type of expression is not set!");
        return type;
    }
}
