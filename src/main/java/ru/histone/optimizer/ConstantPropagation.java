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
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

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

        return process(ast);
    }

    @Override
    public void pushContext() {
        context.push();
    }

    @Override
    public void popContext() {
        context.pop();
    }

    protected JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode var = variable.get(1);
        String varName = var.asText();
        context.removeVar(varName);

        JsonNode preValue = variable.get(2);
        JsonNode valueNode = processAstNode(preValue);

        if (valueNode.isArray()) {
            ArrayNode value = (ArrayNode) valueNode;
            if (var.isTextual() && (isConstant(value) || isStatements(value) || isMapOfConstants(value))) {
                context.putVar(varName, value);
            }
        }

        return nodeFactory.jsonArray(AstNodeType.VAR, var, valueNode);
    }

    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode fullVariable = selector.get(1);

        boolean arrayTokenFound = false;
        JsonNode[] processedTokens = new JsonNode[fullVariable.size()];
        for (int i = 0; i < processedTokens.length; i++) {
            JsonNode token = fullVariable.get(i);
            if (token.isArray()) {
                processedTokens[i] = processAstNode(token);
                arrayTokenFound = true;
            } else {
                processedTokens[i] = token;
            }
        }

        if (arrayTokenFound) {
            return nodeFactory.jsonArray(AstNodeType.SELECTOR, nodeFactory.jsonArray(processedTokens));
        }

        JsonNode token = processedTokens[0];
        if (token.isTextual()) {
            String varName = token.asText();
            if (context.hasVar(varName)) {
                ArrayNode value = context.getVarValue(varName);
                for (int i = 1; i < fullVariable.size(); i++) {
                    JsonNode t = fullVariable.get(i);
                    if (t.isTextual()) {
                        value = findValueInAstMap(value, t.asText());
                    } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.STRING) {
                        value = findValueInAstMap(value, t.get(1).asText());
                    } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.INT) {
                        value = findValueInAstMap(value, t.get(1).asInt());
                    } else {
                        break;
                    }

                    if (value == null) break;
                }
                if (value != null) return value;
            }
        }

        return nodeFactory.jsonArray(AstNodeType.SELECTOR, nodeFactory.jsonArray(processedTokens));
    }

    private ArrayNode findValueInAstMap(ArrayNode mapEntries, String key) {
        mapEntries = (ArrayNode) mapEntries.get(1);
        for (JsonNode jsonNode : mapEntries) {
            ArrayNode mapEntry = (ArrayNode) jsonNode;
            JsonNode entryKey = mapEntry.get(0);
            ArrayNode entryValue = (ArrayNode) mapEntry.get(1);

            if (entryKey.asText().equals(key)) {
                return entryValue;
            }
        }
        return null;
    }

    private ArrayNode findValueInAstMap(ArrayNode mapEntries, int index) {
        mapEntries = (ArrayNode) mapEntries.get(1);
        boolean isArray = true;
        for (JsonNode mapEntry : mapEntries) {
            JsonNode key = mapEntry.get(0);
            isArray = isArray && key.isNull();
        }
        if (!isArray) {
            for (JsonNode jsonNode : mapEntries) {
                ArrayNode mapEntry = (ArrayNode) jsonNode;
                JsonNode entryKey = mapEntry.get(0);
                ArrayNode entryValue = (ArrayNode) mapEntry.get(1);

                if (entryKey.asInt() == index) {
                    return entryValue;
                }
            }
        }

        if (index < mapEntries.size()) {
            return (ArrayNode) mapEntries.get(index).get(1);
        } else {
            return null;
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

        public void removeVar(String varName) {
            for (Map<String, ArrayNode> frame : stackVars) {
                if (frame.containsKey(varName)) {
                    frame.remove(varName);
                }
            }
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
