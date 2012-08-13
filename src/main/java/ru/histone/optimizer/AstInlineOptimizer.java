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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ru.histone.HistoneException;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.MacroFunc;
import ru.histone.parser.AstNodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.Iterator;

public class AstInlineOptimizer {

    public JsonArray inline(JsonArray ast) throws HistoneException {
        InlineOptimizerContext context = new InlineOptimizerContext();
        return inline(ast, context);
    }

    public JsonArray inline(JsonArray ast, InlineOptimizerContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        for (JsonElement node : ast) {
            int nodeType = 0;
            if (!isString(node)) {
                nodeType = getNodeType(node.getAsJsonArray());
                node = inlineNode(node.getAsJsonArray(), context);
            }

            if (nodeType == AstNodeType.CALL) {
                for (JsonElement elem : node.getAsJsonArray()) {
                    result.add(elem);
                }
            } else {
                result.add(node);
            }
        }

        return result;
    }

    private JsonArray inlineNode(JsonArray node, InlineOptimizerContext context) throws HistoneException {
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
//                return inlineMap(nodeType, node.get(1).getAsJsonArray(), context);
//            case AstNodeType.ARRAY:
//                return inlineArray(nodeType, node.get(1).getAsJsonArray(), context);
//
//            case AstNodeType.OBJECT:
//                return inlineObject(nodeType, node.get(1).getAsJsonArray(), context);

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
                return inlineBinaryOperation(nodeType, node.get(1).getAsJsonArray(), node.get(2).getAsJsonArray(), context);

            case AstNodeType.NEGATE:
            case AstNodeType.NOT:
                return inlineUnaryOperation(nodeType, node.get(1).getAsJsonArray(), context);


            case AstNodeType.IF:
                return inlineIf(nodeType, node.get(1).getAsJsonArray(), context);

            case AstNodeType.FOR:
                return inlineFor(nodeType, node.get(1).getAsJsonArray(), node.get(2).getAsJsonArray(), node.get(3).getAsJsonArray(), context);

            case AstNodeType.CALL:
                return inlineCall(nodeType, node.get(1), node.get(2), node.get(3), context);

            case AstNodeType.MACRO:
                return inlineMacro(nodeType, node.get(1).getAsJsonPrimitive(), node.get(2).getAsJsonArray(), node.get(3).getAsJsonArray(), context);

            case AstNodeType.SELECTOR:
                return inlineSelector(nodeType, node.get(1).getAsJsonArray(), context);

//            case AstNodeType.IMPORT:
//            case -AstNodeType.IMPORT:
//                return inlineImport(nodeType, node.get(1).getAsJsonPrimitive(), context);

            default:
                return node;
        }
    }

    private JsonArray inlineBinaryOperation(int nodeType, JsonArray leftAst, JsonArray rightAst, InlineOptimizerContext context) throws HistoneException {
        leftAst = inlineNode(leftAst, context).getAsJsonArray();
        rightAst = inlineNode(rightAst, context).getAsJsonArray();
        return AstNodeFactory.createNode(nodeType, leftAst, rightAst);
    }

    private JsonArray inlineUnaryOperation(int nodeType, JsonArray ast, InlineOptimizerContext context) throws HistoneException {
        ast = inlineNode(ast, context).getAsJsonArray();
        return AstNodeFactory.createNode(nodeType, ast);
    }

    private JsonArray inlineArray(int nodeType, JsonArray elements, InlineOptimizerContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        for (JsonElement elem : elements) {
            elem = inlineNode(elem.getAsJsonArray(), context);
            result.add(elem);
        }

        return AstNodeFactory.createNode(nodeType, result);
    }

    private JsonArray inlineObject(int nodeType, JsonArray elements, InlineOptimizerContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        for (JsonElement elem : elements) {
            JsonArray objElem = new JsonArray();

            JsonElement key = elem.getAsJsonArray().get(0);
            JsonArray val = elem.getAsJsonArray().get(1).getAsJsonArray();

            val = inlineNode(val.getAsJsonArray(), context);

            objElem.add(key);
            objElem.add(val);
            result.add(objElem);
        }

        return AstNodeFactory.createNode(nodeType, result);
    }

    private JsonArray inlineSelector(int nodeType, JsonArray selector, InlineOptimizerContext context) {
        JsonElement varElem = selector.get(0);

        if (varElem.isJsonPrimitive()) {
            String varName = varElem.getAsJsonPrimitive().getAsString();
            if (context.hasVar(varName)) {
                if (selector.size() == 1) {
                    return context.getVar(varName);
                } else {
                    JsonArray resultSelectorElements = new JsonArray();
//                    result.add(new JsonPrimitive(AstNodeType.SELECTOR));
                    resultSelectorElements.add(context.getVar(varName));
                    Iterator<JsonElement> iter = selector.iterator();
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

//    private JsonArray inlineImport(JsonElement uri, InlineOptimizerContext context) {
//        return null;  //To change body of created methods use File | Settings | File Templates.
//    }

    private JsonArray inlineIf(int nodeType, JsonArray conditions, InlineOptimizerContext context) throws HistoneException {
        JsonArray conditionsOut = new JsonArray();

        context.saveState();
        for (JsonElement condition : conditions) {
            JsonElement expressionAst = condition.getAsJsonArray().get(0);
            JsonArray statementsAst = condition.getAsJsonArray().get(1).getAsJsonArray();

            expressionAst = inlineNode(expressionAst.getAsJsonArray(), context);
            statementsAst = inline(statementsAst, context);

            JsonArray conditionOut = new JsonArray();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restoreState();

        return AstNodeFactory.createNode(nodeType, conditionsOut);
    }

    private JsonArray inlineFor(int nodeType, JsonArray iterator, JsonArray collection, JsonArray statements, InlineOptimizerContext context) throws HistoneException {
        JsonArray statementsForAst = statements.getAsJsonArray().get(0).getAsJsonArray();
        JsonArray statementsElseAst = statements.getAsJsonArray().size() > 1 ? statements.getAsJsonArray().get(1).getAsJsonArray() : null;

        JsonElement collectionAst = inlineNode(collection, context);
        statementsForAst = inline(statementsForAst, context);
        statementsElseAst = (statementsElseAst != null) ? inline(statementsElseAst, context) : null;

        JsonArray statementsArr = new JsonArray();

        statementsArr.add(statementsForAst);
        if (statementsElseAst != null) {
            statementsArr.add(statementsElseAst);
        }

        return AstNodeFactory.createNode(nodeType, iterator, collectionAst, statementsArr);

    }

    private JsonArray inlineCall(int nodeType, JsonElement target, JsonElement nameElement, JsonElement args, InlineOptimizerContext context) throws HistoneException {
        if (!target.isJsonNull() || !isString(nameElement) || nodeType < 0) {
            return AstNodeFactory.createNode(nodeType, target, nameElement, args);
        }

        String macroName = nameElement.getAsJsonPrimitive().getAsString();

        if (macroName == null || macroName.length() == 0 || !context.hasMacro(macroName)) {
            return AstNodeFactory.createNode(nodeType, target, nameElement, args);
        }

        MacroFunc macro = context.getMacro(macroName);
        JsonArray macroArgsAst = macro.getArgs();
        JsonArray macroBodyAst = macro.getStatements();

        context.saveState();

        // prepare inline vars for defined macro argument
        for (int i = 0; i < macroArgsAst.size(); i++) {
            context.putVar(macroArgsAst.get(i).getAsString(), args.getAsJsonArray().get(i).getAsJsonArray());
        }

        // prepare inline vars for 'self' reserved word
        JsonArray argumentsAst = new JsonArray();
        argumentsAst.add(new JsonPrimitive("arguments"));
        //TODO:
//        argumentsAst.add(AstNodeFactory.createNode(AstNodeType.ARRAY, args));
//        JsonArray self = AstNodeFactory.createNode(AstNodeType.OBJECT, AstNodeFactory.createArray(argumentsAst));
//        context.putVar("self", self);

        // do inline optimization using prepared context
        macroBodyAst = inline(macroBodyAst, context);

        context.restoreState();

        return macroBodyAst;
    }

    private JsonArray inlineMacro(int nodeType, JsonPrimitive ident, JsonArray args, JsonArray statements, InlineOptimizerContext context) throws EvaluatorException {
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

//    private JsonArray inlineImport(int nodeType, JsonPrimitive ident, InlineOptimizerContext context) throws EvaluatorException {
//        String uri = ident.getAsString();
//
//        resourceLoaderManager.
//
//        return AstNodeFactory.createNode(nodeType, ident);
//    }

    private boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(JsonArray astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }

    private JsonArray makeElementUnsafe(JsonArray element) {
        boolean typeUpdated = false;
        JsonArray result = new JsonArray();
        for (JsonElement item : element) {
            if (typeUpdated) {
                result.add(item);
            } else {
                result.add(new JsonPrimitive(-item.getAsJsonPrimitive().getAsInt()));
                typeUpdated = true;
            }
        }
        return result;
    }

}
