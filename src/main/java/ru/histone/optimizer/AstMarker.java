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
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.Assert;
import ru.histone.utils.StringUtils;

import java.util.*;

/**
 * @author sazonovkirill@gmail.com
 */
public class AstMarker extends BaseOptimization {
    private Context context;

    public AstMarker(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    public ArrayNode mark(ArrayNode ast) throws HistoneException {
        context = new Context();

        return process(ast);
    }

    private boolean safeMapItems(ArrayNode mapEntries) {
        boolean isSafe = true;
        for (JsonNode mapEntry : mapEntries) {
            JsonNode entryKey = mapEntry.get(0);
            JsonNode entryValue = mapEntry.get(1);

            if (!entryValue.isNull()) {
                isSafe = isSafe && safeAstNode(entryValue);
            }
        }
        return isSafe;
    }

    public static boolean safeArray(ArrayNode array) {
        boolean isSafe = true;
        for (JsonNode node : array) {
            isSafe = isSafe && safeAstNode(node);
        }
        return isSafe;
    }

    public static boolean safeArray(JsonNode[] array) {
        boolean isSafe = true;
        for (JsonNode node : array) {
            isSafe = isSafe && safeAstNode(node);
        }
        return isSafe;
    }

    public static boolean safeAstNode(JsonNode node) {
        return (node.isArray() && getNodeType((ArrayNode) node) > 0) || isString(node);
    }

    public static boolean unsafeAstNode(JsonNode node) {
        return node.isArray() && getNodeType((ArrayNode) node) < 0;
    }

    /**
     * Statements are safe, if all of them are safe.
     */
    protected JsonNode processStatements(ArrayNode statements) throws HistoneException {
        statements = (ArrayNode) statements.get(1);

        JsonNode[] statementsOut = new JsonNode[statements.size()];
        for (int i = 0; i < statements.size(); i++) {
            statementsOut[i] = processAstNode(statements.get(i));
        }
        boolean isSafe = safeArray(statementsOut);

        return ast(isSafe, AstNodeType.STATEMENTS, nodeFactory.jsonArray(statementsOut));
    }

    /**
     * Var is safe, if its expression is safe.
     */
    protected JsonNode processVariable(ArrayNode variable) throws HistoneException {
        Assert.isTrue(variable.size() == 3);

        JsonNode varName = variable.get(1);
        JsonNode varExpression = variable.get(2);
        JsonNode processedVarExpression = processAstNode(varExpression);

        boolean isSafe = safeAstNode(processedVarExpression);
        if (isSafe) {
            context.addSafeVar(varName.asText());
        }

        return ast(isSafe, AstNodeType.VAR, varName, processedVarExpression);
    }

    /**
     * Map is safe is all its items are safe.
     */
    protected JsonNode processMap(ArrayNode map) throws HistoneException {
        ArrayNode items = (ArrayNode) map.get(1);
        for (JsonNode item : items) {
            if (item.isArray()) {
                ArrayNode arr = (ArrayNode) item;
                JsonNode key = arr.get(0);
                JsonNode value = arr.get(1);

                arr.removeAll();
                arr.add(key);
                arr.add(value);
            }
        }

        boolean isSafe = safeMapItems(items);
        return ast(isSafe, AstNodeType.MAP, items);
    }

    /**
     * Macros is safe if it statements are safe.
     */
    protected JsonNode processMacro(ArrayNode macro) throws HistoneException {
        JsonNode name = macro.get(1);
        ArrayNode args = (ArrayNode) macro.get(2);
        ArrayNode statements = (ArrayNode) macro.get(3);

        pushContext();

        // Adding macro arguments and 'self' keyword as safe variables.
        context.addSafeVar(KEYWORD_SELF);
        Set<String> argNames = new HashSet<String>();
        if (!args.isNull()) {
            for (JsonNode arg : args) {
                argNames.add(arg.asText());
                context.addSafeVar(arg.asText());
            }
        }

        args = (ArrayNode) processAstNode(args);
        ArrayNode statementsOut = (ArrayNode) processArrayOfAstNodes(statements);
        popContext();

        boolean isSafe = safeArray(statementsOut);

        return ast(isSafe, AstNodeType.MACRO, name, args, statements);
    }

    @Override
    public void pushContext() {
        context.push();
    }

    @Override
    public void popContext() {
        context.pop();
    }

    /**
     * CALL is safe if:
     * 1. It's macro call, not function of type call (so target block is null).
     * 2. Macro name is string (what else can it bee?
     * 3. Macros is safe in context.
     * 4. All args are safe.
     */
    protected JsonNode processCall(ArrayNode call) throws HistoneException {
        JsonNode target = call.get(1);
        JsonNode name = call.get(2);
        JsonNode args = call.get(3);

        if (!target.isNull() || !isString(name) || StringUtils.isBlank(name.asText())) {
            return ast(false, AstNodeType.CALL, target, name, args);
        }

        String macrosName = name.asText();
        boolean isSafe = context.isMacroSafe(macrosName);

        if (args.isArray()) {
            args = processArrayOfAstNodes(args);
            isSafe = isSafe && safeArray((ArrayNode) args);
        }

        return ast(isSafe, AstNodeType.CALL, target, name, args);
    }

    /**
     * Selector is safe if variable is safe or all children elements are safe.
     */
    protected JsonNode processSelector(ArrayNode selector) throws HistoneException {
        JsonNode fullVariable = selector.get(1);

        List<String> ss = new ArrayList<String>();
        for (JsonNode t : fullVariable) {
            if (t.isTextual()) {
                ss.add(t.asText());
            } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.STRING) {
                ss.add(t.get(1).asText());
            } else if (t.isArray() && t.size() == 2 && t.get(0).isInt() && t.get(0).asInt() == AstNodeType.INT) {
                ss.add(String.valueOf(t.get(1).asInt()));
            } else {
                return ast(false, AstNodeType.SELECTOR, fullVariable);
            }
        }
        String fullVariableName = StringUtils.join(ss, ".");
        boolean isSafe = context.isVarSafe(fullVariableName);

        return ast(isSafe, AstNodeType.SELECTOR, fullVariable);
    }

