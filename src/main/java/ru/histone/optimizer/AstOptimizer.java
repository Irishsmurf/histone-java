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
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.EvaluatorContext;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.MacroFunc;
import ru.histone.evaluator.nodes.*;
import ru.histone.parser.AstNodeFactory;
import ru.histone.parser.AstNodeType;

public class AstOptimizer {
    private final Evaluator evaluator;

    public AstOptimizer(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public JsonArray optimize(JsonArray ast) throws HistoneException {
        JsonArray result = new JsonArray();

        EvaluatorContext context = EvaluatorContext.createEmpty(new GlobalObjectNode());
        StringBuilder buf = new StringBuilder();
        for (JsonElement element : ast) {
            Node node = optimizeNode(element, context);
            if (node instanceof AstNode) {
                if (buf.length() > 0) {
                    result.add(new JsonPrimitive(buf.toString()));
                    buf.setLength(0);
                }
                JsonElement astValue = ((AstNode) node).getValue();
                if (astValue.isJsonArray() && isString(astValue.getAsJsonArray().get(0))) {
                    for (JsonElement elem : astValue.getAsJsonArray()) {
                        result.add(elem);
                    }
                } else {
                    result.add(astValue);
                }
            } else {
                buf.append(node.getAsString().getValue());
            }
        }
        if (buf.length() > 0) {
            result.add(new JsonPrimitive(buf.toString()));
        }

        return result;
    }

    private Node optimizeStatements(JsonElement statements, EvaluatorContext context) throws HistoneException {
        JsonArray result = new JsonArray();

        StringBuilder buf = new StringBuilder();
        for (JsonElement element : statements.getAsJsonArray()) {
            if (isString(element)) {
                buf.append(element.getAsString());
            } else {
                Node node = optimizeNode(element, context);
                if (node instanceof AstNode) {
                    if (buf.length() > 0) {
                        result.add(AstNodeFactory.createNode(AstNodeType.STRING, new JsonPrimitive(buf.toString())));
                        buf.setLength(0);
                    }
                    result.add(((AstNode) node).getValue());
                } else {
                    buf.append(node.getAsString().getValue());
                }
            }
        }
        if (buf.length() > 0) {
            result.add(AstNodeFactory.createNode(AstNodeType.STRING, new JsonPrimitive(buf.toString())));
        }

        return AstNode.create(result);
    }

    private Node optimizeNode(JsonElement element, EvaluatorContext context) throws HistoneException {
        if (isString(element)) {
            return StringNode.create(element.getAsString());
        }

        if (!element.isJsonArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return AstNode.create(element);
        }

        JsonArray astArray = element.getAsJsonArray();

        int nodeType = astArray.get(0).getAsJsonPrimitive().getAsInt();
        switch (nodeType) {
            case AstNodeType.TRUE:
                return Node.TRUE;

            case AstNodeType.FALSE:
                return Node.FALSE;

            case AstNodeType.NULL:
                return Node.NULL;

            case AstNodeType.INT:
                return NumberNode.create(astArray.get(1).getAsBigDecimal());

            case AstNodeType.DOUBLE:
                return NumberNode.create(astArray.get(1).getAsBigDecimal());

            case AstNodeType.STRING:
                return StringNode.create(astArray.get(1).getAsString());


//            case AstNodeType.MAP:
//            TODO:
//            case AstNodeType.ARRAY:
//                return optimizeArray(astArray.get(1).getAsJsonArray(), context);
//
//            case AstNodeType.OBJECT:
//                return optimizeObject(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.ADD:
                return optimizeAdd(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.SUB:
                return optimizeSub(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MUL:
                return optimizeMul(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.DIV:
                return optimizeDiv(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MOD:
                return optimizeMod(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.NEGATE:
                return optimizeNegate(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.OR:
                return optimizeOr(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.AND:
                return optimizeAnd(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT:
                return optimizeNot(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.EQUAL:
                return optimizeEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT_EQUAL:
                return optimizeNotEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.LESS_OR_EQUAL:
                return optimizeLessOrEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.LESS_THAN:
                return optimizeLessThan(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return optimizeGreaterOrEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_THAN:
                return optimizeGreaterThan(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.TERNARY:
                return optimizeTernary(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), (astArray.size() > 3) ? astArray.get(3).getAsJsonArray() : null, context);
            case -AstNodeType.IF:
                return optimizeIf(astArray.get(1).getAsJsonArray(), context);
            case -AstNodeType.FOR:
                return optimizeFor(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);
            case AstNodeType.FOR:
            case AstNodeType.IF:
            case AstNodeType.CALL:
            case AstNodeType.SELECTOR:
            case AstNodeType.STATEMENTS:
                return evaluate(astArray, context);

//
            case -AstNodeType.STATEMENTS:
                return optimizeStatements(astArray.get(1), context);
//
//            case AstNodeType.VAR:
//                return optimizeVar(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), context);
//
//            case AstNodeType.SELECTOR:
//                return optimizeSelector(astArray.get(1).getAsJsonArray(), context);
//
            case -AstNodeType.CALL:
                return optimizeCall(astArray.get(1), astArray.get(2), astArray.get(3), context);
//
//            case AstNodeType.IMPORT:
//                return optimizeImport(astArray.get(1), context);
//
            case AstNodeType.MACRO:
                return optimizeMacro(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);

            default:
                Histone.runtime_log_error("Optimization is unsupported for node type {}", null, nodeType);
                return AstNode.create(makeElementSafe(astArray));
        }
    }

    private Node evaluate(JsonArray astArray, EvaluatorContext context) throws HistoneException {
        return StringNode.create(evaluator.process(AstNodeFactory.createArray(astArray), context));
    }

    private Node optimizeCall(JsonElement target, JsonElement nameElement, JsonElement args, EvaluatorContext context) throws HistoneException {
        JsonArray argsOpt = new JsonArray();
        for (JsonElement arg : args.getAsJsonArray()) {
            Node node = optimizeNode(arg, context);
            if (node.isAst()) {
                argsOpt.add(makeElementSafe(((AstNode) node).getValue().getAsJsonArray()));
            } else {
                argsOpt.add(node2Ast(node));
            }
        }

        return AstNode.create(AstNodeFactory.createNode(AstNodeType.CALL, target, nameElement, argsOpt));
    }

    private Node optimizeMacro(JsonPrimitive ident, JsonArray args, JsonArray statements, EvaluatorContext context) throws EvaluatorException {
        String name = ident.getAsString();

        MacroFunc func = new MacroFunc();
        func.setArgs(args);
        func.setStatements(statements);
        context.putMacro(name, func);

        return AstNode.create(AstNodeFactory.createNode(AstNodeType.MACRO, ident, args, statements));
    }


    private Node optimizeAdd(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.ADD, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.ADD, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.ADD, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_add(right);
            }
        }
    }


    private Node optimizeSub(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.SUB, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.SUB, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.SUB, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_sub(right);
            }
        }
    }

    private Node optimizeMul(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MUL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MUL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MUL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_mul(right);
            }
        }
    }

    private Node optimizeDiv(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.DIV, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.DIV, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.DIV, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_div(right);
            }
        }
    }

    private Node optimizeMod(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MOD, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MOD, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.MOD, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_mod(right);
            }
        }
    }

    private Node optimizeNegate(JsonArray left, EvaluatorContext context) throws HistoneException {
        Node nodeLeft = optimizeNode(left, context);
        if (nodeLeft.isAst()) {
            return AstNode.create(AstNodeFactory.createNode(AstNodeType.NEGATE, left));
        } else {
            return nodeLeft.oper_negate();
        }
    }

    private Node optimizeOr(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.OR, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.OR, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.OR, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_or(right);
            }
        }
    }

    private Node optimizeAnd(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.AND, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.AND, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.AND, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_and(right);
            }
        }
    }

    private Node optimizeNot(JsonArray left, EvaluatorContext context) throws HistoneException {
        Node nodeLeft = optimizeNode(left, context);
        if (nodeLeft.isAst()) {
            return AstNode.create(AstNodeFactory.createNode(AstNodeType.NOT, left));
        } else {
            return nodeLeft.oper_not();
        }
    }

    private Node optimizeEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_equal(right);
            }
        }
    }

    private Node optimizeNotEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.NOT_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.NOT_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.NOT_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_notEqual(right);
            }
        }
    }

    private Node optimizeLessOrEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_OR_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_OR_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_OR_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_lessOrEqual(right);
            }
        }
    }

    private Node optimizeLessThan(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_THAN, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_THAN, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.LESS_THAN, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_lessThan(right);
            }
        }
    }

    private Node optimizeGreaterOrEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_OR_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_OR_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_OR_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_greaterOrEqual(right);
            }
        }
    }

    private Node optimizeGreaterThan(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_THAN, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_THAN, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(AstNodeFactory.createNode(AstNodeType.GREATER_THAN, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_greaterThan(right);
            }
        }
    }

