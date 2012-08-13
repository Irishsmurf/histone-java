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
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.parser.AstNodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.HashSet;
import java.util.Set;

public class AstMarker {

    private AstOptimizer optimizer;


    public AstMarker(AstOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    public JsonArray mark(JsonArray ast) throws HistoneException {
        OptimizerContext context = new OptimizerContext();
        return markInternal(ast, context);
    }

    private JsonArray markInternal(JsonArray ast, OptimizerContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        for (JsonElement element : ast) {
            JsonElement node = markNode(element, context);
            result.add(node);
        }

        return result;
    }

//    private JsonArray markStatements(JsonArray statements, OptimizerContext context) throws HistoneException {
//        JsonArray result = new JsonArray();
//
//        result.add(markInternal(statements, context));
//
//        return result;
//
//    }

    private JsonElement markNode(JsonElement element, OptimizerContext context) throws HistoneException {
        if (isString(element)) {
            return element;
        }

        if (!element.isJsonArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return element;
        }

        JsonArray astArray = element.getAsJsonArray();

        int nodeType = getNodeType(astArray);
        switch (nodeType) {
            case AstNodeType.TRUE:
            case AstNodeType.FALSE:
            case AstNodeType.NULL:
            case AstNodeType.INT:
            case AstNodeType.DOUBLE:
            case AstNodeType.STRING:
                return element;

//            case AstNodeType.MAP:
            //TODO:
//            case AstNodeType.ARRAY:
//                return markArray(astArray.get(1).getAsJsonArray(), context);
//
//            case AstNodeType.OBJECT:
//                return markObject(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.ADD:
                return markBinaryOperation(AstNodeType.ADD, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.SUB:
                return markBinaryOperation(AstNodeType.SUB, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MUL:
                return markBinaryOperation(AstNodeType.MUL, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.DIV:
                return markBinaryOperation(AstNodeType.DIV, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MOD:
                return markBinaryOperation(AstNodeType.MOD, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.NEGATE:
                return markUnaryOperation(AstNodeType.NEGATE, astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.OR:
                return markBinaryOperation(AstNodeType.OR, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.AND:
                return markBinaryOperation(AstNodeType.AND, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT:
                return markUnaryOperation(AstNodeType.NOT, astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.EQUAL:
                return markBinaryOperation(AstNodeType.EQUAL, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT_EQUAL:
                return markBinaryOperation(AstNodeType.NOT_EQUAL, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.LESS_OR_EQUAL:
                return markBinaryOperation(AstNodeType.LESS_OR_EQUAL, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.LESS_THAN:
                return markBinaryOperation(AstNodeType.LESS_THAN, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return markBinaryOperation(AstNodeType.GREATER_OR_EQUAL, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_THAN:
                return markBinaryOperation(AstNodeType.GREATER_THAN, astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.TERNARY:
                return markTernary(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), (astArray.size() > 3) ? astArray.get(3).getAsJsonArray() : null, context);
            case AstNodeType.IF:
                return markIf(astArray.get(1).getAsJsonArray(), context);
            case AstNodeType.FOR:
                return markFor(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);

            case AstNodeType.STATEMENTS:
                return markStatements(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.VAR:
                return markVar(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.SELECTOR:
                return markSelector(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.CALL:
                return markCall(astArray.get(1), astArray.get(2), astArray.get(3), context);

//            case AstNodeType.IMPORT:
//                return markImport(astArray.get(1), context);
//
            case AstNodeType.MACRO:
                return markMacro(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);

            default:
                Histone.runtime_log_error("Unknown node type '{}', marking is skipped.", null, nodeType);
                return makeElementUnsafe(astArray);
        }
    }

    private JsonElement markStatements(JsonArray ast, OptimizerContext context) throws HistoneException {
        JsonArray markedStatements = markInternal(ast, context);
        boolean isSafe = true;
        for (JsonElement item : markedStatements) {
            if (item.isJsonArray() && getNodeType(item.getAsJsonArray()) < 0) {
                isSafe = false;
            }
        }
        return AstNodeFactory.createNode(isSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, markedStatements);
    }

    private JsonElement markVar(JsonPrimitive name, JsonArray expr, OptimizerContext context) throws HistoneException {
        String varName = name.getAsString();
        JsonArray exprMarked = markNode(expr, context).getAsJsonArray();

        JsonArray result = AstNodeFactory.createNode(AstNodeType.VAR, name, exprMarked);

        if (getNodeType(exprMarked) < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }


    private JsonElement markMacro(JsonPrimitive ident, JsonArray args, JsonArray statements, OptimizerContext context) throws HistoneException {
        JsonArray result = AstNodeFactory.createNode(AstNodeType.MACRO, ident, args, statements);

        context.save();
        context.addSafeVar("self");

        Set<String> argNames = new HashSet<String>();
        if (!args.isJsonNull()) {
            for (JsonElement arg : args) {
                argNames.add(arg.getAsString());
                context.addSafeVar(arg.getAsString());
            }
        }
        JsonArray statementsOpt = markInternal(statements, context).getAsJsonArray();
        context.restore();

        boolean isSafe = true;
        for (JsonElement st : statementsOpt) {
            if (st.isJsonArray() && getNodeType(st.getAsJsonArray()) < 0) {
                isSafe = false;
                break;
            }
        }

        if (isSafe) {
            context.addSafeMacro(ident.getAsString(), argNames);
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonArray markCall(JsonElement target, JsonElement name, JsonElement args, OptimizerContext context) throws HistoneException {
        if (!target.isJsonNull() || !isString(name)) {
            return makeElementUnsafe(AstNodeFactory.createNode(AstNodeType.CALL, target, name, args));
        }

        String macroName = name.getAsJsonPrimitive().getAsString();

        if (macroName == null || macroName.length() == 0) {
            return makeElementUnsafe(AstNodeFactory.createNode(AstNodeType.CALL, target, name, args));
        }

        boolean isSafe = context.isMacroSafe(macroName);

        JsonElement argsMarked = new JsonArray();
        if (!args.isJsonNull()) {
            for (JsonElement arg : args.getAsJsonArray()) {
                JsonArray argMarked = null;
                if (getNodeType(arg.getAsJsonArray()) == AstNodeType.STATEMENTS) {
                    boolean isArgSafe = true;
                    JsonArray argStatements = new JsonArray();
                    for (JsonElement stItem : arg.getAsJsonArray().get(1).getAsJsonArray()) {
                        JsonElement stItemMarked = markNode(stItem, context);
                        if (stItemMarked.isJsonArray() && getNodeType(stItemMarked.getAsJsonArray()) < 0) {
                            isArgSafe = false;
                        }
                        argStatements.add(stItemMarked);
                    }
                    argMarked = AstNodeFactory.createNode(isArgSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, argStatements);
                } else {
                    argMarked = markNode(arg, context).getAsJsonArray();
                }

                if (getNodeType(argMarked) < 0) {
                    isSafe = false;
                }
                argsMarked.getAsJsonArray().add(argMarked);
            }
        } else {
            argsMarked = JsonNull.INSTANCE;
        }

        JsonArray result = AstNodeFactory.createNode(AstNodeType.CALL, target, name, argsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markSelector(JsonArray selector, OptimizerContext context) throws HistoneException {
        boolean isSafe = false;
//        boolean isForInline = false;

        JsonElement varElement = selector.get(0);
        if (varElement.isJsonPrimitive()) {
            String varName = varElement.getAsJsonPrimitive().getAsString();
            isSafe = context.isVarSafe(varName) || "global".equals(varName) || "this".equals(varName) || "self".equals(varName);
//            isForInline = "global".equals(varName);
        } else {
            JsonArray varElemMarked = markNode(varElement, context).getAsJsonArray();
            isSafe = getNodeType(varElemMarked) > 0;
        }

        JsonArray result = AstNodeFactory.createNode(AstNodeType.SELECTOR, selector);

        if (isSafe) {
//            if (isForInline) {
//                return AstNodeFactory.createNode(AstNodeType.INLINE, result);
//            } else {
//                return result;
//            }
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markFor(JsonArray vars, JsonArray collection, JsonArray statements, OptimizerContext context) throws HistoneException {
        JsonArray collectionOpt = markNode(collection, context).getAsJsonArray();

        String iterVal = vars.get(0).getAsString();
        String iterKey = (vars.size() > 1) ? vars.get(1).getAsString() : null;

        context.save();
        context.addSafeVar("self");
        context.addSafeVar(iterVal);
        if (iterKey != null) {
            context.addSafeVar(iterKey);
        }
        JsonArray statementsOpt = markInternal(statements.get(0).getAsJsonArray(), context).getAsJsonArray();
        JsonArray statementsElseOpt = statements.size() > 1 ? markInternal(statements.get(1).getAsJsonArray(), context).getAsJsonArray() : null;
        context.restore();

        boolean statementsHasUnsafeNode = false;
        for (JsonElement st : statementsOpt) {
            if (st.isJsonArray() && getNodeType(st.getAsJsonArray()) < 0) {
                statementsHasUnsafeNode = true;
                break;
            }
        }
        boolean isNotSafe = getNodeType(collectionOpt) < 0 || statementsHasUnsafeNode;

        JsonArray statementsMarked = AstNodeFactory.createArray(statementsOpt);
        if (statementsElseOpt != null) {
            statementsMarked.add(statementsElseOpt);
        }
        JsonArray result = AstNodeFactory.createNode(AstNodeType.FOR, vars, collectionOpt, statementsMarked);

        if (isNotSafe) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }

    }

    private JsonElement markIf(JsonArray conditions, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;
        JsonArray conditionsOut = new JsonArray();

        context.save();
        for (JsonElement condition : conditions) {
            JsonArray expressionAst = markNode(condition.getAsJsonArray().get(0).getAsJsonArray(), context).getAsJsonArray();
            if (getNodeType(expressionAst) < 0) {
                isSafe = false;
            }
            JsonArray statementsAst = markInternal(condition.getAsJsonArray().get(1).getAsJsonArray(), context);

            for (JsonElement st : statementsAst) {
                if (st.isJsonArray() && getNodeType(st.getAsJsonArray()) < 0) {
                    isSafe = false;
                }
            }

            JsonArray conditionOut = new JsonArray();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restore();

        JsonArray result = AstNodeFactory.createNode(AstNodeType.IF, conditionsOut);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markArray(JsonArray elements, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;

        JsonArray elementsMarked = new JsonArray();
        for (JsonElement element : elements) {
            element = markNode(element, context);
            if (getNodeType(element.getAsJsonArray()) < 0) {
                isSafe = false;
            }
            elementsMarked.add(element);
        }

        //TODO: check array->map
        JsonArray result = AstNodeFactory.createNode(AstNodeType.MAP, elementsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markObject(JsonArray elements, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;

        JsonArray elementsMarked = new JsonArray();
        for (JsonElement element : elements) {
            JsonElement elementKey = element.getAsJsonArray().get(0);
            JsonElement elementVal = markNode(element.getAsJsonArray().get(1), context);
            if (getNodeType(elementVal.getAsJsonArray()) < 0) {
                isSafe = false;
            }

            JsonArray elementOut = new JsonArray();
            elementOut.add(elementKey);
            elementOut.add(elementVal);
            elementsMarked.add(elementOut);
        }

        //TODO: check array->map
        JsonArray result = AstNodeFactory.createNode(AstNodeType.MAP, elementsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markUnaryOperation(int type, JsonArray arg, OptimizerContext context) throws HistoneException {
        JsonArray argOpt = markNode(arg, context).getAsJsonArray();

        int argType = getNodeType(argOpt);

        JsonArray result = AstNodeFactory.createNode(type, argOpt);

        if (argType > 0) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonElement markBinaryOperation(int type, JsonArray left, JsonArray right, OptimizerContext context) throws HistoneException {
        JsonArray leftOpt = markNode(left, context).getAsJsonArray();
        JsonArray rightOpt = markNode(right, context).getAsJsonArray();

        int leftType = getNodeType(leftOpt);
        int rightType = getNodeType(rightOpt);

        JsonArray result = AstNodeFactory.createNode(type, leftOpt, rightOpt);

        if (leftType < 0 || rightType < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }

    private JsonElement markTernary(JsonArray expr, JsonArray trueAst, JsonArray falseAst, OptimizerContext context) throws HistoneException {
        JsonArray exprMarked = markNode(expr, context).getAsJsonArray();
        JsonArray trueMarked = markNode(trueAst, context).getAsJsonArray();
        JsonArray falseMarked = falseAst != null ? markNode(falseAst, context).getAsJsonArray() : null;

        int exprType = getNodeType(exprMarked);
        int trueType = getNodeType(trueMarked);
        int falseType = falseAst != null ? getNodeType(falseMarked) : 0;

        JsonArray result = AstNodeFactory.createNode(AstNodeType.TERNARY, exprMarked, trueMarked, falseAst != null ? falseMarked : null);

        if (exprType < 0 || trueType < 0 || falseType < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }


    private JsonArray makeElementUnsafe(JsonArray element) {
        boolean typeUpdated = false;
        JsonArray result = new JsonArray();
        for (JsonElement item : element) {
            if (typeUpdated) {
                result.add(item);
            } else {
                int nodeType = item.getAsJsonPrimitive().getAsInt();
                result.add(new JsonPrimitive(nodeType > 0 ? -nodeType : nodeType));
                typeUpdated = true;
            }
        }
        return result;
    }

    private boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(JsonArray astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }

}
