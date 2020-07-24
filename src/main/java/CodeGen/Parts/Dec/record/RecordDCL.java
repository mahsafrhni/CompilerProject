package CodeGen.Parts.Dec.record;

import CodeGen.Parts.Dec.Dec;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

@Data
public class RecordDCL implements Dec {
    private String name;

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
    }
}