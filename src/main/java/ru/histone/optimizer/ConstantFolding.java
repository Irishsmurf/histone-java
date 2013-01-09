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
import ru.histone.HistoneException;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class ConstantFolding extends BaseOptimization {
    private final Evaluator evaluator;

    public ConstantFolding(NodeFactory nodeFactory, Evaluator evaluator) {
        super(nodeFactory);
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
                return node;
            case AstNodeType.STATEMENTS:
                return processStatements(arr);
            case AstNodeType.VAR:
                return processVar(arr);
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

    private JsonNode processStatements(ArrayNode statements) throws HistoneException {
        statements = (ArrayNode) statements.get(1);

        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processNode(statements.get(i));
        }

        return ast(AstNodeType.STATEMENTS, nodeFactory.jsonArray(statementsOut));
    }

    private JsonNode processVar(ArrayNode variable) throws HistoneException {
        JsonNode var = variable.get(1);
        JsonNode preValue = variable.get(2);
        JsonNode valueNode = processNode(preValue);

        return ast(AstNodeType.VAR, var, valueNode);
    }

    private JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        ArrayNode conditionsOut = nodeFactory.jsonArray();

        for (JsonNode condition : conditions) {
            JsonNode expression = condition.get(0);
            JsonNode statements = condition.get(1);

            expression = processNode(expression);

            JsonNode[] statementsOut = new JsonNode[statements.size()];
            for (int i = 0; i < statements.size(); i++) {
                statementsOut[i] = processNode(statements.get(i));
            }

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expression);
            conditionOut.add(nodeFactory.jsonArray(statementsOut));
            conditionsOut.add(conditionOut);
        }

        return ast(AstNodeType.IF, conditionsOut);
    }

    private JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3).get(0);

        String iterVal = var.get(0).asText();
        String iterKey = (var.size() > 1) ? var.get(1).asText() : null;

        collection = (ArrayNode) processNode(collection);

        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processNode(statements.get(i));
        }
        // Very stange AST structure in case of FOR
        ArrayNode statementsContainer = nodeFactory.jsonArray(nodeFactory.jsonArray(statementsOut));

        return ast(AstNodeType.FOR, var, collection, statementsContainer);
    }

    private JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(1);
        ArrayNode args = (ArrayNode) macro.get(2);
        ArrayNode statements = (ArrayNode) macro.get(3);

        args = (ArrayNode) processNode(args);
        statements = (ArrayNode) processNode(statements);

        return ast(AstNodeType.MACRO, name, args, statements);
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

        if (isConstants(processedArguments)) {
            Node node = evaluate(operationType, processedArguments);
            return node2Ast(node);
        } else {
            return ast(operationType, processedArguments);
        }
    }

    private Node evaluate(int operationType, Collection<? extends ArrayNode> arguments) throws EvaluatorException {
        return evaluator.evaluate(ast(operationType, arguments));
    }

    private Node evaluate(int operationType, ArrayNode... arguments) throws EvaluatorException {
        return evaluator.evaluate(ast(operationType, arguments));
    }
}
