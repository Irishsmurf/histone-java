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

            if (var.isTextual() && (isConstant(value) || isStatements(value) || isMapOfConstants(value))) {
                String varName = var.asText();

                if (!context.hasVar(varName)) {
                    context.putVar(varName, value);
                }
            }
        }

        return ast(AstNodeType.VAR, var, valueNode);
    }

    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode fullVariable = selector.get(1);

        JsonNode token = fullVariable.get(0);
        if (token.isTextual()) {
            String varName = token.asText();
            if (context.hasVar(varName)) {
                ArrayNode value = context.getVarValue(varName);
                for (int i = 1; i < fullVariable.size(); i++) {
                    JsonNode t = fullVariable.get(i);
                    if (t.isTextual()) {
                        value = findValueInAstMap(value, t.asText());
                    } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.STRING) {
                        value = findValueInAstMap(value, t.get(1).asText());
                    } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.INT) {
                        value = findValueInAstMap(value, t.get(1).asInt());
                    } else {
                        return selector;
                    }

                    if (value == null) return selector;
                }
                return value;
            } else {
                return selector;
            }
        } else return processAstNode(token);
    }

    private ArrayNode findValueInAstMap(ArrayNode mapEntries, String key) {
        mapEntries = (ArrayNode) mapEntries.get(1);
        for (JsonNode jsonNode : mapEntries) {
            ArrayNode mapEntry = (ArrayNode) jsonNode;
            JsonNode entryKey = mapEntry.get(0);
            ArrayNode entryValue = (ArrayNode) mapEntry.get(1);

            if (entryKey.asText().equals(key)) {
                return entryValue;
            }
        }
        return null;
    }

    private ArrayNode findValueInAstMap(ArrayNode mapEntries, int index) {
        mapEntries = (ArrayNode) mapEntries.get(1);
        boolean isArray = true;
        for (JsonNode mapEntry : mapEntries) {
            JsonNode key = mapEntry.get(0);
            isArray = isArray && key.isNull();
        }
        if (!isArray) {
            for (JsonNode jsonNode : mapEntries) {
                ArrayNode mapEntry = (ArrayNode) jsonNode;
                JsonNode entryKey = mapEntry.get(0);
                ArrayNode entryValue = (ArrayNode) mapEntry.get(1);

                if (entryKey.asInt() == index) {
                    return entryValue;
                }
            }
        }

        if (index < mapEntries.size()) {
            return (ArrayNode) mapEntries.get(index).get(1);
        } else {
            return null;
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
