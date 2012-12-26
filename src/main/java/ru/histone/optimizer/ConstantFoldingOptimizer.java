/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.HistoneException;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.*;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class ConstantFoldingOptimizer {
    private static final Logger logger = LoggerFactory.getLogger(ConstantFoldingOptimizer.class);

    private final Context context = new Context();
    private final NodeFactory nodeFactory;
    private final Evaluator evaluator;

    public ConstantFoldingOptimizer(NodeFactory nodeFactory, Evaluator evaluator) {
        this.nodeFactory = nodeFactory;
        this.evaluator = evaluator;
    }

    public ArrayNode foldConstants(ArrayNode ast) throws HistoneException {
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

        Assert.isTrue(node.isArray());
        ArrayNode arr = (ArrayNode) node;

        int nodeType = getNodeType(arr);

        if (getOperationsOverArguments().contains(nodeType)) {
            return processOperationOverArguments(arr);
        } else {
            return node;
        }
    }

    private final static Set<Integer> CONSTANTS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.TRUE,
            AstNodeType.FALSE,
            AstNodeType.NULL,
            AstNodeType.INT,
            AstNodeType.DOUBLE,
            AstNodeType.STRING
    ));

    private final static Set<Integer> BINARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.ADD,
            AstNodeType.SUB,
            AstNodeType.MUL,
            AstNodeType.DIV,
            AstNodeType.MOD,
            AstNodeType.OR,
            AstNodeType.AND,
            AstNodeType.NOT,
            AstNodeType.EQUAL,
            AstNodeType.NOT_EQUAL,
            AstNodeType.LESS_OR_EQUAL,
            AstNodeType.LESS_THAN,
            AstNodeType.GREATER_OR_EQUAL,
            AstNodeType.GREATER_THAN
    ));

    private final static Set<Integer> UNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.NEGATE,
            AstNodeType.NOT
    ));

    private final static Set<Integer> TERNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.NEGATE
    ));

    private Set<Integer> getBinaryOperations() {
        return BINARY_OPERATIONS;
    }

    private Set<Integer> getUnaryOperations() {
        return UNARY_OPERATIONS;
    }

    private Set<Integer> getTernaryOperations() {
        return TERNARY_OPERATIONS;
    }

    private Set<Integer> getOperationsOverArguments() {
        final Set<Integer> result = new HashSet<Integer>();
        result.addAll(getBinaryOperations());
        result.addAll(getUnaryOperations());
        result.addAll(getTernaryOperations());
        return result;
    }

    private JsonNode processUnaryOperation(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.isTrue(ast.size() == 2);
        Assert.isTrue(ast.get(0).isNumber());
        Assert.isTrue(ast.get(1).isArray());
        Assert.isTrue(getUnaryOperations().contains(ast.get(0).asInt()));

        int operationType = ast.get(0).asInt();
        ArrayNode arg1 = (ArrayNode) ast.get(1);
        JsonNode processedArg1 = processNode(arg1);
        Assert.isTrue(processedArg1.isArray());
        arg1 = (ArrayNode) processedArg1;

        if (isConstant(arg1)) {
            Node node = evaluate(operationType, arg1);
            return node2Ast(node);
        } else {
            return ast(operationType, arg1);
        }
    }

    private JsonNode processBinaryOperation(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.isTrue(ast.size() == 3);
        Assert.isTrue(ast.get(0).isNumber());
        Assert.isTrue(ast.get(1).isArray());
        Assert.isTrue(ast.get(2).isArray());
        Assert.isTrue(getBinaryOperations().contains(ast.get(0).asInt()));

        int operationType = ast.get(0).asInt();
        ArrayNode arg1 = (ArrayNode) ast.get(1);
        ArrayNode arg2 = (ArrayNode) ast.get(2);

        JsonNode processedArg1 = processNode(arg1);
        JsonNode processedArg2 = processNode(arg2);

        Assert.isTrue(processedArg1.isArray());
        Assert.isTrue(processedArg2.isArray());
        arg1 = (ArrayNode) processedArg1;
        arg2 = (ArrayNode) processedArg2;

        if (isConstant(arg1) && isConstant(arg2)) {
            Node node = evaluate(operationType, arg1, arg2);
            return node2Ast(node);
        } else {
            return ast(operationType, arg1, arg2);
        }
    }

    private JsonNode processTernaryOperation(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.notNull(ast.size() == 4);
        Assert.isTrue(ast.get(0).isNumber());
        Assert.isTrue(ast.get(1).isArray());
        Assert.isTrue(ast.get(2).isArray());
        Assert.isTrue(ast.get(3).isArray());
        Assert.isTrue(getTernaryOperations().contains(ast.get(0).asInt()));

        int operationType = ast.get(0).asInt();

        ArrayNode arg1 = (ArrayNode) ast.get(1);
        ArrayNode arg2 = (ArrayNode) ast.get(2);
        ArrayNode arg3 = (ArrayNode) ast.get(3);

        JsonNode arg1Processed = processNode(arg1);
        JsonNode arg2Processed = processNode(arg2);
        JsonNode arg3Processed = processNode(arg3);
        Assert.isTrue(arg1Processed.isArray());
        Assert.isTrue(arg2Processed.isArray());
        Assert.isTrue(arg3Processed.isArray());

        arg1 = (ArrayNode) arg1Processed;
        arg2 = (ArrayNode) arg2Processed;
        arg3 = (ArrayNode) arg3Processed;

        if (isConstant(arg1) && isConstant(arg2) && isConstant(arg3)) {
            Node node = evaluate(operationType, arg1, arg2, arg3);
            return node2Ast(node);
        } else {
            return ast(operationType, arg1, arg2, arg3);
        }
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

        if (isConstants(processedArguments)) {
            Node node = evaluate(operationType, processedArguments);
            return node2Ast(node);
        } else {
            return ast(operationType, processedArguments);
        }
    }

    private ArrayNode ast(int operationType, ArrayNode... arguments) {
        ArrayNode array = nodeFactory.jsonArray(IntNode.valueOf(operationType));
        for (ArrayNode arg : arguments) {
            array.add(arg);
        }
        return array;
    }

    private ArrayNode ast(int operationType, Collection<? extends ArrayNode> arguments) {
        ArrayNode array = nodeFactory.jsonArray(IntNode.valueOf(operationType));
        for (ArrayNode argument : arguments) {
            array.add(argument);
        }
        return array;
    }

    private Node evaluate(int operationType, Collection<? extends ArrayNode> arguments) throws EvaluatorException {
        return evaluator.evaluate(ast(operationType, arguments));
    }

    private Node evaluate(int operationType, ArrayNode... arguments) throws EvaluatorException {
        return evaluator.evaluate(ast(operationType, arguments));
    }

    private JsonNode node2Ast(Node node) {
        if (node.isBoolean()) {
            return node.getAsBoolean().getValue() ? nodeFactory.jsonArray(AstNodeType.TRUE) : nodeFactory.jsonArray(AstNodeType.FALSE);
        } else if (node.isInteger()) {
            return nodeFactory.jsonArray(AstNodeType.INT, nodeFactory.jsonNumber(node.getAsNumber().getValue()));
        } else if (node.isFloat()) {
            return nodeFactory.jsonArray(AstNodeType.DOUBLE, nodeFactory.jsonNumber(node.getAsNumber().getValue()));
        } else if (node.isString()) {
            return nodeFactory.jsonArray(AstNodeType.STRING, nodeFactory.jsonString(node.getAsString().getValue()));
        } else if (node.isNull()) {
            return nodeFactory.jsonArray(AstNodeType.NULL);
        }

        throw new IllegalStateException(String.format("Can't convert node %s to AST element", node));
    }

    static class Context {

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
