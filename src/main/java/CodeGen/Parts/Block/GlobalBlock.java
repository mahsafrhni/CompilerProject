package CodeGen.Parts.Block;

import CodeGen.Parts.Dec.Dec;
import CodeGen.Parts.Expression.FuncCall;
import CodeGen.Parts.Node;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

@Data
public class GlobalBlock implements Node {
    public List<Node> getDeclarationList() {
        return declarationList;
    }

    private List<Node> declarationList;
    private static GlobalBlock instance = new GlobalBlock();

    public static GlobalBlock getInstance() {
        return instance;
    }

    private GlobalBlock() {
        this.declarationList = new ArrayList<>();
    }

    public void addDeclaration(Dec declaration) {
        declarationList.add(declaration);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        new FuncCall("start", new ArrayList<>()).codegen(mv, cw);
       // mv = cw.visitMethod(ACC_STATIC, "<clinit>",
      //          "()V", null, null);
       // mv.visitCode();
        for (Node dec :
                declarationList) {
            dec.codegen(mv, cw);
        }
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}
