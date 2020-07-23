package CodeGen;

import java.util.ArrayDeque;

public class SS extends ArrayDeque<Object> {  //SEMANTIC STACK
    @Override
    public Object pop() {
        //System.out.println("poped : " + this.getFirst().getClass().getName());
        return this.removeFirst();
    }

    @Override
    public void push(Object var1) {
        //System.out.println("pushed : " + var1.getClass().getName());
        this.addFirst(var1);
    }
}

