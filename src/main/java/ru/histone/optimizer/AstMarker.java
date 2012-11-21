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

public class AstMarker {

    private AstOptimizer optimizer;


    public AstMarker(AstOptimizer optimizer) {
        this.optimizer = optimizer;
    }
/*
    public ArrayNode mark(ArrayNode ast) throws HistoneException {
        OptimizerContext context = new OptimizerContext();
        return markInternal(ast, context);
    }

    private ArrayNode markInternal(ArrayNode ast, OptimizerContext context) throws HistoneException {
        ArrayNode result = new ArrayNode();

        for (JsonNode element : ast) {
            JsonNode node = markNode(element, context);
            result.add(node);
        }

        return result;
    }

//    private ArrayNode markStatements(ArrayNode statements, OptimizerContext context) throws HistoneException {
//        ArrayNode result = new ArrayNode();
//
//        result.add(markInternal(statements, context));
//
//        return result;
//
//    }

    private JsonNode markNode(JsonNode element, OptimizerContext context) throws HistoneException {
        if (isString(element)) {
            return element;
        }

        if (!element.isArrayNode()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return element;
        }

        ArrayNode astArray = element.getAsArrayNode();

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
//                return markArray(astArray.get(1).getAsArrayNode(), context);
//
//            case AstNodeType.OBJECT:
//                return markObject(astArray.get(1).getAsArrayNode(), context);

            case AstNodeType.ADD:
                return markBinaryOperation(AstNodeType.ADD, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.SUB:
                return markBinaryOperation(AstNodeType.SUB, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.MUL:
                return markBinaryOperation(AstNodeType.MUL, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.DIV:
                return markBinaryOperation(AstNodeType.DIV, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.MOD:
                return markBinaryOperation(AstNodeType.MOD, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);

            case AstNodeType.NEGATE:
                return markUnaryOperation(AstNodeType.NEGATE, astArray.get(1).getAsArrayNode(), context);

            case AstNodeType.OR:
                return markBinaryOperation(AstNodeType.OR, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.AND:
                return markBinaryOperation(AstNodeType.AND, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.NOT:
                return markUnaryOperation(AstNodeType.NOT, astArray.get(1).getAsArrayNode(), context);

            case AstNodeType.EQUAL:
                return markBinaryOperation(AstNodeType.EQUAL, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.NOT_EQUAL:
                return markBinaryOperation(AstNodeType.NOT_EQUAL, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);

            case AstNodeType.LESS_OR_EQUAL:
                return markBinaryOperation(AstNodeType.LESS_OR_EQUAL, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.LESS_THAN:
                return markBinaryOperation(AstNodeType.LESS_THAN, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return markBinaryOperation(AstNodeType.GREATER_OR_EQUAL, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.GREATER_THAN:
                return markBinaryOperation(AstNodeType.GREATER_THAN, astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), context);
            case AstNodeType.TERNARY:
                return markTernary(astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), (astArray.size() > 3) ? astArray.get(3).getAsArrayNode() : null, context);
            case AstNodeType.IF:
                return markIf(astArray.get(1).getAsArrayNode(), context);
            case AstNodeType.FOR:
                return markFor(astArray.get(1).getAsArrayNode(), astArray.get(2).getAsArrayNode(), astArray.get(3).getAsArrayNode(), context);

            case AstNodeType.STATEMENTS:
                return markStatements(astArray.get(1).getAsArrayNode(), context);

            case AstNodeType.VAR:
                return markVar(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsArrayNode(), context);

            case AstNodeType.SELECTOR:
                return markSelector(astArray.get(1).getAsArrayNode(), context);

            case AstNodeType.CALL:
                return markCall(astArray.get(1), astArray.get(2), astArray.get(3), context);

//            case AstNodeType.IMPORT:
//                return markImport(astArray.get(1), context);
//
            case AstNodeType.MACRO:
                return markMacro(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsArrayNode(), astArray.get(3).getAsArrayNode(), context);

            default:
                Histone.runtime_log_error("Unknown node type '{}', marking is skipped.", null, nodeType);
                return makeElementUnsafe(astArray);
        }
    }

    private JsonNode markStatements(ArrayNode ast, OptimizerContext context) throws HistoneException {
        ArrayNode markedStatements = markInternal(ast, context);
        boolean isSafe = true;
        for (JsonNode item : markedStatements) {
            if (item.isArrayNode() && getNodeType(item.getAsArrayNode()) < 0) {
                isSafe = false;
            }
        }
        return AstNodeFactory.createNode(isSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, markedStatements);
    }

    private JsonNode markVar(JsonPrimitive name, ArrayNode expr, OptimizerContext context) throws HistoneException {
        String varName = name.getAsString();
        ArrayNode exprMarked = markNode(expr, context).getAsArrayNode();

        ArrayNode result = AstNodeFactory.createNode(AstNodeType.VAR, name, exprMarked);

        if (getNodeType(exprMarked) < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }


    private JsonNode markMacro(JsonPrimitive ident, ArrayNode args, ArrayNode statements, OptimizerContext context) throws HistoneException {
        ArrayNode result = AstNodeFactory.createNode(AstNodeType.MACRO, ident, args, statements);

        context.save();
        context.addSafeVar("self");

        Set<String> argNames = new HashSet<String>();
        if (!args.isJsonNull()) {
            for (JsonNode arg : args) {
                argNames.add(arg.getAsString());
                context.addSafeVar(arg.getAsString());
            }
        }
        ArrayNode statementsOpt = markInternal(statements, context).getAsArrayNode();
        context.restore();

        boolean isSafe = true;
        for (JsonNode st : statementsOpt) {
            if (st.isArrayNode() && getNodeType(st.getAsArrayNode()) < 0) {
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

    private ArrayNode markCall(JsonNode target, JsonNode name, JsonNode args, OptimizerContext context) throws HistoneException {
        if (!target.isJsonNull() || !isString(name)) {
            return makeElementUnsafe(AstNodeFactory.createNode(AstNodeType.CALL, target, name, args));
        }

        String macroName = name.getAsJsonPrimitive().getAsString();

        if (macroName == null || macroName.length() == 0) {
            return makeElementUnsafe(AstNodeFactory.createNode(AstNodeType.CALL, target, name, args));
        }

        boolean isSafe = context.isMacroSafe(macroName);

        JsonNode argsMarked = new ArrayNode();
        if (!args.isJsonNull()) {
            for (JsonNode arg : args.getAsArrayNode()) {
                ArrayNode argMarked = null;
                if (getNodeType(arg.getAsArrayNode()) == AstNodeType.STATEMENTS) {
                    boolean isArgSafe = true;
                    ArrayNode argStatements = new ArrayNode();
                    for (JsonNode stItem : arg.getAsArrayNode().get(1).getAsArrayNode()) {
                        JsonNode stItemMarked = markNode(stItem, context);
                        if (stItemMarked.isArrayNode() && getNodeType(stItemMarked.getAsArrayNode()) < 0) {
                            isArgSafe = false;
                        }
                        argStatements.add(stItemMarked);
                    }
                    argMarked = AstNodeFactory.createNode(isArgSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, argStatements);
                } else {
                    argMarked = markNode(arg, context).getAsArrayNode();
                }

                if (getNodeType(argMarked) < 0) {
                    isSafe = false;
                }
                argsMarked.getAsArrayNode().add(argMarked);
            }
        } else {
            argsMarked = JsonNull.INSTANCE;
        }

        ArrayNode result = AstNodeFactory.createNode(AstNodeType.CALL, target, name, argsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonNode markSelector(ArrayNode selector, OptimizerContext context) throws HistoneException {
        boolean isSafe = false;
//        boolean isForInline = false;

        JsonNode varElement = selector.get(0);
        if (varElement.isJsonPrimitive()) {
            String varName = varElement.getAsJsonPrimitive().getAsString();
            isSafe = context.isVarSafe(varName) || "global".equals(varName) || "this".equals(varName) || "self".equals(varName);
//            isForInline = "global".equals(varName);
        } else {
            ArrayNode varElemMarked = markNode(varElement, context).getAsArrayNode();
            isSafe = getNodeType(varElemMarked) > 0;
        }

        ArrayNode result = AstNodeFactory.createNode(AstNodeType.SELECTOR, selector);

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

    private JsonNode markFor(ArrayNode vars, ArrayNode collection, ArrayNode statements, OptimizerContext context) throws HistoneException {
        ArrayNode collectionOpt = markNode(collection, context).getAsArrayNode();

        String iterVal = vars.get(0).getAsString();
        String iterKey = (vars.size() > 1) ? vars.get(1).getAsString() : null;

        context.save();
        context.addSafeVar("self");
        context.addSafeVar(iterVal);
        if (iterKey != null) {
            context.addSafeVar(iterKey);
        }
        ArrayNode statementsOpt = markInternal(statements.get(0).getAsArrayNode(), context).getAsArrayNode();
        ArrayNode statementsElseOpt = statements.size() > 1 ? markInternal(statements.get(1).getAsArrayNode(), context).getAsArrayNode() : null;
        context.restore();

        boolean statementsHasUnsafeNode = false;
        for (JsonNode st : statementsOpt) {
            if (st.isArrayNode() && getNodeType(st.getAsArrayNode()) < 0) {
                statementsHasUnsafeNode = true;
                break;
            }
        }
        boolean isNotSafe = getNodeType(collectionOpt) < 0 || statementsHasUnsafeNode;

        ArrayNode statementsMarked = AstNodeFactory.createArray(statementsOpt);
        if (statementsElseOpt != null) {
            statementsMarked.add(statementsElseOpt);
        }
        ArrayNode result = AstNodeFactory.createNode(AstNodeType.FOR, vars, collectionOpt, statementsMarked);

        if (isNotSafe) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }

    }

    private JsonNode markIf(ArrayNode conditions, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;
        ArrayNode conditionsOut = new ArrayNode();

        context.save();
        for (JsonNode condition : conditions) {
            ArrayNode expressionAst = markNode(condition.getAsArrayNode().get(0).getAsArrayNode(), context).getAsArrayNode();
            if (getNodeType(expressionAst) < 0) {
                isSafe = false;
            }
            ArrayNode statementsAst = markInternal(condition.getAsArrayNode().get(1).getAsArrayNode(), context);

            for (JsonNode st : statementsAst) {
                if (st.isArrayNode() && getNodeType(st.getAsArrayNode()) < 0) {
                    isSafe = false;
                }
            }

            ArrayNode conditionOut = new ArrayNode();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restore();

        ArrayNode result = AstNodeFactory.createNode(AstNodeType.IF, conditionsOut);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonNode markArray(ArrayNode elements, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;

        ArrayNode elementsMarked = new ArrayNode();
        for (JsonNode element : elements) {
            element = markNode(element, context);
            if (getNodeType(element.getAsArrayNode()) < 0) {
                isSafe = false;
            }
            elementsMarked.add(element);
        }

        //TODO: check array->map
        ArrayNode result = AstNodeFactory.createNode(AstNodeType.MAP, elementsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonNode markObject(ArrayNode elements, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;

        ArrayNode elementsMarked = new ArrayNode();
        for (JsonNode element : elements) {
            JsonNode elementKey = element.getAsArrayNode().get(0);
            JsonNode elementVal = markNode(element.getAsArrayNode().get(1), context);
            if (getNodeType(elementVal.getAsArrayNode()) < 0) {
                isSafe = false;
            }

            ArrayNode elementOut = new ArrayNode();
            elementOut.add(elementKey);
            elementOut.add(elementVal);
            elementsMarked.add(elementOut);
        }

        //TODO: check array->map
        ArrayNode result = AstNodeFactory.createNode(AstNodeType.MAP, elementsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonNode markUnaryOperation(int type, ArrayNode arg, OptimizerContext context) throws HistoneException {
        ArrayNode argOpt = markNode(arg, context).getAsArrayNode();

        int argType = getNodeType(argOpt);

        ArrayNode result = AstNodeFactory.createNode(type, argOpt);

        if (argType > 0) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private JsonNode markBinaryOperation(int type, ArrayNode left, ArrayNode right, OptimizerContext context) throws HistoneException {
        ArrayNode leftOpt = markNode(left, context).getAsArrayNode();
        ArrayNode rightOpt = markNode(right, context).getAsArrayNode();

        int leftType = getNodeType(leftOpt);
        int rightType = getNodeType(rightOpt);

        ArrayNode result = AstNodeFactory.createNode(type, leftOpt, rightOpt);

        if (leftType < 0 || rightType < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }

    private JsonNode markTernary(ArrayNode expr, ArrayNode trueAst, ArrayNode falseAst, OptimizerContext context) throws HistoneException {
        ArrayNode exprMarked = markNode(expr, context).getAsArrayNode();
        ArrayNode trueMarked = markNode(trueAst, context).getAsArrayNode();
        ArrayNode falseMarked = falseAst != null ? markNode(falseAst, context).getAsArrayNode() : null;

        int exprType = getNodeType(exprMarked);
        int trueType = getNodeType(trueMarked);
        int falseType = falseAst != null ? getNodeType(falseMarked) : 0;

        ArrayNode result = AstNodeFactory.createNode(AstNodeType.TERNARY, exprMarked, trueMarked, falseAst != null ? falseMarked : null);

        if (exprType < 0 || trueType < 0 || falseType < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }


    private ArrayNode makeElementUnsafe(ArrayNode element) {
        boolean typeUpdated = false;
        ArrayNode result = new ArrayNode();
        for (JsonNode item : element) {
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

    private boolean isString(JsonNode element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }
        */
}
