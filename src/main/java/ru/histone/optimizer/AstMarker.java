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
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sazonovkirill@gmail.com
 */
public class AstMarker {
    private NodeFactory nodeFactory;

    public ArrayNode mark(ArrayNode ast) throws HistoneException {
        if (ast == null) throw new IllegalArgumentException();

        OptimizerContext context = new OptimizerContext();
        return markInternal(ast, context);
    }

    private ArrayNode markInternal(ArrayNode ast, OptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        if (ast.size() == 2 &&
                ast.get(0).isArray() &&
                ast.get(1).isArray() &&
                "HISTONE".equals(ast.get(0).get(0).asText())) {
            ast = (ArrayNode) ast.get(1);
        }

        for (JsonNode element : ast) {
            JsonNode node = markNode(element, context);
            result.add(node);
        }

        return result;
    }


    private JsonNode markNode(JsonNode element, OptimizerContext context) throws HistoneException {
        // All text nodes are returned 'as it'
        if (isString(element)) {
            return element;
        }

        // We expect text nodes or array nodes. Any other are returned 'as it'
        if (!element.isArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return element;
        }

        ArrayNode astArray = (ArrayNode) element;

        int nodeType = getNodeType(astArray);
        switch (nodeType) {
            // Constants are always safe
            case AstNodeType.TRUE:
            case AstNodeType.FALSE:
            case AstNodeType.NULL:
            case AstNodeType.INT:
            case AstNodeType.DOUBLE:
            case AstNodeType.STRING:
                return element;

            case AstNodeType.MAP:
                // TODO: Map safe check is not implemented. If you have time, implement it!
                // Now we consider all maps to be un safe.
                return makeElementUnsafe(astArray);

            case AstNodeType.IMPORT:
                // TODO: Import safe check is not implemented. If you have time, implement it!
                // Now we consider all imports to be un safe.
                return makeElementUnsafe(astArray);


            case AstNodeType.ADD:
                return markBinaryOperation(AstNodeType.ADD, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.SUB:
                return markBinaryOperation(AstNodeType.SUB, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.MUL:
                return markBinaryOperation(AstNodeType.MUL, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.DIV:
                return markBinaryOperation(AstNodeType.DIV, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.MOD:
                return markBinaryOperation(AstNodeType.MOD, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);

            case AstNodeType.NEGATE:
                return markUnaryOperation(AstNodeType.NEGATE, (ArrayNode) astArray.get(1), context);

            case AstNodeType.OR:
                return markBinaryOperation(AstNodeType.OR, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.AND:
                return markBinaryOperation(AstNodeType.AND, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.NOT:
                return markUnaryOperation(AstNodeType.NOT, (ArrayNode) astArray.get(1), context);

            case AstNodeType.EQUAL:
                return markBinaryOperation(AstNodeType.EQUAL, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.NOT_EQUAL:
                return markBinaryOperation(AstNodeType.NOT_EQUAL, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);

            case AstNodeType.LESS_OR_EQUAL:
                return markBinaryOperation(AstNodeType.LESS_OR_EQUAL, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.LESS_THAN:
                return markBinaryOperation(AstNodeType.LESS_THAN, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return markBinaryOperation(AstNodeType.GREATER_OR_EQUAL, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.GREATER_THAN:
                return markBinaryOperation(AstNodeType.GREATER_THAN, (ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.TERNARY:
                return markTernary((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), (astArray.size() > 3) ? (ArrayNode) astArray.get(3) : null, context);

            case AstNodeType.IF:
                return markIf((ArrayNode) astArray.get(1), context);

            case AstNodeType.FOR:
                return markFor((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), (ArrayNode) astArray.get(3), context);

            case AstNodeType.STATEMENTS:
                return markStatements((ArrayNode) astArray.get(1), context);

            case AstNodeType.VAR:
                return markVar(astArray.get(1), (ArrayNode) astArray.get(2), context);

            case AstNodeType.SELECTOR:
                return markSelector((ArrayNode) astArray.get(1), context);

            case AstNodeType.CALL:
                return markCall(astArray.get(1), astArray.get(2), astArray.get(3), context);

            case AstNodeType.MACRO:
                return markMacro(astArray.get(1), (ArrayNode) astArray.get(2), (ArrayNode) astArray.get(3), context);

            default:
                Histone.runtime_log_error("Unknown node type '{}', marking is skipped.", null, nodeType);
                return makeElementUnsafe(astArray);
        }
    }

    /**
     * Statements are safe, if all of them are safe.
     */
    private JsonNode markStatements(ArrayNode ast, OptimizerContext context) throws HistoneException {
        ArrayNode markedStatements = markInternal(ast, context);
        boolean isSafe = true;
        for (JsonNode item : markedStatements) {
            if (item.isArray() && getNodeType((ArrayNode) item) < 0) {
                isSafe = false;
            }
        }
        return nodeFactory.jsonArray(isSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, markedStatements);
    }

    /**
     * Var is safe, if its expression is safe.
     */
    private JsonNode markVar(JsonNode name, ArrayNode expr, OptimizerContext context) throws HistoneException {
        ArrayNode exprMarked = (ArrayNode) markNode(expr, context);

        ArrayNode result = nodeFactory.jsonArray(AstNodeType.VAR, name, exprMarked);

        if (getNodeType(exprMarked) < 0) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }
    }

    /**
     * Macros is safe if it uses only its parameters and all of statements are safe.
     */
    private JsonNode markMacro(JsonNode ident, ArrayNode args, ArrayNode statements, OptimizerContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray(AstNodeType.MACRO, ident, args, statements);

        context.save();
        context.addSafeVar("self");

        Set<String> argNames = new HashSet<String>();
        if (!args.isNull()) {
            for (JsonNode arg : args) {
                argNames.add(arg.asText());
                context.addSafeVar(arg.asText());
            }
        }
        ArrayNode statementsOpt = markInternal(statements, context);
        context.restore();

        boolean isSafe = true;
        for (JsonNode st : statementsOpt) {
            if (st.isArray() && getNodeType((ArrayNode) st) < 0) {
                isSafe = false;
                break;
            }
        }

        if (isSafe) {
            context.addSafeMacro(ident.asText(), argNames);
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * CALL is safe if:
     * 1. It's macro call, not function of type call (so target block is null).
     * 2. Macro name is string (what else can it bee?
     * 3. Macros is safe in context.
     * 4. All args are safe.
     */
    private ArrayNode markCall(JsonNode target, JsonNode name, JsonNode args, OptimizerContext context) throws HistoneException {
        if (!target.isNull() || !isString(name)) {
            return makeElementUnsafe(nodeFactory.jsonArray(AstNodeType.CALL, target, name, args));
        }

        String macroName = name.asText();

        if (macroName == null || macroName.length() == 0) {
            return makeElementUnsafe(nodeFactory.jsonArray(AstNodeType.CALL, target, name, args));
        }

        boolean isSafe = context.isMacroSafe(macroName);

        JsonNode argsMarked = nodeFactory.jsonArray();
        if (!args.isNull()) {
            for (JsonNode arg : args) {
                ArrayNode argMarked = null;
                if (getNodeType((ArrayNode) arg) == AstNodeType.STATEMENTS) {
                    boolean isArgSafe = true;
                    ArrayNode argStatements = nodeFactory.jsonArray();
                    for (JsonNode stItem : arg.get(1)) {
                        JsonNode stItemMarked = markNode(stItem, context);
                        if (stItemMarked.isArray() && getNodeType((ArrayNode) stItemMarked) < 0) {
                            isArgSafe = false;
                        }
                        argStatements.add(stItemMarked);
                    }
                    argMarked = nodeFactory.jsonArray(isArgSafe ? AstNodeType.STATEMENTS : -AstNodeType.STATEMENTS, argStatements);
                } else {
                    argMarked = (ArrayNode) markNode(arg, context);
                }

                if (getNodeType(argMarked) < 0) {
                    isSafe = false;
                }
                ((ArrayNode) argsMarked).add(argMarked);
            }
        } else {
            argsMarked = nodeFactory.jsonNull();
        }

        ArrayNode result = nodeFactory.jsonArray(AstNodeType.CALL, target, name, argsMarked);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * Selector is safe if variable is safe or all children elements are safe.
     */
    private JsonNode markSelector(ArrayNode selector, OptimizerContext context) throws HistoneException {
        boolean isSafe = false;

        JsonNode varElement = selector.get(0);
        if (varElement.isTextual()) {
            String varName = varElement.asText();
            isSafe = context.isVarSafe(varName) || "global".equals(varName) || "this".equals(varName) || "self".equals(varName);
        } else {
            ArrayNode varElemMarked = (ArrayNode) markNode(varElement, context);
            isSafe = getNodeType(varElemMarked) > 0;
        }

        ArrayNode result = nodeFactory.jsonArray(AstNodeType.SELECTOR, selector);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * FOR is safe if all its statements are safe and collection, for iterarated over, is safe also.
     */
    private JsonNode markFor(ArrayNode vars, ArrayNode collection, ArrayNode statements, OptimizerContext context) throws HistoneException {
        ArrayNode collectionOpt = (ArrayNode) markNode(collection, context);

        String iterVal = vars.get(0).asText();
        String iterKey = (vars.size() > 1) ? vars.get(1).asText() : null;

        context.save();
        context.addSafeVar("self");
        context.addSafeVar(iterVal);
        if (iterKey != null) {
            context.addSafeVar(iterKey);
        }
        ArrayNode statementsOpt = markInternal((ArrayNode) statements.get(0), context);
        ArrayNode statementsElseOpt = statements.size() > 1 ? markInternal((ArrayNode) statements.get(1), context) : null;
        context.restore();

        boolean statementsHasUnsafeNode = false;
        for (JsonNode st : statementsOpt) {
            if (st.isArray() && getNodeType((ArrayNode) st) < 0) {
                statementsHasUnsafeNode = true;
                break;
            }
        }
        boolean isNotSafe = getNodeType(collectionOpt) < 0 || statementsHasUnsafeNode;

        ArrayNode statementsMarked = nodeFactory.jsonArray(statementsOpt);
        if (statementsElseOpt != null) {
            statementsMarked.add(statementsElseOpt);
        }
        ArrayNode result = nodeFactory.jsonArray(AstNodeType.FOR, vars, collectionOpt, statementsMarked);

        if (isNotSafe) {
            return makeElementUnsafe(result);
        } else {
            return result;
        }

    }

    /**
     * If is safe if for all conditions both expression and statements are safe.
     */
    private JsonNode markIf(ArrayNode conditions, OptimizerContext context) throws HistoneException {
        boolean isSafe = true;
        ArrayNode conditionsOut = nodeFactory.jsonArray();

        context.save();
        for (JsonNode condition : conditions) {
            JsonNode expressionAst = markNode(condition.get(0), context);
            if (getNodeType((ArrayNode) expressionAst) < 0) {
                isSafe = false;
            }

            ArrayNode statementsAst = markInternal((ArrayNode) condition.get(1), context);
            for (JsonNode st : statementsAst) {
                if (st.isArray() && getNodeType((ArrayNode) st) < 0) {
                    isSafe = false;
                }
            }

            ArrayNode conditionOut = nodeFactory.jsonArray();
            conditionOut.add(expressionAst);
            conditionOut.add(statementsAst);
            conditionsOut.add(conditionOut);
        }
        context.restore();

        ArrayNode result = nodeFactory.jsonArray(AstNodeType.IF, conditionsOut);

        if (isSafe) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * Unary operation is safe, if its operand is safe.
     */
    private JsonNode markUnaryOperation(int type, ArrayNode arg, OptimizerContext context) throws HistoneException {
        ArrayNode argOpt = (ArrayNode) markNode(arg, context);

        int argType = getNodeType(argOpt);

        ArrayNode result = nodeFactory.jsonArray(type, argOpt);

        if (argType > 0) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * Binary operation is safe only if both operands are safe.
     * =
     */
    private JsonNode markBinaryOperation(int type, ArrayNode left, ArrayNode right, OptimizerContext context) throws HistoneException {
        ArrayNode leftOpt = (ArrayNode) markNode(left, context);
        ArrayNode rightOpt = (ArrayNode) markNode(right, context);

        int leftType = getNodeType(leftOpt);
        int rightType = getNodeType(rightOpt);

        ArrayNode result = nodeFactory.jsonArray(type, leftOpt, rightOpt);

        if (leftType > 0 && rightType > 0) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    /**
     * Ternary operation is safe (you'll never guess), only if all arguments are safe.
     */
    private JsonNode markTernary(ArrayNode expr, ArrayNode trueAst, ArrayNode falseAst, OptimizerContext context) throws HistoneException {
        ArrayNode exprMarked = (ArrayNode) markNode(expr, context);
        ArrayNode trueMarked = (ArrayNode) markNode(trueAst, context);
        ArrayNode falseMarked = falseAst != null ? (ArrayNode) markNode(falseAst, context) : null;

        int exprType = getNodeType(exprMarked);
        int trueType = getNodeType(trueMarked);
        int falseType = falseAst != null ? getNodeType(falseMarked) : 0;

        ArrayNode result = nodeFactory.jsonArray(AstNodeType.TERNARY, exprMarked, trueMarked, falseAst != null ? falseMarked : null);

        if (exprType > 0 && trueType > 0 && falseType > 0) {
            return result;
        } else {
            return makeElementUnsafe(result);
        }
    }

    private ArrayNode makeElementUnsafe(ArrayNode element) {
        // Alternative implementation
        int nodeType = element.get(0).asInt();
        if (nodeType > 0) nodeType = -nodeType;
        element.set(0, IntNode.valueOf(nodeType));
        return element;
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
