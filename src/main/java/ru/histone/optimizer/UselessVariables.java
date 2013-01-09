package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * User: sazonovkirill@gmail.com
 * Date: 09.01.13
 */
public class UselessVariables extends BaseOptimization {
    private Mode mode;
    private Set<String> selectors;

    public UselessVariables(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    public ArrayNode removeUselessVariables(ArrayNode ast) throws HistoneException {
        selectors = new HashSet<String>();
        mode = Mode.COLLECT_SELECTORS;
        process(ast);
        mode = Mode.REMOVE_VARIABLES;
        return process(ast);
    }

    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode var = selector.get(1);

        if (var.size() == 1) {
            var = var.get(0);
            String varName = var.asText();
            selectors.add(varName);
            return selector;
        } else {
            return processAstNode(var);
        }
    }

    protected JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode var = variable.get(1);
        JsonNode value = variable.get(2);
        JsonNode processedValue = processAstNode(value);

        String varName = var.asText();
        if (selectors.contains(varName)) {
            return ast(AstNodeType.VAR, var, processedValue);
        } else {
            return ast(AstNodeType.NULL);
        }
    }

    enum Mode {
        COLLECT_SELECTORS,
        REMOVE_VARIABLES
    }

    @Override
    public void pushContext() {
        // There is no context to push in this optimizer
    }

    @Override
    public void popContext() {
        // There is no context to pop in this optimizer
    }
}
