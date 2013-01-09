package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.*;

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

    public JsonNode processAsArray(JsonNode node) throws HistoneException {
        if (node.isArray()) {
            JsonNode[] result = new JsonNode[node.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = processNode(node.get(i));
            }
            return nodeFactory.jsonArray(result);
        } else {
            return processNode(node);
        }
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

        if (arr.size() == 0) {
            return arr;
        }

        int nodeType = getNodeType(arr);

        if (getOperationsOverArguments().contains(nodeType)) {
            return processOperationOverArguments(arr);
        }

        switch (nodeType) {
            case AstNodeType.SELECTOR:
                return processSelector(arr);
            case AstNodeType.STATEMENTS:
                return processStatements(arr);

            case AstNodeType.VAR:
                return processVariable(arr);
            case AstNodeType.IF:
                return processIf(arr);
            case AstNodeType.FOR:
                return processFor(arr);

            case AstNodeType.MACRO:
                return processMacro(arr);
            case AstNodeType.CALL:
                return processCall(arr);
            case AstNodeType.MAP:
                return processMap(arr);
            default:
                return node;
        }
    }

    private JsonNode processCall(ArrayNode call) throws HistoneException {
        if (call.get(3).isArray()) {
            ArrayNode arr = (ArrayNode) call.get(3);
            return processNode(arr);
        } else {
            return call;
        }
    }

    private JsonNode processMap(ArrayNode map) throws HistoneException {
        ArrayNode items = (ArrayNode) map.get(1);
        for (JsonNode item : items) {
            if (item.isArray()) {
                ArrayNode arr = (ArrayNode) item;
                JsonNode key = arr.get(0);
                JsonNode value = arr.get(1);

                value = processNode(value);

                arr.removeAll();
                arr.add(key);
                arr.add(value);
            }
        }

        return map;
    }

    private JsonNode processOperationOverArguments(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.notNull(ast.size() > 1);
        Assert.isTrue(ast.get(0).isNumber());
        for (int i = 1; i < ast.size(); i++) Assert.isTrue(ast.get(i).isArray());
        Assert.isTrue(getOperationsOverArguments().contains(ast.get(0).asInt()));

        int operationType = ast.get(0).asInt();

        final List<ArrayNode> processedArguments = new ArrayList<ArrayNode>();
        for (int i = 1; i < ast.size(); i++) {
            JsonNode processedArg = processNode(ast.get(i));
            Assert.isTrue(processedArg.isArray());
            processedArguments.add((ArrayNode) processedArg);
        }

        return ast(operationType, processedArguments);
    }

    private JsonNode processStatements(ArrayNode statements) throws HistoneException {
        statements = (ArrayNode) statements.get(1);

        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processNode(statements.get(i));
        }

        return ast(AstNodeType.STATEMENTS, nodeFactory.jsonArray(statementsOut));
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

                if (!context.hasVar(varName)) {
                    context.putVar(varName, value);
                }
            }
        }

        return ast(AstNodeType.VAR, var, valueNode);
    }

    public JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(1);
        ArrayNode args = (ArrayNode) macro.get(2);
        ArrayNode statements = (ArrayNode) macro.get(3);

        context.push();
        args = (ArrayNode) processNode(args);
        statements = (ArrayNode) processAsArray(statements);
        context.pop();

        return ast(AstNodeType.MACRO, name, args, statements);
    }

    public JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        ArrayNode conditionsOut = nodeFactory.jsonArray();

        for (JsonNode condition : conditions) {
            JsonNode expression = condition.get(0);
            JsonNode statements = condition.get(1);

            expression = processNode(expression);

            context.push();
            JsonNode[] statementsOut = new JsonNode[statements.size()];
            for (int i = 0; i < statements.size(); i++) {
                statementsOut[i] = processNode(statements.get(i));
            }
            context.pop();

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expression);
            conditionOut.add(nodeFactory.jsonArray(statementsOut));
            conditionsOut.add(conditionOut);
        }

        return ast(AstNodeType.IF, conditionsOut);
    }

    public JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3).get(0);

        String iterVal = var.get(0).asText();
        String iterKey = (var.size() > 1) ? var.get(1).asText() : null;

        collection = (ArrayNode) processNode(collection);

        context.push();
        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processNode(statements.get(i));
        }
        // Very stange AST structure in case of FOR
        ArrayNode statementsContainer = nodeFactory.jsonArray(nodeFactory.jsonArray(statementsOut));
        context.pop();

        return ast(AstNodeType.FOR, var, collection, statementsContainer);
    }

    public JsonNode processSelector(ArrayNode selector) throws HistoneException {
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
            return processNode(var);
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