    /**
     * FOR is safe if all its statements are safe and collection, for iterarated over, is safe also.
     */
    protected JsonNode processFor(ArrayNode for_) throws HistoneException {
        ArrayNode var = (ArrayNode) for_.get(1);
        ArrayNode collection = (ArrayNode) for_.get(2);
        ArrayNode statements = (ArrayNode) for_.get(3).get(0);

        String iterVal = var.get(0).asText();
        String iterKey = (var.size() > 1) ? var.get(1).asText() : null;

        collection = (ArrayNode) processAstNode(collection);

        pushContext();
        context.addSafeVar(iterVal);
        if (iterKey != null) {
            context.addSafeVar(iterKey);
        }
        context.addSafeVar("self.index");
        context.addSafeVar("self.last");
        statements = (ArrayNode) processArrayOfAstNodes(statements);
        boolean isSafe = safeArray(statements);
        popContext();

        return ast(isSafe, AstNodeType.FOR, var, collection, nodeFactory.jsonArray(statements));
    }

    /**
     * If is safe if for all conditions both expression and statements are safe.
     */
    protected JsonNode processIf(ArrayNode if_) throws HistoneException {
        ArrayNode conditions = (ArrayNode) if_.get(1);

        boolean isSafe = true;
        ArrayNode conditionsOut = nodeFactory.jsonArray();
        for (JsonNode condition : conditions) {
            JsonNode expression = condition.get(0);
            JsonNode statements = condition.get(1);

            expression = processAstNode(expression);
            isSafe = isSafe && safeAstNode(expression);

            pushContext();
            JsonNode[] statementsOut = new JsonNode[statements.size()];
            for (int i = 0; i < statements.size(); i++) {
                statementsOut[i] = processAstNode(statements.get(i));
                isSafe = isSafe && safeAstNode(statementsOut[i]);
            }
            popContext();

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expression);
            conditionOut.add(nodeFactory.jsonArray(statementsOut));
            conditionsOut.add(conditionOut);
        }

        return ast(isSafe, AstNodeType.IF, conditionsOut);
    }

    protected JsonNode processOperationOverArguments(ArrayNode ast) throws HistoneException {
        Assert.notNull(ast);
        Assert.notNull(ast.size() > 1);
        Assert.isTrue(ast.get(0).isNumber());
        for (int i = 1; i < ast.size(); i++) Assert.isTrue(ast.get(i).isArray());
        Assert.isTrue(getOperationsOverArguments().contains(ast.get(0).asInt()));

        int operationType = ast.get(0).asInt();
        boolean isSafe = true;
        final List<ArrayNode> processedArguments = new ArrayList<ArrayNode>();
        for (int i = 1; i < ast.size(); i++) {
            JsonNode processedArg = processAstNode(ast.get(i));
            isSafe = isSafe && safeAstNode(processedArg);
            Assert.isTrue(processedArg.isArray());
            processedArguments.add((ArrayNode) processedArg);
        }

        return ast(isSafe, operationType, processedArguments);
    }

    protected ArrayNode ast(boolean isSafe, int operationType, JsonNode... arguments) {
        return ast(isSafe ? operationType : -operationType, arguments);
    }

    protected ArrayNode ast(boolean isSafe, int operationType, Collection<? extends ArrayNode> arguments) {
        ArrayNode array = nodeFactory.jsonArray(IntNode.valueOf(isSafe ? operationType : -operationType));
        for (ArrayNode argument : arguments) {
            array.add(argument);
        }
        return array;
    }


    class Context {
        private Deque<Set<String>> declaredVariables;
        private Deque<Map<String, Set<String>>> declaredMacroses;

        public Context() {
            this.declaredVariables = new ArrayDeque<Set<String>>();
            this.declaredMacroses = new ArrayDeque<Map<String, Set<String>>>();
            push();
        }

        public void push() {
            declaredVariables.push(new HashSet<String>());
            declaredMacroses.push(new HashMap<String, Set<String>>());
        }

        public void pop() {
            declaredVariables.pollFirst();
            declaredMacroses.pollFirst();
        }

        public boolean isVarSafe(String name) {
            return declaredVariables.getFirst().contains(name);
        }

        public void addSafeVar(String name) {
            declaredVariables.getFirst().add(name);
        }

        public boolean isMacroSafe(String name) {
            for (Map<String, Set<String>> stack : declaredMacroses) {
                if (stack.containsKey(name)) {
                    return true;
                }
            }
            return false;
        }

        public void addSafeMacro(String name, Set<String> argNames) {
            declaredMacroses.getFirst().put(name, argNames);
        }
    }
}
