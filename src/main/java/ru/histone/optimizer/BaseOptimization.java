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
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.*;

/**
 * User: sazonovkirill@gmail.com
 * Date: 08.01.13
 */
public abstract class BaseOptimization {
    protected final NodeFactory nodeFactory;

    protected static ArrayNode removeHistoneAstSignature(ArrayNode ast) {
        if (ast.size() == 2 &&
                ast.get(0).isArray() &&
                ast.get(1).isArray() &&
                "HISTONE".equals(ast.get(0).get(0).asText())) {
            return (ArrayNode) ast.get(1);
        } else {
            return ast;
        }
    }

    protected ArrayNode process(ArrayNode ast) throws HistoneException {
        ast = removeHistoneAstSignature(ast);

        ArrayNode result = nodeFactory.jsonArray();
        for (JsonNode node : ast) {
            JsonNode processedNode = processAstNode(node);
            result.add(processedNode);
        }
        return result;
    }

    protected JsonNode processArrayOfAstNodes(JsonNode node) throws HistoneException {
        if (node.isArray()) {
            JsonNode[] result = new JsonNode[node.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = processAstNode(node.get(i));
            }
            return nodeFactory.jsonArray(result);
        } else {
            return processAstNode(node);
        }
    }

    protected JsonNode processAstNode(JsonNode node) throws HistoneException {
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

        int nodeType = Math.abs(getNodeType(arr));

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

    public abstract void pushContext();

    public abstract void popContext();

    protected JsonNode processCall(ArrayNode call) throws HistoneException {
        JsonNode target = call.get(1);
        JsonNode functionName = call.get(2);
        JsonNode args = call.get(3);

        target = processAstNode(target);
        if (args.isArray() && args.size() > 0) {
            args = processArrayOfAstNodes(args);
        }

        return ast(AstNodeType.CALL, target, functionName, args);
    }

    protected JsonNode processMap(ArrayNode map) throws HistoneException {
        ArrayNode items = (ArrayNode) map.get(1);
        for (JsonNode item : items) {
            if (item.isArray()) {
                ArrayNode arr = (ArrayNode) item;
                JsonNode key = arr.get(0);
                JsonNode value = arr.get(1);

                value = processAstNode(value);

                arr.removeAll();
                arr.add(key);
                arr.add(value);
            }
        }

        return map;
    }

    protected JsonNode processOperationOverArguments(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.notNull(ast.size() > 1);
        Assert.isTrue(ast.get(0).isNumber());
        for (int i = 1; i < ast.size(); i++) Assert.isTrue(ast.get(i).isArray());
        Assert.isTrue(getOperationsOverArguments().contains(Math.abs(ast.get(0).asInt())));

        int operationType = ast.get(0).asInt();

        final List<ArrayNode> processedArguments = new ArrayList<ArrayNode>();
        for (int i = 1; i < ast.size(); i++) {
            JsonNode processedArg = processAstNode(ast.get(i));
            Assert.isTrue(processedArg.isArray());
            processedArguments.add((ArrayNode) processedArg);
        }

        return ast(operationType, processedArguments);
    }

    protected JsonNode processStatements(ArrayNode statements) throws HistoneException {
        statements = (ArrayNode) statements.get(1);

        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processAstNode(statements.get(i));
        }

        return ast(AstNodeType.STATEMENTS, nodeFactory.jsonArray(statementsOut));
    }

    protected JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode var = variable.get(1);
        JsonNode value = variable.get(2);
        JsonNode processedValue = processAstNode(value);

