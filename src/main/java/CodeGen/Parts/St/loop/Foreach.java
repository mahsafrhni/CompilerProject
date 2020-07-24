package CodeGen.Parts.St.loop;

import CodeGen.Parts.Block.Block;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class Foreach extends Loop {

    public Foreach(Block block) {
        super(block);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {

    }
}