//    private Node optimizeObject(JsonArray items, EvaluatorContext context) throws HistoneException {
//        boolean isSafe = true;
//
//        ObjectNode result = ObjectNode.create();
//        for (JsonElement item : items) {
//            JsonPrimitive key = item.getAsJsonArray().get(0).getAsJsonPrimitive();
//            JsonElement val = item.getAsJsonArray().get(1);
//            Node itemNode = optimizeNode(val, context);
//            if (itemNode instanceof AstNode) {
//                isSafe = false;
//                break;
//            }
//            result.put(key.getAsString(), itemNode);
//        }
//
//        if (isSafe) {
//            return result;
//        } else {
//            return AstNode.create(AstNodeFactory.createNode(AstNodeType.OBJECT, items));
//        }
//    }

    private Node optimizeTernary(JsonArray condition, JsonArray trueNode, JsonArray falseNode, EvaluatorContext context) throws HistoneException {
        Node optCondition = optimizeNode(condition, context);
        Node optTrueNode = optimizeNode(trueNode, context);
        Node optFalseNode = falseNode != null ? optimizeNode(falseNode, context) : Node.NULL;

        if (optCondition.isAst()) {
            Object argTrue = optTrueNode.isAst() ? trueNode : node2Ast(optTrueNode);
            Object argFalse = optFalseNode.isAst() ? falseNode : node2Ast(optFalseNode);

            return AstNode.create(AstNodeFactory.createNode(AstNodeType.TERNARY, condition, argTrue, argFalse));
        } else {
            if (optCondition.getAsBoolean().getValue()) {
                return optimizeNode(trueNode, context);
            } else if (falseNode != null) {
                return optimizeNode(falseNode, context);
            } else {
                return Node.UNDEFINED;
            }
        }
    }

    private Node optimizeIf(JsonArray conditions, EvaluatorContext context) throws HistoneException {
        JsonArray conditionsOut = new JsonArray();

        context.saveState();
        for (JsonElement condition : conditions) {
            JsonArray expressionAst = condition.getAsJsonArray().get(0).getAsJsonArray();
            Node expressionResult = optimizeNode(expressionAst, context);

            JsonArray statementsAst = condition.getAsJsonArray().get(1).getAsJsonArray();
            Node statementsResult = optimizeStatements(statementsAst.getAsJsonArray(), context);

            if (!expressionResult.isAst() && expressionResult.getAsBoolean().getValue()) {
                if (statementsResult.isAst()) {
                    return statementsResult;
                } else {
                    return statementsResult.getAsString();
                }
            } else {
                JsonArray conditionOut = new JsonArray();
                if (expressionResult.isAst()) {
                    conditionOut.add(expressionAst);

                    if (statementsResult.isAst()) {
                        conditionOut.add(statementsAst);
                    } else {
                        conditionOut.add(node2Ast(statementsResult));
                    }
                }

                // if have condition to add, then add it to resulting conditions array
                if (conditionOut.size() > 0) {
                    conditionsOut.add(conditionOut);
                }
            }
        }
        context.restoreState();

        return AstNode.create(AstNodeFactory.createNode(AstNodeType.IF, conditionsOut));
    }

    private Node optimizeFor(JsonArray iterator, JsonArray collection, JsonArray statements, EvaluatorContext context) throws HistoneException {
        Node collectionOpt = optimizeNode(collection, context);
        Node statementsForOpt = optimizeStatements(statements.getAsJsonArray().get(0), context);
        Node statementsElseOpt = statements.getAsJsonArray().size() > 1 ? optimizeStatements(statements.getAsJsonArray().get(1), context) : null;

        if (!collectionOpt.isAst()) { // we can't mark 'for' loop if loop collection can't be optimized to Node object (either array or object)
            String iterVal = iterator.get(0).getAsString();
            String iterKey = (iterator.size() > 1) ? iterator.get(1).getAsString() : null;

            // run evaluator with 'for' AST subtree
            JsonArray forAst = new JsonArray();
            forAst.add(AstNodeFactory.createNode(AstNodeType.FOR, iterator, collection, statements).getAsJsonArray());
            return StringNode.create(evaluator.process(forAst, context));
        }

        JsonArray statementsArr = new JsonArray();
        if (!statementsForOpt.isAst()) {
            statementsArr.add(node2Ast(statementsForOpt));
        } else {
            statementsArr.add(statements.getAsJsonArray().get(0));
        }

        if (statementsElseOpt != null) {
            if (!statementsElseOpt.isAst()) {
                statementsArr.add(node2Ast(statementsElseOpt));
            } else {
                statementsArr.add(statements.getAsJsonArray().get(1));
            }
        }

        return AstNode.create(AstNodeFactory.createNode(AstNodeType.FOR, iterator, ((AstNode) collectionOpt).getValue(), statementsArr));

    }

