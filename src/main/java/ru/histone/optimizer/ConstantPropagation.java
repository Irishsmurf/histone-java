package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.*;

/**
 * User: sazonovkirill@gmail.com
 * Date: 07.01.13
 */
public class ConstantPropagation {
    private static final Logger logger = LoggerFactory.getLogger(ConstantPropagation.class);

    private final NodeFactory nodeFactory;

    public ConstantPropagation(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public ArrayNode propagateConstants(ArrayNode ast) throws HistoneException {
        this.stackVars = new ArrayDeque<Map<String, Object>>();
        pushContext();

        ArrayNode result = nodeFactory.jsonArray();

        if (ast.size() == 2 &&
                ast.get(0).isArray() &&
                ast.get(1).isArray() &&
                "HISTONE".equals(ast.get(0).get(0).asText())) {
            ast = (ArrayNode) ast.get(1);
        }

        for (JsonNode node : ast) {
            JsonNode processedNode = processNode(node);
            result.add(processedNode);
        }

        return result;
    }

    public JsonNode processNode(JsonNode node) throws HistoneException {
        // All text nodes are returned 'as it'
        if (isString(node)) {
            return node;
        }

        if (!node.isArray()) {
            return node;
        }
        ArrayNode arr = (ArrayNode) node;

        int nodeType = getNodeType(arr);
        switch (nodeType) {
            case AstNodeType.SELECTOR:
                return processSelector(arr);
            case AstNodeType.VAR:
                return processVariable(arr);
            case AstNodeType.MACRO:
                return processMacro(arr);
            case AstNodeType.IF:
                return processIf(arr);
            case AstNodeType.FOR:
                return processFor(arr);
            default:
                return node;
        }
    }

    public JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode var = variable.get(1);
        JsonNode preValue = variable.get(2);
        JsonNode valueNode = processNode(preValue);

        if (valueNode.isArray()) {
            ArrayNode value = (ArrayNode) valueNode;

            if (var.isTextual() && isConstant(value)) {
                String varName = var.asText();

                if (!contextContainsVarDefinition(varName)) {
                    putToContext(varName, value);
                }
            }
        }

        return array(AstNodeType.VAR, var, valueNode);
    }

    public JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(0);
        ArrayNode args = (ArrayNode) macro.get(1);
        ArrayNode statements = (ArrayNode) macro.get(2);

        pushContext();
        args = (ArrayNode) processNode(args);
        statements = (ArrayNode) processNode(statements);
        popContext();

        return array(AstNodeType.MACRO, name, args, statements);
    }

    public JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        ArrayNode conditionsOut = nodeFactory.jsonArray();

        for (JsonNode condition : conditions) {
            JsonNode expression = conditions.get(0);
            JsonNode statements = condition.get(1);

            expression = processNode(expression);
            pushContext();
            statements = processNode(statements);
            popContext();

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expression);
            conditionOut.add(statements);
            conditionsOut.add(conditionOut);
        }

        return array(AstNodeType.IF, conditionsOut);
    }

    public JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3);

        String iterVal = var.get(0).asText();
        String iterKey = (var.size() > 1) ? var.get(1).asText() : null;

        collection = (ArrayNode) processNode(collection);

        pushContext();
        statements = (ArrayNode) processNode(statements);
        popContext();

        return array(AstNodeType.FOR, var, collection, statements);
    }

    public JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode var = selector.get(1);

        if (var.size() == 1) {
            var = var.get(0);
            String varName = var.asText();
            if (contextContainsVarDefinition(varName)) {
                return (JsonNode) getVarValue(varName);
            } else {
                return var;
            }
        } else {
            return processNode(var);
        }
    }

    private Deque<Map<String, Object>> stackVars = new ArrayDeque<Map<String, Object>>();

    private void pushContext() {
        stackVars.push(new HashMap<String, Object>());
    }

    private void popContext() {
        stackVars.pollFirst();
    }

    private void putToContext(String varName, ArrayNode value) {
        stackVars.getFirst().put(varName, value);
    }

    private Object getVarValue(String varName) {
        for (Map<String, Object> frame : stackVars) {
            if (frame.containsKey(varName)) {
                return frame.get(varName);
            }
        }
        return null;
    }

    private boolean contextContainsVarDefinition(String varName) {
        for (Map<String, Object> frame : stackVars) {
            if (frame.containsKey(varName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayNode array(int operationType, JsonNode... arguments) {
        return nodeFactory.jsonArray(operationType, arguments);
    }

    private boolean isString(JsonNode element) {
        return element.isTextual();
    }

    private boolean isConstant(ArrayNode astArray) {
        int nodeType = getNodeType(astArray);
        return nodeType == AstNodeType.TRUE ||
                nodeType == AstNodeType.FALSE ||
                nodeType == AstNodeType.NULL ||
                nodeType == AstNodeType.INT ||
                nodeType == AstNodeType.DOUBLE ||
                nodeType == AstNodeType.STRING;
    }

    private boolean isConstants(Collection<? extends ArrayNode> astArrays) {
        for (ArrayNode node : astArrays) {
            if (!isConstant(node)) {
                return false;
            }
        }
        return true;
    }

    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }
}
