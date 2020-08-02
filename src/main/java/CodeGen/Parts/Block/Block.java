package CodeGen.Parts.Block;

import CodeGen.Parts.Node;
import CodeGen.Parts.Op;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class Block implements Node {
    private ArrayList<Op> operations;

    public Block(ArrayList<Op> operations) {
        this.operations = operations;
    }

    public void addOperation(Op operation) {
        operations.add(operation);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if (operations == null)
            throw new RuntimeException("Error! No expression found!");
        for (Op op : operations) {
            op.codegen(mv, cw);
        }
    }
}