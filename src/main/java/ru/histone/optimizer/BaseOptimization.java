package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: sazonovkirill@gmail.com
 * Date: 08.01.13
 */
public class BaseOptimization {
    protected final NodeFactory nodeFactory;

    protected final static Set<Integer> CONSTANTS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.TRUE,
            AstNodeType.FALSE,
            AstNodeType.NULL,
            AstNodeType.INT,
            AstNodeType.DOUBLE,
            AstNodeType.STRING
    ));

    protected final static Set<Integer> BINARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
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

    protected final static Set<Integer> UNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.NEGATE,
            AstNodeType.NOT
    ));

    protected final static Set<Integer> TERNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.NEGATE
    ));

    protected Set<Integer> getBinaryOperations() {
        return BINARY_OPERATIONS;
    }

    protected Set<Integer> getUnaryOperations() {
        return UNARY_OPERATIONS;
    }

    protected Set<Integer> getTernaryOperations() {
        return TERNARY_OPERATIONS;
    }

    protected Set<Integer> getOperationsOverArguments() {
        final Set<Integer> result = new HashSet<Integer>();
        result.addAll(getBinaryOperations());
        result.addAll(getUnaryOperations());
        result.addAll(getTernaryOperations());
        return result;
    }

    protected boolean isString(JsonNode element) {
        return element.isTextual();
    }

    protected boolean isConstant(ArrayNode astArray) {
        int nodeType = getNodeType(astArray);
        return nodeType == AstNodeType.TRUE ||
                nodeType == AstNodeType.FALSE ||
                nodeType == AstNodeType.NULL ||
                nodeType == AstNodeType.INT ||
                nodeType == AstNodeType.DOUBLE ||
                nodeType == AstNodeType.STRING;
    }

    protected boolean isConstants(Collection<? extends ArrayNode> astArrays) {
        for (ArrayNode node : astArrays) {
            if (!isConstant(node)) {
                return false;
            }
        }
        return true;
    }

    protected int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }

    protected ArrayNode ast(int operationType, JsonNode... arguments) {
        ArrayNode array = nodeFactory.jsonArray(IntNode.valueOf(operationType));
        for (JsonNode arg : arguments) {
            array.add(arg);
        }
        return array;
    }

    protected ArrayNode ast(int operationType, Collection<? extends ArrayNode> arguments) {
        ArrayNode array = nodeFactory.jsonArray(IntNode.valueOf(operationType));
        for (ArrayNode argument : arguments) {
            array.add(argument);
        }
        return array;
    }

    protected JsonNode node2Ast(Node node) {
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

    protected BaseOptimization(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public static long hash(JsonNode arr) {
        long result = 0;

        if (arr.isContainerNode()) {
            for (JsonNode node : arr) {
                result += hash(node);
            }
        }

        if (arr.isValueNode()) {
            result += arr.asText().hashCode();
        }

        return result;
    }
}
