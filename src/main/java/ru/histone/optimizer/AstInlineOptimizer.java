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

import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.HistoneException;

public class AstInlineOptimizer {

/*    public ArrayNode inline(ArrayNode ast) throws HistoneException {
        InlineOptimizerContext context = new InlineOptimizerContext();
        return inline(ast, context);
    }

    public ArrayNode inline(ArrayNode ast, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = new ArrayNode();

        for (JsonNode node : ast) {
            int nodeType = 0;
            if (!isString(node)) {
                nodeType = getNodeType(node.getAsArrayNode());
                node = inlineNode(node.getAsArrayNode(), context);
            }

            if (nodeType == AstNodeType.CALL) {
                for (JsonNode elem : node.getAsArrayNode()) {
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
//                return inlineMap(nodeType, node.get(1).getAsArrayNode(), context);
//            case AstNodeType.ARRAY:
//                return inlineArray(nodeType, node.get(1).getAsArrayNode(), context);
//
//            case AstNodeType.OBJECT:
//                return inlineObject(nodeType, node.get(1).getAsArrayNode(), context);

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
                return inlineBinaryOperation(nodeType, node.get(1).getAsArrayNode(), node.get(2).getAsArrayNode(), context);

            case AstNodeType.NEGATE:
            case AstNodeType.NOT:
                return inlineUnaryOperation(nodeType, node.get(1).getAsArrayNode(), context);


            case AstNodeType.IF:
                return inlineIf(nodeType, node.get(1).getAsArrayNode(), context);

            case AstNodeType.FOR:
                return inlineFor(nodeType, node.get(1).getAsArrayNode(), node.get(2).getAsArrayNode(), node.get(3).getAsArrayNode(), context);

            case AstNodeType.CALL:
                return inlineCall(nodeType, node.get(1), node.get(2), node.get(3), context);

            case AstNodeType.MACRO:
                return inlineMacro(nodeType, node.get(1).getAsJsonPrimitive(), node.get(2).getAsArrayNode(), node.get(3).getAsArrayNode(), context);

            case AstNodeType.SELECTOR:
                return inlineSelector(nodeType, node.get(1).getAsArrayNode(), context);

//            case AstNodeType.IMPORT:
//            case -AstNodeType.IMPORT:
//                return inlineImport(nodeType, node.get(1).getAsJsonPrimitive(), context);

            default:
                return node;
        }
    }

    private ArrayNode inlineBinaryOperation(int nodeType, ArrayNode leftAst, ArrayNode rightAst, InlineOptimizerContext context) throws HistoneException {
        leftAst = inlineNode(leftAst, context).getAsArrayNode();
        rightAst = inlineNode(rightAst, context).getAsArrayNode();
        return AstNodeFactory.createNode(nodeType, leftAst, rightAst);
    }

    private ArrayNode inlineUnaryOperation(int nodeType, ArrayNode ast, InlineOptimizerContext context) throws HistoneException {
        ast = inlineNode(ast, context).getAsArrayNode();
        return AstNodeFactory.createNode(nodeType, ast);
    }

    private ArrayNode inlineArray(int nodeType, ArrayNode elements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = new ArrayNode();

        for (JsonNode elem : elements) {
            elem = inlineNode(elem.getAsArrayNode(), context);
            result.add(elem);
        }

        return AstNodeFactory.createNode(nodeType, result);
    }

    private ArrayNode inlineObject(int nodeType, ArrayNode elements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode result = new ArrayNode();

        for (JsonNode elem : elements) {
            ArrayNode objElem = new ArrayNode();

            JsonNode key = elem.getAsArrayNode().get(0);
            ArrayNode val = elem.getAsArrayNode().get(1).getAsArrayNode();

            val = inlineNode(val.getAsArrayNode(), context);

            objElem.add(key);
            objElem.add(val);
            result.add(objElem);
        }

        return AstNodeFactory.createNode(nodeType, result);
    }

    private ArrayNode inlineSelector(int nodeType, ArrayNode selector, InlineOptimizerContext context) {
        JsonNode varElem = selector.get(0);

        if (varElem.isJsonPrimitive()) {
            String varName = varElem.getAsJsonPrimitive().getAsString();
            if (context.hasVar(varName)) {
                if (selector.size() == 1) {
                    return context.getVar(varName);
                } else {
                    ArrayNode resultSelectorElements = new ArrayNode();
//                    result.add(new JsonPrimitive(AstNodeType.SELECTOR));
                    resultSelectorElements.add(context.getVar(varName));
                    Iterator<JsonNode> iter = selector.iterator();
                    iter.next();//iter.size>1, so we can safely skip first selector element
                    while (iter.hasNext()) {
                        resultSelectorElements.add(iter.next());
                    }
                    return AstNodeFactory.createNode(AstNodeType.SELECTOR, resultSelectorElements);
                }
            } else {
                return AstNodeFactory.createNode(nodeType, selector);
            }
        } else {
            return AstNodeFactory.createNode(nodeType, selector);
        }
    }

//    private ArrayNode inlineImport(JsonNode uri, InlineOptimizerContext context) {
//        return null;  //To change body of created methods use File | Settings | File Templates.
//    }

    private ArrayNode inlineIf(int nodeType, ArrayNode conditions, InlineOptimizerContext context) throws HistoneException {
        ArrayNode conditionsOut = new ArrayNode();

        context.saveState();
        for (JsonNode condition : conditions) {
            JsonNode expressionAst = condition.getAsArrayNode().get(0);
            ArrayNode statementsAst = condition.getAsArrayNode().get(1).getAsArrayNode();

            expressionAst = inlineNode(expressionAst.getAsArrayNode(), context);
            statementsAst = inline(statementsAst, context);

            ArrayNode conditionOut = new ArrayNode();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restoreState();

        return AstNodeFactory.createNode(nodeType, conditionsOut);
    }

    private ArrayNode inlineFor(int nodeType, ArrayNode iterator, ArrayNode collection, ArrayNode statements, InlineOptimizerContext context) throws HistoneException {
        ArrayNode statementsForAst = statements.getAsArrayNode().get(0).getAsArrayNode();
        ArrayNode statementsElseAst = statements.getAsArrayNode().size() > 1 ? statements.getAsArrayNode().get(1).getAsArrayNode() : null;

        JsonNode collectionAst = inlineNode(collection, context);
        statementsForAst = inline(statementsForAst, context);
        statementsElseAst = (statementsElseAst != null) ? inline(statementsElseAst, context) : null;

        ArrayNode statementsArr = new ArrayNode();

        statementsArr.add(statementsForAst);
        if (statementsElseAst != null) {
            statementsArr.add(statementsElseAst);
        }

        return AstNodeFactory.createNode(nodeType, iterator, collectionAst, statementsArr);

    }

    private ArrayNode inlineCall(int nodeType, JsonNode target, JsonNode nameElement, JsonNode args, InlineOptimizerContext context) throws HistoneException {
        if (!target.isJsonNull() || !isString(nameElement) || nodeType < 0) {
            return AstNodeFactory.createNode(nodeType, target, nameElement, args);
        }

        String macroName = nameElement.getAsJsonPrimitive().getAsString();

        if (macroName == null || macroName.length() == 0 || !context.hasMacro(macroName)) {
            return AstNodeFactory.createNode(nodeType, target, nameElement, args);
        }

        MacroFunc macro = context.getMacro(macroName);
        ArrayNode macroArgsAst = macro.getArgs();
        ArrayNode macroBodyAst = macro.getStatements();

        context.saveState();

        // prepare inline vars for defined macro argument
        for (int i = 0; i < macroArgsAst.size(); i++) {
            context.putVar(macroArgsAst.get(i).getAsString(), args.getAsArrayNode().get(i).getAsArrayNode());
        }

        // prepare inline vars for 'self' reserved word
        ArrayNode argumentsAst = new ArrayNode();
        argumentsAst.add(new JsonPrimitive("arguments"));
        //TODO:
//        argumentsAst.add(AstNodeFactory.createNode(AstNodeType.ARRAY, args));
//        ArrayNode self = AstNodeFactory.createNode(AstNodeType.OBJECT, AstNodeFactory.createArray(argumentsAst));
//        context.putVar("self", self);

        // do inline optimization using prepared context
        macroBodyAst = inline(macroBodyAst, context);

        context.restoreState();

        return macroBodyAst;
    }

    private ArrayNode inlineMacro(int nodeType, JsonPrimitive ident, ArrayNode args, ArrayNode statements, InlineOptimizerContext context) throws EvaluatorException {
        String name = ident.getAsString();

        MacroFunc func = new MacroFunc();
        func.setArgs(args);
        func.setStatements(statements);
        context.putMacro(name, func);

        if (nodeType > 0) {
            return AstNodeFactory.createNode(AstNodeType.STRING, "");
        } else {
            return AstNodeFactory.createNode(nodeType, ident, args, statements);

        }
    }

//    private ArrayNode inlineImport(int nodeType, JsonPrimitive ident, InlineOptimizerContext context) throws EvaluatorException {
//        String uri = ident.getAsString();
//
//        resourceLoaderManager.
//
//        return AstNodeFactory.createNode(nodeType, ident);
//    }

    private boolean isString(JsonNode element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }

    private ArrayNode makeElementUnsafe(ArrayNode element) {
        boolean typeUpdated = false;
        ArrayNode result = new ArrayNode();
        for (JsonNode item : element) {
            if (typeUpdated) {
                result.add(item);
            } else {
                result.add(new JsonPrimitive(-item.getAsJsonPrimitive().getAsInt()));
                typeUpdated = true;
            }
        }
        return result;
    }
                  */
}