//    private boolean isSafe(JsonArray ast, String... varNames) {
//        Set<String> varNamesSet = new HashSet<String>();
//        for (String name : varNames) {
//            varNamesSet.add(name);
//        }
//
//        for (JsonElement item : ast) {
//            if (item.isJsonArray()) {
//                JsonArray asJsonArray = item.getAsJsonArray();
//                JsonElement firstElem = asJsonArray.get(0);
//                if (!isString(firstElem)) {
//                    switch (firstElem.getAsJsonPrimitive().getAsInt()) {
//                        case AstNodeType.SELECTOR:
//                            if (!varNamesSet.contains(asJsonArray.get(1).getAsJsonArray().get(0).getAsString())) {
//                                return false;
//                            }
//                            break;
//                        case AstNodeType.FOR:
//                        case AstNodeType.IF:
//                        case AstNodeType.CALL:
//                            return false;
//                        default:
//                            if (!isSafe(asJsonArray, varNames)) {
//                                return false;
//                            }
//                    }
//                }
//            }
//        }
//
//        return true;
//    }

    private JsonElement node2Ast(Node node) {
        if (node.isBoolean()) {
            return node.getAsBoolean().getValue() ? AstNodeFactory.createNode(AstNodeType.TRUE) : AstNodeFactory.createNode(AstNodeType.FALSE);
        } else if (node.isInteger()) {
            return AstNodeFactory.createNode(AstNodeType.INT, node.getAsNumber().getValue());
        } else if (node.isFloat()) {
            return AstNodeFactory.createNode(AstNodeType.DOUBLE, node.getAsNumber().getValue());
        } else if (node.isString()) {
            return AstNodeFactory.createNode(AstNodeType.STRING, node.getAsString().getValue());
        } else if (node.isNull()) {
            return AstNodeFactory.createNode(AstNodeType.NULL);
            //TODO: implement array->map
//        } else if (node.isArray()) {
//            JsonArray elems = new JsonArray();
//
//            for (Node elem : node.getAsArray().getElements()) {
//                elems.add(node2Ast(elem));
//            }
//
//            return AstNodeFactory.createNode(AstNodeType.ARRAY, elems);
//        } else if (node.isObject()) {
//            JsonArray props = new JsonArray();
//
//            for (Map.Entry<String, Node> entry : node.getAsObject().entries()) {
//                JsonArray prop = new JsonArray();
//                prop.add(new JsonPrimitive(entry.getKey()));
//                prop.add(node2Ast(entry.getValue()));
//                props.add(prop);
//            }
//
//            return AstNodeFactory.createNode(AstNodeType.OBJECT, props);
        }
        throw new IllegalStateException(String.format("Can't convert node %s to AST element", node));
    }


    private boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private int getNodeType(JsonArray astArray) {
        return astArray.get(0).getAsJsonPrimitive().getAsInt();
    }

    private JsonArray makeElementSafe(JsonArray element) throws HistoneException {
        boolean typeUpdated = false;
        JsonArray result = new JsonArray();
        for (JsonElement item : element) {
            if (typeUpdated) {
                if (item.isJsonArray() && item.getAsJsonArray().get(0).isJsonPrimitive() && item.getAsJsonArray().get(0).getAsJsonPrimitive().isNumber()) {
                    result.add(makeElementSafe(item.getAsJsonArray()));
                } else {
                    result.add(item);
                }
//                result.add(item);
            } else {
                result.add(new JsonPrimitive(Math.abs(item.getAsJsonPrimitive().getAsInt())));
                typeUpdated = true;
            }
        }
        return result;
    }

}
