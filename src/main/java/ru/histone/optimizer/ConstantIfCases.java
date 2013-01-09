package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

/**
 * User: sazonovkirill@gmail.com
 * Date: 09.01.13
 */
public class ConstantIfCases extends BaseOptimization {
    public ConstantIfCases(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    public ArrayNode replaceConstantIfs(ArrayNode ast) throws HistoneException {
        return process(ast);
    }

    protected JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        if (conditions.size() == 1 &&
                conditions.get(0).size() == 2 &&
                conditions.get(0).get(0).size() == 1 &&
                (conditions.get(0).get(0).get(0).asInt() == AstNodeType.TRUE ||
                        conditions.get(0).get(0).get(0).asInt() == AstNodeType.FALSE)) {

            JsonNode condition = conditions.get(0);
            JsonNode expression = condition.get(0);
            JsonNode statements = condition.get(1);

            if (expression.get(0).asInt() == AstNodeType.TRUE) {
                return ast(AstNodeType.STATEMENTS, processArrayOfAstNodes(statements));
            } else {
                return ast(AstNodeType.NULL);
            }
        } else {
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
