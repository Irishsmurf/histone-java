package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * User: sazonovkirill@gmail.com
 * Date: 07.01.13
 */
public class ConstantPropagation extends BaseOptimization {
    private Context context;

    public ConstantPropagation(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    public ArrayNode propagateConstants(ArrayNode ast) throws HistoneException {
        context = new Context();

        return process(ast);
    }

    @Override
    public void pushContext() {
        context.push();
    }

    @Override
    public void popContext() {
        context.pop();
    }

    protected JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode var = variable.get(1);
        JsonNode preValue = variable.get(2);
        JsonNode valueNode = processAstNode(preValue);

        if (valueNode.isArray()) {
            ArrayNode value = (ArrayNode) valueNode;

            if (var.isTextual() && isConstant(value)) {
                String varName = var.asText();

                if (!context.hasVar(varName)) {
                    context.putVar(varName, value);
                }
            }
        }

        return ast(AstNodeType.VAR, var, valueNode);
    }

    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode var = selector.get(1);

        if (var.size() == 1) {
            var = var.get(0);
            String varName = var.asText();
            if (context.hasVar(varName)) {
                return context.getVarValue(varName);
            } else {
                return selector;
            }
        } else {
            return processAstNode(var);
        }
    }

    static class Context {
        private Deque<Map<String, ArrayNode>> stackVars = new ArrayDeque<Map<String, ArrayNode>>();

        public Context() {
            push();
        }

        public void push() {
            stackVars.push(new HashMap<String, ArrayNode>());
        }

        public void pop() {
            stackVars.pollFirst();
        }

        public void putVar(String varName, ArrayNode value) {
            stackVars.getFirst().put(varName, value);
        }

        public ArrayNode getVarValue(String varName) {
            for (Map<String, ArrayNode> frame : stackVars) {
                if (frame.containsKey(varName)) {
                    return frame.get(varName);
                }
            }
            return null;
        }

        public boolean hasVar(String varName) {
            for (Map<String, ArrayNode> frame : stackVars) {
                if (frame.containsKey(varName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
