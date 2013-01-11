package ru.histone.evaluator.nodes;


import ru.histone.evaluator.MacroFunc;

import java.util.HashMap;
import java.util.Map;

public class NameSpaceNode extends ObjectHistoneNode{

    private Map<NodeKey, MacroFunc> macros = new HashMap<NodeKey, MacroFunc>();

    protected NameSpaceNode(NodeFactory nodeFactory) {
        super(nodeFactory);
    }
    
    
    public void addMacro(String key, MacroFunc value){
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        macros.put(new NodeKey(key), value);
        
    }
}
