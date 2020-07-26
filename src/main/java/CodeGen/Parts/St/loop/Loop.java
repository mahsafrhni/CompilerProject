package CodeGen.Parts.St.loop;

import CodeGen.Parts.Block.Block;
import CodeGen.Parts.St.Statement;
import org.objectweb.asm.Label;

public abstract class Loop extends Statement {
    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Label getStartLoop() {
        return startLoop;
    }

    public Label getEnd() {
        return end;
    }

    public void setEnd(Label end) {
        this.end = end;
    }

    protected Block block;
    Label startLoop = new Label();
    Label end = new Label();
    Loop(Block block) {
        this.block = block;
    }
}

