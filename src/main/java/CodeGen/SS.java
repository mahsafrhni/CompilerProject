package CodeGen;

import java.util.ArrayDeque;

public class SS extends ArrayDeque<Object> {  //SEMANTIC STACK
    @Override
    public Object pop() {
        return this.removeFirst();
    }

    @Override
    public void push(Object variable) {
        this.addFirst(variable);
    }
}

