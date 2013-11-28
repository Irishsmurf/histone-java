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
import ru.histone.evaluator.EvaluatorContext;
import ru.histone.evaluator.nodes.GlobalObjectNode;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

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
 */
public class AstOptimizer extends BaseOptimization {
    private final Evaluator evaluator;

    public AstOptimizer(NodeFactory nodeFactory, Evaluator evaluator) {
        super(nodeFactory);
        this.evaluator = evaluator;
    }

    private JsonNode clear(JsonNode ast) {
        if (ast.isArray() && ast.size() > 0 && ast.get(0).isInt()) {
            ArrayNode arr = (ArrayNode) ast;
            int value = arr.get(0).asInt();
            if (value < 0) {
                arr.remove(0);
                arr.insert(0, -value);
            }
        }

        for (JsonNode node : ast) {
            clear(node);
        }

        return ast;
    }

    public ArrayNode optimize(ArrayNode ast) throws HistoneException {
        boolean fullAstTreeIsSafe = true;
        for (JsonNode node : ast) {
            if (node.isArray() && node.size() > 0 && node.get(0).isInt() && node.get(0).asInt() < 0) {
                fullAstTreeIsSafe = false;
            }
        }

        if (fullAstTreeIsSafe) {
            JsonNode evaluated = evaluateAstOnCleanContext(ast);
            return nodeFactory.jsonArray(evaluated);
        } else {
            ast = process(ast);
            ast = (ArrayNode) clear(ast);
            return ast;
        }
    }

    @Override
    protected JsonNode processFor(ArrayNode for_) throws HistoneException {
        if (AstMarker.safeAstNode(for_)) {
            return evaluateAstOnCleanContext(nodeFactory.jsonArray(for_));
        } else {
            return super.processFor(for_);
        }
    }

    public JsonNode evaluateAstOnCleanContext(ArrayNode ast) throws HistoneException {
        EvaluatorContext context = EvaluatorContext.createEmpty(nodeFactory, new GlobalObjectNode(nodeFactory));
        Node node = evaluate(ast, context);
        return nodeFactory.jsonString(node.getAsString().getValue());
    }

    private Node evaluate(ArrayNode astArray, EvaluatorContext context) throws HistoneException {
        // TODO baseURI implementation
        String result = evaluator.process("", astArray, context.getAsNode().getAsJsonNode());
        return nodeFactory.string(result);
    }

    @Override
    public void pushContext() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void popContext() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
