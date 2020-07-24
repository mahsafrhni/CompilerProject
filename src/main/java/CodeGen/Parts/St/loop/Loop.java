package CodeGen.Parts.St.loop;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.St.Statement;
import org.objectweb.asm.Label;

public abstract class Loop extends Statement {
    protected Block block;
    Label startLoop = new Label();
    Label end = new Label();
    Loop(Block block) {
        this.block = block;
    }
}

