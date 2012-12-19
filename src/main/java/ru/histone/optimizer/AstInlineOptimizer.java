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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.MacroFunc;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.Iterator;

public class AstInlineOptimizer {

    private NodeFactory nodeFactory = new NodeFactory(new ObjectMapper());

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
            case AstNodeType.TRUE:
            case AstNodeType.FALSE:
            case AstNodeType.NULL:
            case AstNodeType.INT:
            case AstNodeType.DOUBLE:
            case AstNodeType.STRING:
                return node;

//            case AstNodeType.MAP:
            //TODO: check array->map
//                return inlineMap(nodeType, (ArrayNode)node.get(1), context);
//            case AstNodeType.ARRAY:
//                return inlineArray(nodeType, (ArrayNode)node.get(1), context);
//
//            case AstNodeType.OBJECT:
//                return inlineObject(nodeType, (ArrayNode)node.get(1), context);

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
                return inlineBinaryOperation(nodeType, (ArrayNode)node.get(1), (ArrayNode)node.get(2), context);

            case AstNodeType.NEGATE:
            case AstNodeType.NOT:
                return inlineUnaryOperation(nodeType, (ArrayNode)node.get(1), context);


            case AstNodeType.IF:
                return inlineIf(nodeType, (ArrayNode)node.get(1), context);

            case AstNodeType.FOR:
                return inlineFor(nodeType, (ArrayNode)node.get(1), (ArrayNode)node.get(2), (ArrayNode)node.get(3), context);

            case AstNodeType.CALL:
                return inlineCall(nodeType, (ArrayNode)node.get(1), (ArrayNode)node.get(2), (ArrayNode)node.get(3), context);

            case AstNodeType.MACRO:
                return inlineMacro(nodeType, (ArrayNode)node.get(1), (ArrayNode)node.get(2), (ArrayNode)node.get(3), context);

            case AstNodeType.SELECTOR:
                return inlineSelector(nodeType, (ArrayNode)node.get(1), context);

//            case AstNodeType.IMPORT:
//            case -AstNodeType.IMPORT:
//                return inlineImport(nodeType, (ArrayNode)node.get(1).getAsJsonPrimitive(), context);

            default:
                return node;
        }
    }

    private ArrayNode inlineBinaryOperation(int nodeType, ArrayNode leftAst, ArrayNode rightAst, InlineOptimizerContext context) throws HistoneException {
        leftAst = inlineNode(leftAst, context);
        rightAst = inlineNode(rightAst, context);
        return nodeFactory.jsonArray(nodeType, leftAst, rightAst);
    }

    private ArrayNode inlineUnaryOperation(int nodeType, ArrayNode ast, InlineOptimizerContext context) throws HistoneException {
        ast = inlineNode(ast, context);
        return nodeFactory.jsonArray(nodeType, ast);
    }

    private ArrayNode inlineArray(int nodeType, ArrayNode elements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        for (JsonNode elem : elements) {
            elem = inlineNode((ArrayNode) elem, context);
            result.add(elem);
        }

        return nodeFactory.jsonArray(nodeType, result);
    }

    private ArrayNode inlineObject(int nodeType, ArrayNode elements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        for (JsonNode elem : elements) {
            ArrayNode objElem = nodeFactory.jsonArray();

            JsonNode key = elem.get(0);
            ArrayNode val = (ArrayNode) elem.get(1);

            val = inlineNode(val, context);

            objElem.add(key);
            objElem.add(val);
            result.add(objElem);
        }

        return nodeFactory.jsonArray(nodeType, result);
    }

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

//    private ArrayNode inlineImport(JsonNode uri, InlineOptimizerContext context) {
//        return null;  //To change body of created methods use File | Settings | File Templates.
//    }

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
            context.putVar(macroArgsAst.get(i).asText(), (ArrayNode)args.get(i));
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

    private ArrayNode inlineMacro(int nodeType, JsonNode ident, ArrayNode args, ArrayNode statements, InlineOptimizerContext context) throws EvaluatorException {
        String name = ident.asText();

        MacroFunc func = new MacroFunc();
        func.setArgs(args);
        func.setStatements(statements);
        context.putMacro(name, func);

        if (nodeType > 0) {
            return nodeFactory.jsonArray(AstNodeType.STRING);
        } else {
            return nodeFactory.jsonArray(nodeType, ident, args, statements);

        }
    }

//    private ArrayNode inlineImport(int nodeType, JsonPrimitive ident, InlineOptimizerContext context) throws EvaluatorException {
//        String uri = ident.asText();
//
//        resourceLoaderManager.
//
//        return nodeFactory.jsonArray(nodeType, ident);
//    }

    private boolean isString(JsonNode element) {
        return element.isTextual();
    }
    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }

    private ArrayNode makeElementUnsafe(ArrayNode element) {
        boolean typeUpdated = false;
        ArrayNode result = nodeFactory.jsonArray();
        for (JsonNode item : element) {
            if (typeUpdated) {
                result.add(item);
            } else {
                result.add(nodeFactory.jsonArray(-item.asInt()));
                typeUpdated = true;
            }
        }
        return result;
    }
                 
}
