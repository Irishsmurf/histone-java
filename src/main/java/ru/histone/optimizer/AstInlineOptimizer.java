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
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.MacroFunc;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.Iterator;

/**
 * Inlines safe expressions.
 * Meaningful functions are inlineCall and inlineSelector (because they perform actual inlining).
 * Other function just call 'inlining' for their children nodes.
 *
 * @author sazonovkirill@gmail.com
 */
public class AstInlineOptimizer {
    private NodeFactory nodeFactory;

    public ArrayNode inline(ArrayNode ast) throws HistoneException {
        InlineOptimizerContext context = new InlineOptimizerContext();
        return inline(ast, context);
    }

    public ArrayNode inline(ArrayNode ast, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        for (JsonNode node : ast) {
            int nodeType = 0;
            if (!isString(node)) {
                nodeType = getNodeType((ArrayNode) node);
                node = inlineNode((ArrayNode) node, context);
            }

            if (nodeType == AstNodeType.CALL) {
                for (JsonNode elem : node) {
                    result.add(elem);
                }
            } else {
                result.add(node);
            }
        }

        return result;
    }

    private ArrayNode inlineNode(ArrayNode node, InlineOptimizerContext context) throws HistoneException {
        int nodeType = getNodeType(node);
        switch (Math.abs(nodeType)) {
            //
            //  No sense of inlining for constants
            //
            case AstNodeType.TRUE:
            case AstNodeType.FALSE:
            case AstNodeType.NULL:
            case AstNodeType.INT:
            case AstNodeType.DOUBLE:
            case AstNodeType.STRING:
                return node;

            //
            //  Recusive inlining for binray operations
            //
            case AstNodeType.ADD:
            case AstNodeType.SUB:
            case AstNodeType.MUL:
            case AstNodeType.DIV:
            case AstNodeType.MOD:
            case AstNodeType.OR:
            case AstNodeType.AND:
            case AstNodeType.EQUAL:
            case AstNodeType.NOT_EQUAL:
            case AstNodeType.LESS_OR_EQUAL:
            case AstNodeType.LESS_THAN:
            case AstNodeType.GREATER_OR_EQUAL:
            case AstNodeType.GREATER_THAN:
                return inlineBinaryOperation(nodeType, (ArrayNode) node.get(1), (ArrayNode) node.get(2), context);

            //
            //  Recusive inlining for unary operations
            //
            case AstNodeType.NEGATE:
            case AstNodeType.NOT:
                return inlineUnaryOperation(nodeType, (ArrayNode) node.get(1), context);


            case AstNodeType.IF:
                return inlineIf(nodeType, (ArrayNode) node.get(1), context);

            case AstNodeType.FOR:
                return inlineFor(nodeType, (ArrayNode) node.get(1), (ArrayNode) node.get(2), (ArrayNode) node.get(3), context);

            case AstNodeType.CALL:
                return inlineCall(nodeType, node.get(1), node.get(2), node.get(3), context);

            case AstNodeType.MACRO:
                return inlineMacro(nodeType, node.get(1), (ArrayNode) node.get(2), (ArrayNode) node.get(3), context);

            //
            //  Actual inlining for selector
            //
            case AstNodeType.SELECTOR:
                return inlineSelector(nodeType, (ArrayNode) node.get(1), context);

            default:
                return node;
        }
    }

    /**
     * Inlines both left and right args.
     */
    private ArrayNode inlineBinaryOperation(int nodeType, ArrayNode leftAst, ArrayNode rightAst, InlineOptimizerContext context) throws HistoneException {
        leftAst = inlineNode(leftAst, context);
        rightAst = inlineNode(rightAst, context);
        return nodeFactory.jsonArray(nodeType, leftAst, rightAst);
    }

    /**
     * Inlines its arg.
     */
    private ArrayNode inlineUnaryOperation(int nodeType, ArrayNode ast, InlineOptimizerContext context) throws HistoneException {
        ast = inlineNode(ast, context);
        return nodeFactory.jsonArray(nodeType, ast);
    }

    /**
     * Inlines items of an array.
     */
    private ArrayNode inlineArray(int nodeType, ArrayNode elements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        for (JsonNode elem : elements) {
            elem = inlineNode((ArrayNode) elem, context);
            result.add(elem);
        }

        return nodeFactory.jsonArray(nodeType, result);
    }