        return ast(AstNodeType.VAR, var, processedValue);
    }

    protected JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(1);
        ArrayNode args = (ArrayNode) macro.get(2);
        ArrayNode statements = (ArrayNode) macro.get(3);

        pushContext();
        args = (ArrayNode) processAstNode(args);
        statements = (ArrayNode) processArrayOfAstNodes(statements);
        popContext();

        return ast(AstNodeType.MACRO, name, args, statements);
    }

    protected JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        ArrayNode conditionsOut = nodeFactory.jsonArray();

        for (JsonNode condition : conditions) {
            JsonNode expression = condition.get(0);
            JsonNode statements = condition.get(1);

            expression = processAstNode(expression);

            pushContext();
            JsonNode[] statementsOut = new JsonNode[statements.size()];
            for (int i = 0; i < statements.size(); i++) {
                statementsOut[i] = processAstNode(statements.get(i));
            }
            popContext();

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expression);
            conditionOut.add(nodeFactory.jsonArray(statementsOut));
            conditionsOut.add(conditionOut);
        }

        return ast(AstNodeType.IF, conditionsOut);
    }

    protected JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3).get(0);

        ArrayNode elseStatements = null;
        if (for_.get(3).size() > 1) {
            elseStatements = (ArrayNode) for_.get(3).get(1);
        }

        String iterVal = var.get(0).asText();
        String iterKey = (var.size() > 1) ? var.get(1).asText() : null;

        collection = (ArrayNode) processAstNode(collection);

        pushContext();
        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processAstNode(statements.get(i));
        }
        popContext();

        JsonNode[] elseStatementsOut = null;
        if (elseStatements != null) {
            elseStatementsOut = new JsonNode[elseStatements.size()];
            for (int i = 0; i < elseStatements.size(); i++) {
                elseStatementsOut[i] = processAstNode(elseStatements.get(i));
            }
        }

        ArrayNode statementsContainer = elseStatementsOut == null ?
                nodeFactory.jsonArray(nodeFactory.jsonArray(statementsOut)) :
                nodeFactory.jsonArray(nodeFactory.jsonArray(statementsOut), nodeFactory.jsonArray(elseStatementsOut));

        return ast(AstNodeType.FOR, var, collection, statementsContainer);
    }

    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode fullVariable = selector.get(1);

        JsonNode[] tokensOut = new JsonNode[fullVariable.size()];
        for (int i = 0; i < fullVariable.size(); i++) {
            JsonNode token = fullVariable.get(i);
            if (token.isArray()) {
                token = processAstNode(token);
            }
            tokensOut[i] = token;
        }

        JsonNode result = ast(AstNodeType.SELECTOR, nodeFactory.jsonArray(tokensOut));
        return result;
    }

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

    public static boolean isString(JsonNode element) {
        return element.isTextual();
    }

    public static boolean isStatements(ArrayNode arr) {
        int nodeType = getNodeType(arr);
        return nodeType == AstNodeType.STATEMENTS;
    }

    public static boolean isSelector(ArrayNode arr) {
        int nodeType = getNodeType(arr);
        return nodeType == AstNodeType.SELECTOR;
    }

    public static boolean isConstant(ArrayNode arr) {
        int nodeType = getNodeType(arr);
        return nodeType == AstNodeType.TRUE ||
                nodeType == AstNodeType.FALSE ||
                nodeType == AstNodeType.NULL ||
                nodeType == AstNodeType.INT ||
                nodeType == AstNodeType.DOUBLE ||
                nodeType == AstNodeType.STRING;
    }

    public static boolean isMap(ArrayNode arr) {
        int nodeType = getNodeType(arr);
        return nodeType == AstNodeType.MAP;
    }

    public static boolean isMapOfConstants(ArrayNode arr) {
        int nodeType = getNodeType(arr);
        if (nodeType != AstNodeType.MAP) return false;

        boolean result = true;
        ArrayNode mapEntries = (ArrayNode) arr.get(1);
        for (JsonNode mapEntry : mapEntries) {
            JsonNode entryKey = mapEntry.get(0);
            ArrayNode entryValue = (ArrayNode) mapEntry.get(1);

            result = result && (isConstant(entryValue) || isMapOfConstants(entryValue));
        }
        return result;
    }

    protected boolean isConstants(Collection<? extends ArrayNode> astArrays) {
        for (ArrayNode node : astArrays) {
            if (!isConstant(node)) {
                return false;
            }
        }
        return true;
    }

    public static int getNodeType(ArrayNode astArray) {
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
        } else if (node.isUndefined()) {
            // EXPERIMENTAL: Not sure here
            return nodeFactory.jsonString("");
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

    public static final String KEYWORD_THIS = "this";
    public static final String KEYWORD_SELF = "self";
    public static final String KEYWORD_GLOBAL = "global";
    public static final String KEYWORD_INDEX = "index";
    public static final String KEYWORD_LAST = "last";
    public static final String KEYWORD_ARGUMENTS = "arguments";
}
