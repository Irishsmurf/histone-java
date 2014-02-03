package ru.histone.optimizer;

import ru.histone.evaluator.nodes.NodeFactory;

public class InlineMacroOptimizer extends AbstractASTWalker {
    public InlineMacroOptimizer(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public void pushContext() {
        //nop
    }

    @Override
    public void popContext() {
        //nop
    }
}