    /**
     * Inlines variable value instead of variable (selector) if context contains it. Else no changes.
     */
    private ArrayNode inlineSelector(int nodeType, ArrayNode selector, InlineOptimizerContext context) {
        JsonNode varElem = selector.get(0);

        if (varElem.isTextual()) {
            String varName = varElem.asText();
            if (context.hasVar(varName)) {
                if (selector.size() == 1) {
                    return context.getVar(varName);
                } else {
                    ArrayNode resultSelectorElements = nodeFactory.jsonArray();
//                    result.add(new JsonPrimitive(AstNodeType.SELECTOR));
                    resultSelectorElements.add(context.getVar(varName));
                    Iterator<JsonNode> iter = selector.iterator();
                    iter.next();//iter.size>1, so we can safely skip first selector element
                    while (iter.hasNext()) {
                        resultSelectorElements.add(iter.next());
                    }
                    return nodeFactory.jsonArray(AstNodeType.SELECTOR, resultSelectorElements);
                }
            } else {
                return nodeFactory.jsonArray(nodeType, selector);
            }
        } else {
            return nodeFactory.jsonArray(nodeType, selector);
        }
    }

    /**
     * Inlines child conditions' expressions and statements.
     */
    private ArrayNode inlineIf(int nodeType, ArrayNode conditions, InlineOptimizerContext context) throws HistoneException {
        ArrayNode conditionsOut = nodeFactory.jsonArray();

        context.saveState();
        for (JsonNode condition : conditions) {
            JsonNode expressionAst = condition.get(0);
            ArrayNode statementsAst = (ArrayNode) condition.get(1);

            expressionAst = inlineNode((ArrayNode) expressionAst, context);
            statementsAst = inline(statementsAst, context);

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restoreState();

        return nodeFactory.jsonArray(nodeType, conditionsOut);
    }

    /**
     * Inlines collection and statements.
     */
    private ArrayNode inlineFor(int nodeType, ArrayNode iterator, ArrayNode collection, ArrayNode statements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode statementsForAst = (ArrayNode) statements.get(0);
        ArrayNode statementsElseAst = statements.size() > 1 ? (ArrayNode) statements.get(1) : null;

        JsonNode collectionAst = inlineNode(collection, context);
        statementsForAst = inline(statementsForAst, context);
        statementsElseAst = (statementsElseAst != null) ? inline(statementsElseAst, context) : null;

        ArrayNode statementsArr = nodeFactory.jsonArray();

        statementsArr.add(statementsForAst);
        if (statementsElseAst != null) {
            statementsArr.add(statementsElseAst);
        }

        return nodeFactory.jsonArray(nodeType, iterator, collectionAst, statementsArr);

    }

    private ArrayNode inlineCall(int nodeType, JsonNode target, JsonNode nameElement, JsonNode args, InlineOptimizerContext context) throws HistoneException {
        if (!target.isNull() || !isString(nameElement) || nodeType < 0) {
            return nodeFactory.jsonArray(nodeType, target, nameElement, args);
        }

        String macroName = nameElement.asText();

        if (macroName == null || macroName.length() == 0 || !context.hasMacro(macroName)) {
            return nodeFactory.jsonArray(nodeType, target, nameElement, args);
        }

        MacroFunc macro = context.getMacro(macroName);
        ArrayNode macroArgsAst = macro.getArgs();
        ArrayNode macroBodyAst = macro.getStatements();

        context.saveState();

        // prepare inline vars for defined macro argument
        for (int i = 0; i < macroArgsAst.size(); i++) {
            context.putVar(macroArgsAst.get(i).asText(), (ArrayNode) args.get(i));
        }

        // prepare inline vars for 'self' reserved word
        ArrayNode argumentsAst = nodeFactory.jsonArray();
        argumentsAst.add(nodeFactory.jsonString("arguments"));
        //TODO:
//        argumentsAst.add(nodeFactory.jsonArray(AstNodeType.ARRAY, args));
//        ArrayNode self = nodeFactory.jsonArray(AstNodeType.OBJECT, AstNodeFactory.createArray(argumentsAst));
//        context.putVar("self", self);

        // do inline optimization using prepared context
        macroBodyAst = inline(macroBodyAst, context);

        context.restoreState();

        return macroBodyAst;
    }

    /**
     * If macros is safe, this function puts is to context and replaces by empty String[] array. Otherwise no changes.
     */
    private ArrayNode inlineMacro(int nodeType, JsonNode identifier, ArrayNode args, ArrayNode statements, InlineOptimizerContext context) throws EvaluatorException {
        String name = identifier.asText();

        MacroFunc macroFunc = new MacroFunc();
        macroFunc.setArgs(args);
        macroFunc.setStatements(statements);
        context.putMacro(name, macroFunc);

        if (nodeType > 0) {
            // if macro is safe, replace it by empty String[] array
            return nodeFactory.jsonArray(AstNodeType.STRING);
        } else {
            // Otherwise no changes
            return nodeFactory.jsonArray(nodeType, identifier, args, statements);
        }
    }

    private boolean isString(JsonNode element) {
        return element.isTextual();
    }

    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
}
