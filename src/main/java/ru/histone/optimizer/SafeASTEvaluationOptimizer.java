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
import ru.histone.HistoneException;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

/**
 * This optimization unit evaluates constant AST branches and replaces them by evaluation result (string constant AST node).
 * Constant AST branches are those, that don't have 'side-effects'. These branches can be evaluated only once and later
 * be treated as strings, because:
 * <ul compact>
 * <li>They don't depend on context variables</li>
 * <li>They don't use any calls (functions)</li>
 * </ul>
 *
 * @author sazonovkirill@gmail.com
 * @author peter@salnikov.cc
 */
public class SafeASTEvaluationOptimizer extends AbstractASTWalker {
    private final Evaluator evaluator;

    public SafeASTEvaluationOptimizer(NodeFactory nodeFactory, Evaluator evaluator) {
        super(nodeFactory);
        this.evaluator = evaluator;
    }

    @Override
    protected JsonNode processAstNode(JsonNode node) throws HistoneException {
        if (SafeASTNodesMarker.safeAstNode(node)) {
            if (node instanceof ArrayNode) {
                int type = getNodeType((ArrayNode)node);
                if (type == AstNodeType.INT
                        || type == AstNodeType.TRUE
                        || type == AstNodeType.FALSE
                        || type == AstNodeType.NULL
                        || type == AstNodeType.DOUBLE
                        || type == AstNodeType.STRING) {
                    return clearSafeFlag(super.processAstNode(node));
                } else {
                    JsonNode evaluated = evaluateAstOnCleanContext(node);
                    return nodeFactory.jsonArray(evaluated);
                }
            } else {
                JsonNode evaluated = evaluateAstOnCleanContext(node);
                return nodeFactory.jsonArray(evaluated);
            }
        } else {
            return clearSafeFlag(super.processAstNode(node));
        }
    }

    public JsonNode evaluateAstOnCleanContext(JsonNode ast) throws HistoneException {
        Node node = evaluator.evaluate(ast);
        return nodeFactory.jsonString(node.getAsString().getValue());
    }

    @Override
    public void pushContext() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void popContext() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private JsonNode clearSafeFlag(JsonNode ast) {
        if (ast.isArray() && ast.size() > 0 && ast.get(0).isInt()) {
            ArrayNode arr = (ArrayNode) ast;
            int value = arr.get(0).asInt();
            if (value < 0) {
                arr.remove(0);
                arr.insert(0, -value);
            }
        }

        for (JsonNode node : ast) {
            clearSafeFlag(node);
        }

        return ast;
    }
}
