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
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.EvaluatorContext;
import ru.histone.evaluator.EvaluatorException;
import ru.histone.evaluator.MacroFunc;
import ru.histone.evaluator.nodes.AstNode;
import ru.histone.evaluator.nodes.GlobalObjectNode;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.parser.AstNodeType;

import java.math.BigDecimal;

public class AstOptimizer {
    private final Evaluator evaluator;
    private NodeFactory nodeFactory = new NodeFactory(new ObjectMapper());

    public AstOptimizer(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public ArrayNode optimize(ArrayNode ast) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        EvaluatorContext context = EvaluatorContext.createEmpty(nodeFactory, new GlobalObjectNode(nodeFactory));
        StringBuilder buf = new StringBuilder();
        for (JsonNode element : ast) {
            Node node = optimizeNode(element, context);
            if (node instanceof AstNode) {
                if (buf.length() > 0) {
                    result.add(nodeFactory.jsonString(buf.toString()));
                    buf.setLength(0);
                }
                JsonNode astValue = ((AstNode) node).getValue();
                if (astValue.isArray() && isString(astValue.get(0))) {
                    for (JsonNode elem : astValue) {
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
            result.add(nodeFactory.jsonString(buf.toString()));
        }

        return result;
    }

    private Node optimizeStatements(JsonNode statements, EvaluatorContext context) throws HistoneException {
        ArrayNode result = nodeFactory.jsonArray();

        StringBuilder buf = new StringBuilder();
        for (JsonNode element : statements) {
            if (isString(element)) {
                buf.append(element.asText());
            } else {
                Node node = optimizeNode(element, context);
                if (node instanceof AstNode) {
                    if (buf.length() > 0) {
                        result.add(nodeFactory.jsonArray(AstNodeType.STRING, nodeFactory.jsonString(buf.toString())));
                        buf.setLength(0);
                    }
                    result.add(((AstNode) node).getValue());
                } else {
                    buf.append(node.getAsString().getValue());
                }
            }
        }
        if (buf.length() > 0) {
            result.add(nodeFactory.jsonArray(AstNodeType.STRING, nodeFactory.jsonString(buf.toString())));
        }

        return AstNode.create(result);
    }

    private Node optimizeNode(JsonNode element, EvaluatorContext context) throws HistoneException {
        if (isString(element)) {
            return nodeFactory.jsonToNode(element);
        }

        if (!element.isArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", element.toString());
            return AstNode.create(element);
        }

        ArrayNode astArray = (ArrayNode) element;

        int nodeType = astArray.get(0).asInt();
        switch (nodeType) {
            case AstNodeType.TRUE:
                return nodeFactory.TRUE;

            case AstNodeType.FALSE:
                return nodeFactory.FALSE;

            case AstNodeType.NULL:
                return nodeFactory.NULL;

            case AstNodeType.INT:
                return nodeFactory.jsonToNode(astArray.get(1));

            case AstNodeType.DOUBLE:
                return nodeFactory.jsonToNode(astArray.get(1));

            case AstNodeType.STRING:
                return nodeFactory.jsonToNode(astArray.get(1));


//            case AstNodeType.MAP:
//            TODO:
//            case AstNodeType.ARRAY:
//                return optimizeArray((ArrayNode) astArray.get(1), context);
//
//            case AstNodeType.OBJECT:
//                return optimizeObject((ArrayNode) astArray.get(1), context);

            case AstNodeType.ADD:
                return optimizeAdd((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.SUB:
                return optimizeSub((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.MUL:
                return optimizeMul((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.DIV:
                return optimizeDiv((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.MOD:
                return optimizeMod((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);

            case AstNodeType.NEGATE:
                return optimizeNegate((ArrayNode) astArray.get(1), context);

            case AstNodeType.OR:
                return optimizeOr((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.AND:
                return optimizeAnd((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.NOT:
                return optimizeNot((ArrayNode) astArray.get(1), context);

            case AstNodeType.EQUAL:
                return optimizeEqual((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.NOT_EQUAL:
                return optimizeNotEqual((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);

            case AstNodeType.LESS_OR_EQUAL:
                return optimizeLessOrEqual((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.LESS_THAN:
                return optimizeLessThan((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return optimizeGreaterOrEqual((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.GREATER_THAN:
                return optimizeGreaterThan((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), context);
            case AstNodeType.TERNARY:
                return optimizeTernary((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), (astArray.size() > 3) ? (ArrayNode) astArray.get(3) : null, context);
            case -AstNodeType.IF:
                return optimizeIf((ArrayNode) astArray.get(1), context);
            case -AstNodeType.FOR:
                return optimizeFor((ArrayNode) astArray.get(1), (ArrayNode) astArray.get(2), (ArrayNode) astArray.get(3), context);
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
//                return optimizeVar(astArray.get(1).getAsJsonPrimitive(), (ArrayNode) astArray.get(2), context);
//
//            case AstNodeType.SELECTOR:
//                return optimizeSelector((ArrayNode) astArray.get(1), context);
//
            case -AstNodeType.CALL:
                return optimizeCall(astArray.get(1), astArray.get(2), astArray.get(3), context);
//
//            case AstNodeType.IMPORT:
//                return optimizeImport(astArray.get(1), context);
//
            case AstNodeType.MACRO:
                return optimizeMacro(astArray.get(1), (ArrayNode) astArray.get(2), (ArrayNode) astArray.get(3), context);

            default:
                Histone.runtime_log_error("Optimization is unsupported for node type {}", null, nodeType);
                return AstNode.create(makeElementSafe(astArray));
        }
    }

    private Node evaluate(ArrayNode astArray, EvaluatorContext context) throws HistoneException {
        //todo baseURI
        String result = evaluator.process("", astArray, node2Ast(context.getAsNode()));
        return nodeFactory.string(result);
    }

    private Node optimizeCall(JsonNode target, JsonNode nameElement, JsonNode args, EvaluatorContext context) throws HistoneException {
        ArrayNode argsOpt = nodeFactory.jsonArray();
        for (JsonNode arg : args) {
            Node node = optimizeNode(arg, context);
            if (node.isAst()) {
                argsOpt.add(makeElementSafe((ArrayNode) ((AstNode) node).getValue()));
            } else {
                argsOpt.add(node2Ast(node));
            }
        }

        return AstNode.create(nodeFactory.jsonArray(AstNodeType.CALL, target, nameElement, argsOpt));
    }

    private Node optimizeMacro(JsonNode ident, ArrayNode args, ArrayNode statements, EvaluatorContext context) throws EvaluatorException {
        String name = ident.asText();

        MacroFunc func = new MacroFunc();
        func.setArgs(args);
        func.setStatements(statements);
        context.putMacro(name, func);

        return AstNode.create(nodeFactory.jsonArray(AstNodeType.MACRO, ident, args, statements));
    }


    private Node optimizeAdd(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.ADD, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.ADD, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.ADD, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_add(right);
            }
        }
    }


    private Node optimizeSub(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.SUB, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.SUB, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.SUB, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_sub(right);
            }
        }
    }

    private Node optimizeMul(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MUL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MUL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MUL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_mul(right);
            }
        }
    }

    private Node optimizeDiv(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.DIV, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.DIV, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.DIV, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_div(right);
            }
        }
    }

    private Node optimizeMod(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MOD, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MOD, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.MOD, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_mod(right);
            }
        }
    }

    private Node optimizeNegate(ArrayNode left, EvaluatorContext context) throws HistoneException {
        Node nodeLeft = optimizeNode(left, context);
        if (nodeLeft.isAst()) {
            return AstNode.create(nodeFactory.jsonArray(AstNodeType.NEGATE, left));
        } else {
            return nodeLeft.oper_negate();
        }
    }

    private Node optimizeOr(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.OR, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.OR, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.OR, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_or(right);
            }
        }
    }

    private Node optimizeAnd(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.AND, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.AND, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.AND, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_and(right);
            }
        }
    }

    private Node optimizeNot(ArrayNode left, EvaluatorContext context) throws HistoneException {
        Node nodeLeft = optimizeNode(left, context);
        if (nodeLeft.isAst()) {
            return AstNode.create(nodeFactory.jsonArray(AstNodeType.NOT, left));
        } else {
            return nodeLeft.oper_not();
        }
    }

    private Node optimizeEqual(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_equal(right);
            }
        }
    }

    private Node optimizeNotEqual(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.NOT_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.NOT_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.NOT_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_notEqual(right);
            }
        }
    }

    private Node optimizeLessOrEqual(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_OR_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_OR_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_OR_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_lessOrEqual(right);
            }
        }
    }

    private Node optimizeLessThan(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_THAN, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_THAN, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.LESS_THAN, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_lessThan(right);
            }
        }
    }

    private Node optimizeGreaterOrEqual(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_OR_EQUAL, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_OR_EQUAL, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_OR_EQUAL, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_greaterOrEqual(right);
            }
        }
    }

    private Node optimizeGreaterThan(ArrayNode nodeLeft, ArrayNode nodeRight, EvaluatorContext context) throws HistoneException {
        Node left = optimizeNode(nodeLeft, context);
        Node right = optimizeNode(nodeRight, context);

        if (left.isAst()) {
            if (right.isAst()) {
                // nothing to mark
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_THAN, nodeLeft, nodeRight));
            } else {
                // only left part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_THAN, nodeLeft, node2Ast(right)));
            }
        } else {
            if (right.isAst()) {
                // only right part was optimized
                return AstNode.create(nodeFactory.jsonArray(AstNodeType.GREATER_THAN, node2Ast(left), nodeRight));
            } else {
                // full optimization is done
                return left.oper_greaterThan(right);
            }
        }
    }

