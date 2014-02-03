/**
 *    Copyright 2013 MegaFon
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
import com.fasterxml.jackson.databind.node.TextNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Упрощает AST дерево, объединяя несколько подряд следующих текстовых элементов в одну строку.
 */
public class FragmentsConcatinationOptimizer extends AbstractASTWalker {
    public FragmentsConcatinationOptimizer(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    protected ArrayNode simplifyArrayNode(ArrayNode ast) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();
        StringBuilder sb = new StringBuilder();
        for (JsonNode node : ast) {
            if (node.isTextual()) {
                sb.append(node.asText());
            } else if (node.isArray() && node.size() > 1 && node.get(0).isInt() && node.get(0).asInt() == AstNodeType.STRING) {
                sb.append(node.get(1).asText());
            } else {
                if (sb.length() > 0) {
                    result.add(nodeFactory.jsonString(sb.toString()));
                    sb.setLength(0);
                }
                JsonNode processedNode = processAstNode(node);
                if (processedNode.isArray()) {
                    processedNode = simplifyArrayNode((ArrayNode) processedNode);
                }
                if (processedNode.isArray() && processedNode.size() == 1) {
                    if (result.size() > 0 && result.get(result.size() - 1).isTextual()) {
                        TextNode prev = (TextNode) result.get(result.size() - 1);
                        TextNode cur = (TextNode) processedNode.get(0);
                        TextNode newElem = (TextNode) nodeFactory.jsonString(prev.textValue() + cur.textValue());
                        result.set(result.size() - 1, newElem);
                    } else {
                        result.add(processedNode.get(0));
                    }
                } else {
                    result.add(processedNode);
                }
            }
        }
        if (sb.length() > 0) {
            result.add(nodeFactory.jsonString(sb.toString()));
        }
        return result;
    }

    protected JsonNode[] simplifyArray(JsonNode[] ast) throws HistoneException {
        List<JsonNode> result = new ArrayList<JsonNode>();
        StringBuilder sb = new StringBuilder();
        for (JsonNode node : ast) {
            if (node.isTextual()) {
                sb.append(node.asText());
            } else if (node.isArray() && node.size() > 1 && node.get(0).isInt() && node.get(0).asInt() == AstNodeType.STRING) {
                sb.append(node.get(1).asText());
            } else {
                result.add(nodeFactory.jsonString(sb.toString()));
                sb.setLength(0);
                JsonNode processedNode = processAstNode(node);
                result.add(processedNode);
            }
        }
        if (sb.length() > 0) {
            result.add(nodeFactory.jsonString(sb.toString()));
        }

        JsonNode[] r = new JsonNode[result.size()];
        result.toArray(r);
        return r;
    }

    @Override
    public ArrayNode process(ArrayNode ast) throws HistoneException {
        ast = removeHistoneAstSignature(ast);

        return simplifyArrayNode(ast);
    }

    @Override
    protected JsonNode processStatements(ArrayNode statements) throws HistoneException {
        statements = (ArrayNode) statements.get(1);
        return nodeFactory.jsonArray(AstNodeType.STATEMENTS, simplifyArrayNode(statements));
    }

    @Override
    protected JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3).get(0);

        ArrayNode elseStatements = null;
        if (for_.get(3).size() > 1) {
            elseStatements = (ArrayNode) for_.get(3).get(1);
        }

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
                nodeFactory.jsonArray(nodeFactory.jsonArray(simplifyArray(statementsOut))) :
                nodeFactory.jsonArray(nodeFactory.jsonArray(simplifyArray(statementsOut)), nodeFactory.jsonArray(simplifyArray(elseStatementsOut)));

        return nodeFactory.jsonArray(AstNodeType.FOR, var, collection, statementsContainer);
    }

    @Override
    protected JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(1);
        ArrayNode args = (ArrayNode) macro.get(2);
        ArrayNode statements = (ArrayNode) macro.get(3);

        pushContext();
        args = (ArrayNode) processAstNode(args);
        statements = (ArrayNode) processArrayOfAstNodes(statements);
        popContext();

        return nodeFactory.jsonArray(AstNodeType.MACRO, name, args, simplifyArrayNode(statements));
    }

    @Override
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
            conditionOut.add(nodeFactory.jsonArray(simplifyArray(statementsOut)));
            conditionsOut.add(conditionOut);
        }

        return nodeFactory.jsonArray(AstNodeType.IF, conditionsOut);
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