//    private Node optimizeObject(ArrayNode items, EvaluatorContext context) throws HistoneException {
//        boolean isSafe = true;
//
//        ObjectHistoneNode result = ObjectHistoneNode.create();
//        for (JsonNode item : items) {
//            JsonPrimitive key = item.getAsArrayNode().get(0).getAsJsonPrimitive();
//            JsonNode val = item.getAsArrayNode().get(1);
//            Node itemNode = optimizeNode(val, context);
//            if (itemNode instanceof AstNode) {
//                isSafe = false;
//                break;
//            }
//            result.put(key.asText(), itemNode);
//        }
//
//        if (isSafe) {
//            return result;
//        } else {
//            return AstNode.create(nodeFactory.jsonArray(AstNodeType.OBJECT, items));
//        }
//    }

    private Node optimizeTernary(ArrayNode condition, ArrayNode trueNode, ArrayNode falseNode, EvaluatorContext context) throws HistoneException {
        Node optCondition = optimizeNode(condition, context);
        Node optTrueNode = optimizeNode(trueNode, context);
        Node optFalseNode = falseNode != null ? optimizeNode(falseNode, context) : nodeFactory.NULL;

        if (optCondition.isAst()) {
            JsonNode argTrue = optTrueNode.isAst() ? trueNode : optTrueNode.getAsJsonNode();
            JsonNode argFalse = optFalseNode.isAst() ? falseNode : optFalseNode.getAsJsonNode();
            return nodeFactory.jsonToNode(nodeFactory.jsonArray(AstNodeType.TERNARY, condition, argTrue, argFalse));
        } else {
            if (optCondition.getAsBoolean().getValue()) {
                return optimizeNode(trueNode, context);
            } else if (falseNode != null) {
                return optimizeNode(falseNode, context);
            } else {
                return nodeFactory.UNDEFINED;
            }
        }

    }

    private Node optimizeIf(ArrayNode conditions, EvaluatorContext context) throws HistoneException {
        ArrayNode conditionsOut = nodeFactory.jsonArray();

        context.saveState();
        for (JsonNode condition : conditions) {
            ArrayNode expressionAst = (ArrayNode) condition.get(0);
            Node expressionResult = optimizeNode(expressionAst, context);

            ArrayNode statementsAst = (ArrayNode) condition.get(1);
            Node statementsResult = optimizeStatements(statementsAst, context);

            if (!expressionResult.isAst() && expressionResult.getAsBoolean().getValue()) {
                if (statementsResult.isAst()) {
                    return statementsResult;
                } else {
                    return statementsResult.getAsString();
                }
            } else {
                ArrayNode conditionOut = nodeFactory.jsonArray();
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

        return AstNode.create(nodeFactory.jsonArray(AstNodeType.IF, conditionsOut));
    }

    private Node optimizeFor(ArrayNode iterator, ArrayNode collection, ArrayNode statements, EvaluatorContext context) throws HistoneException {
        Node collectionOpt = optimizeNode(collection, context);
        Node statementsForOpt = optimizeStatements(statements.get(0), context);
        Node statementsElseOpt = statements.size() > 1 ? optimizeStatements(statements.get(1), context) : null;

        if (!collectionOpt.isAst()) { // we can't mark 'for' loop if loop collection can't be optimized to Node object (either array or object)
            String iterVal = iterator.get(0).asText();
            String iterKey = (iterator.size() > 1) ? iterator.get(1).asText() : null;

            // run evaluator with 'for' AST subtree
            ArrayNode forAst = nodeFactory.jsonArray();
            forAst.add(nodeFactory.jsonArray(AstNodeType.FOR, iterator, collection, statements));
            //todo baseURI
            String result = evaluator.process("", forAst, node2Ast(context.getAsNode()));
            return nodeFactory.string(result);

        }

        ArrayNode statementsArr = nodeFactory.jsonArray();
        if (!statementsForOpt.isAst()) {
            statementsArr.add(node2Ast(statementsForOpt));
        } else {
            statementsArr.add(statements.get(0));
        }

        if (statementsElseOpt != null) {
            if (!statementsElseOpt.isAst()) {
                statementsArr.add(node2Ast(statementsElseOpt));
            } else {
                statementsArr.add(statements.get(1));
            }
        }

        return AstNode.create(nodeFactory.jsonArray(AstNodeType.FOR, iterator, ((AstNode) collectionOpt).getValue(), statementsArr));

    }

//    private boolean isSafe(ArrayNode ast, String... varNames) {
//        Set<String> varNamesSet = new HashSet<String>();
//        for (String name : varNames) {
//            varNamesSet.add(name);
//        }
//
//        for (JsonNode item : ast) {
//            if (item.isArray()) {
//                ArrayNode asArrayNode = item.getAsArrayNode();
//                JsonNode firstElem = asArrayNode.get(0);
//                if (!isString(firstElem)) {
//                    switch (firstElem.getAsJsonPrimitive().asInt()) {
//                        case AstNodeType.SELECTOR:
//                            if (!varNamesSet.contains(asArrayNode.get(1).getAsArrayNode().get(0).asText())) {
//                                return false;
//                            }
//                            break;
//                        case AstNodeType.FOR:
//                        case AstNodeType.IF:
//                        case AstNodeType.CALL:
//                            return false;
//                        default:
//                            if (!isSafe(asArrayNode, varNames)) {
//                                return false;
//                            }
//                    }
//                }
//            }
//        }
//
//        return true;
//    }

    private JsonNode node2Ast(Node node) {
        if (node.isBoolean()) {
            return node.getAsBoolean().getValue() ? nodeFactory.jsonArray(AstNodeType.TRUE) : nodeFactory.jsonArray(AstNodeType.FALSE);
        } else if (node.isInteger()) {
            return nodeFactory.jsonArray(AstNodeType.INT, nodeFactory.jsonNumber(node.getAsNumber().getValue()));
        } else if (node.isFloat()) {
            return nodeFactory.jsonArray(AstNodeType.DOUBLE, nodeFactory.jsonNumber(node.getAsNumber().getValue()));
        } else if (node.isString()) {
            return nodeFactory.jsonArray(AstNodeType.STRING, nodeFactory.jsonString(node.getAsString().getValue()));
        } else if (node.isNull()) {
            return nodeFactory.jsonArray(AstNodeType.NULL);
            //TODO: implement array->map
//        } else if (node.isArray()) {
//            ArrayNode elems = nodeFactory.jsonArray();
//
//            for (Node elem : node.getAsArray().getElements()) {
//                elems.add(node2Ast(elem));
//            }
//
//            return nodeFactory.jsonArray(AstNodeType.ARRAY, elems);
//        } else if (node.isObject()) {
//            ArrayNode props = nodeFactory.jsonArray();
//
//            for (Map.Entry<String, Node> entry : node.getAsObject().entries()) {
//                ArrayNode prop = nodeFactory.jsonArray();
//                prop.add(new JsonPrimitive(entry.getKey()));
//                prop.add(node2Ast(entry.getValue()));
//                props.add(prop);
//            }
//
//            return nodeFactory.jsonArray(AstNodeType.OBJECT, props);
        }
        throw new IllegalStateException(String.format("Can't convert node %s to AST element", node));
    }


    private boolean isString(JsonNode element) {
        return element.isTextual();
    }

    private int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }

    private ArrayNode makeElementSafe(ArrayNode element) throws HistoneException {
        boolean typeUpdated = false;
        ArrayNode result = nodeFactory.jsonArray();
        for (JsonNode item : element) {
            if (typeUpdated) {
                if (item.isArray() && item.get(0).isNumber()) {
                    result.add(makeElementSafe((ArrayNode) item));
                } else {
                    result.add(item);
                }
//                result.add(item);
            } else {
                result.add(nodeFactory.jsonNumber(new BigDecimal(Math.abs(item.asInt()))));
                typeUpdated = true;
            }
        }
        return result;
    }
        
}
