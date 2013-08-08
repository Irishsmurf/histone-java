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
package ru.histone.deparser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import ru.histone.parser.AstNodeType;
import ru.histone.utils.StringUtils;

import java.util.*;

public class Deparser implements IDeparser {
    @Override
    public String deparse(ArrayNode ast) {
        ast = removeHistoneAstSignature(ast);

        StringBuilder result = new StringBuilder();
        for (JsonNode node : ast) {
            String processedNode = processAstNode(node);
            if (processedNode != null) result.append(processedNode + "\n");
        }
        return result.toString();
    }

    protected static ArrayNode removeHistoneAstSignature(ArrayNode ast) {
        if (ast.size() == 2 &&
                ast.get(0).isArray() &&
                ast.get(1).isArray() &&
                "HISTONE".equals(ast.get(0).get(0).asText())) {
            return (ArrayNode) ast.get(1);
        } else {
            return ast;
        }
    }

    protected String processAstNode(JsonNode node) {
        if (node.isTextual()) {
            String escapedString = node.toString();
            escapedString = org.apache.commons.lang.StringUtils.remove(escapedString, "\\t");
            escapedString = org.apache.commons.lang.StringUtils.remove(escapedString, "\\n");
            escapedString = org.apache.commons.lang.StringUtils.remove(escapedString, "\\r");
            escapedString = escapedString.trim();

            return escapedString.length() > 2 ? ind() + escapedString : null;
        }

        if (!node.isArray()) return null;
        ArrayNode arr = (ArrayNode) node;
        if (arr.size() == 0) return null;

        int nodeType = Math.abs(getNodeType(arr));

        if (CONSTANTS.contains(nodeType)) {
            return processConstants(arr);
        } else if (UNARY_OPERATIONS.contains(nodeType)) {
            return processUnaryOperation(arr);
        } else if (BINARY_OPERATIONS.contains(nodeType)) {
            return processBinaryOperation(arr);
        } else if (TERNARY_OPERATIONS.contains(nodeType)) {
            return processTernaryOperation(arr);
        } else {
            switch (nodeType) {
                case AstNodeType.SELECTOR:
                    return processSelector(arr);
                case AstNodeType.STATEMENTS:
                    return processStatements(arr);
                case AstNodeType.IMPORT:
                    return processImport(arr);

                case AstNodeType.VAR:
                    return processVariable(arr);
                case AstNodeType.IF:
                    return processIf(arr);
                case AstNodeType.FOR:
                    return processFor(arr);

                case AstNodeType.MACRO:
                    return processMacro(arr);
                case AstNodeType.CALL:
                    return processCall(arr);
                case AstNodeType.MAP:
                    return processMap(arr);
                default:
                    return null;
            }
        }
    }

    protected String processCall(ArrayNode ast) {
        JsonNode objectToInvoke = ast.get(1);
        JsonNode functionName = ast.get(2);
        JsonNode args = ast.get(3);

        String objectToInvokeProcessed = processAstNode(objectToInvoke);
        List<String> argsProcessed = new ArrayList<String>();
        for (JsonNode arg : args) {
            argsProcessed.add(processAstNode(arg));
        }

        if (objectToInvokeProcessed != null) {
            return ind() + objectToInvokeProcessed + "." + functionName.asText() + "(" + StringUtils.join(argsProcessed, ", ") + ");\n";
        } else {
            return ind() + functionName.asText() + "(" + StringUtils.join(argsProcessed, ", ") + ");\n";
        }
    }

    protected String processMap(ArrayNode ast) {
        ArrayNode map = (ArrayNode) ast.get(1);
        Set<String> entriesAsStrings = new LinkedHashSet<String>();
        for (JsonNode entry : map) {
            if (entry.isArray()) {
                ArrayNode entryAsArray = (ArrayNode) entry;
                JsonNode key = entryAsArray.get(0);
                JsonNode value = entryAsArray.get(1);
                String valueProcessed = processAstNode(value);

                if (!key.isNull()) {
                    String keyAsString = key.asText();
                    entriesAsStrings.add(keyAsString + " : " + valueProcessed);
                } else {
                    entriesAsStrings.add(valueProcessed);
                }
            }
        }

        return "[" + StringUtils.join(entriesAsStrings, ", ") + "]";
    }

    protected String processConstants(ArrayNode ast) {
        int opType = ast.get(0).asInt();

        if (opType == AstNodeType.TRUE) {
            return "true";
        }
        if (opType == AstNodeType.FALSE) {
            return "false";
        }
        if (opType == AstNodeType.NULL) {
            return "null";
        }
        if (opType == AstNodeType.INT) {
            return ast.get(1).asText();
        }
        if (opType == AstNodeType.DOUBLE) {
            return ast.get(1).asText();
        }
        if (opType == AstNodeType.STRING) {
            return ast.get(1).toString();
        }

        return null;
    }

    protected String processUnaryOperation(ArrayNode ast) {
        int opType = ast.get(0).asInt();
        String arg1 = processAstNode(ast.get(1));

        if (opType == AstNodeType.NOT) {
            return ind() + "!(" + arg1 + ")";
        }
        if (opType == AstNodeType.NEGATE) {
            return ind() + "-(" + arg1 + ")";
        }

        return null;
    }

    protected String processBinaryOperation(ArrayNode ast) {
        int opType = ast.get(0).asInt();
        String arg1 = processAstNode(ast.get(1));
        String arg2 = processAstNode(ast.get(2));

        if (opType == AstNodeType.ADD) {
            return "(" + arg1 + " + " + arg2 + ")";
        }
        if (opType == AstNodeType.SUB) {
            return "(" + arg1 + " - " + arg2 + ")";
        }
        if (opType == AstNodeType.MUL) {
            return "(" + arg1 + " * " + arg2 + ")";
        }
        if (opType == AstNodeType.DIV) {
            return "(" + arg1 + " / " + arg2 + ")";
        }
        if (opType == AstNodeType.MOD) {
            return "(" + arg1 + " % " + arg2 + ")";
        }
        if (opType == AstNodeType.OR) {
            return "(" + arg1 + " || " + arg2 + ")";
        }
        if (opType == AstNodeType.AND) {
            return "(" + arg1 + " && " + arg2 + ")";
        }
        if (opType == AstNodeType.EQUAL) {
            return "(" + arg1 + " == " + arg2 + ")";
        }
        if (opType == AstNodeType.NOT_EQUAL) {
            return "(" + arg1 + " != " + arg2 + ")";
        }
        if (opType == AstNodeType.LESS_OR_EQUAL) {
            return "(" + arg1 + " <= " + arg2 + ")";
        }
        if (opType == AstNodeType.LESS_THAN) {
            return "(" + arg1 + " < " + arg2 + ")";
        }
        if (opType == AstNodeType.GREATER_OR_EQUAL) {
            return "(" + arg1 + " >= " + arg2 + ")";
        }
        if (opType == AstNodeType.GREATER_THAN) {
            return "(" + arg1 + " > " + arg2 + ")";
        }

        return null;
    }

    protected String processTernaryOperation(ArrayNode ast) {
        int opType = ast.get(0).asInt();

        String arg1 = processAstNode(ast.get(1));
        String arg2 = processAstNode(ast.get(2));
        String arg3 = ast.get(3) != null ? processAstNode(ast.get(3)) : "null";

        if (opType == AstNodeType.TERNARY) {
            return ind() + "(" + arg1 + ") ? (" + arg2 + ") : (" + arg3 + ");\n";
        }

        return null;
    }

    protected String processStatements(ArrayNode ast) {
        ArrayNode statements = (ArrayNode) ast.get(1);

        StringBuilder sb = new StringBuilder();
        for (JsonNode statement : statements) {
            sb.append(processAstNode(statement));
        }
        return sb.toString();
    }

    protected String processImport(ArrayNode ast) {
        String importResource = ast.get(1).asText();
        return ind() + "import " + importResource + ";";
    }

    protected String processVariable(ArrayNode ast) {
        JsonNode varName = ast.get(1);
        JsonNode varDefinition = ast.get(2);

        String varDefinitionProcessed = processAstNode(varDefinition);
        return ind() + varName.asText() + " = " + varDefinitionProcessed + ";\n";
    }

    protected String processMacro(ArrayNode ast) {
        JsonNode macroName = ast.get(1);
        ArrayNode args = (ArrayNode) ast.get(2);
        ArrayNode statements = (ArrayNode) ast.get(3);

        StringBuilder sb = new StringBuilder();
        List<String> argsProcessed = new ArrayList<String>();
        for (JsonNode arg : args) argsProcessed.add(arg.asText());
        sb.append(ind() + "macro " + macroName.asText() + "(" + StringUtils.join(argsProcessed, ", ") + ") {\n");
        indent();
        for (JsonNode statement : statements) {
            String s = processAstNode(statement);
            if (s != null) sb.append(s + "\n");
        }
        unindent();
        sb.append("}\n");

        return sb.toString();
    }

    protected String processIf(ArrayNode ast) {
        ArrayNode ifBlock = (ArrayNode) ast.get(1);

        StringBuilder sb = new StringBuilder();

        for (JsonNode ifElement : ifBlock) {
            JsonNode expression = ifElement.get(0);
            JsonNode statements = ifElement.get(1);

            String expressionProcessed = processAstNode(expression);

            sb.append(ind() + "if (" + expressionProcessed + ") {\n");
            indent();
            for (JsonNode statement : statements) {
                String s = processAstNode(statement);
                if (s != null) sb.append((isSelector(statement) ? ind() : "") +  s + "\n");
            }
            unindent();
            sb.append(ind() + "}\n");
        }

        return sb.toString();
    }

    protected String processFor(ArrayNode ast) {
        ArrayNode var = (ArrayNode) ast.get(1);
        ArrayNode collection = (ArrayNode) ast.get(2);
        ArrayNode ifStatements = (ArrayNode) ast.get(3).get(0);
        ArrayNode elseStatements = (ast.get(3).size() > 1) ? elseStatements = (ArrayNode) ast.get(3).get(1) : null;

        StringBuilder result = new StringBuilder();

        String v1 = var.get(0).asText();
        String v2 = (var.size() > 1) ? var.get(1).asText() : null;

        String collectionProcessed = processAstNode(collection);
        result.append(ind() + "for (" + v1 + "," + v2 + " in " + collectionProcessed + ") {\n");

        indent();
        for (JsonNode ifStatement : ifStatements) {
            String s = processAstNode(ifStatement);
            if (s != null) result.append(s);
        }
        unindent();

        if (elseStatements != null) {
            result.append(ind() + "} else {\n");
            for (JsonNode elseStatement : elseStatements) {
                String s = processAstNode(elseStatement);
                if (s != null) result.append(s);
            }
            result.append(ind() + "}\n");
        }

        return result.toString();
    }

    protected String processSelector(ArrayNode ast) {
        JsonNode selector = ast.get(1);

        List<String> result = new ArrayList<String>();
        for (JsonNode partOfSelector : selector) {
            if (partOfSelector.isArray()) {
                result.add(processAstNode(partOfSelector));
            } else {
                result.add(partOfSelector.textValue());
            }
        }
        return StringUtils.join(result, ".");
    }

    //<editor-fold desc="Indentation">

    StringBuilder _indent = new StringBuilder();

    protected void indent() {
        _indent.append("  ");
    }

    protected void unindent() {
        if (_indent.length() >= 2) {
            _indent.setLength(_indent.length() - 2);
        }
    }

    protected String ind() {
        return _indent.toString();
    }

    //</editor-fold>

    protected final static Set<Integer> CONSTANTS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.TRUE, AstNodeType.FALSE, AstNodeType.NULL, AstNodeType.INT, AstNodeType.DOUBLE, AstNodeType.STRING
    ));

    protected final static Set<Integer> BINARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(
            AstNodeType.ADD, AstNodeType.SUB, AstNodeType.MUL, AstNodeType.DIV, AstNodeType.MOD,
            AstNodeType.OR, AstNodeType.AND, AstNodeType.EQUAL, AstNodeType.NOT_EQUAL,
            AstNodeType.LESS_OR_EQUAL, AstNodeType.LESS_THAN,
            AstNodeType.GREATER_OR_EQUAL, AstNodeType.GREATER_THAN
    ));

    protected final static Set<Integer> UNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(AstNodeType.NEGATE, AstNodeType.NOT));

    protected final static Set<Integer> TERNARY_OPERATIONS = new HashSet<Integer>(Arrays.asList(AstNodeType.TERNARY));

    public static boolean isSelector(JsonNode ast) {
        if (ast instanceof ArrayNode) return getNodeType((ArrayNode)ast) == AstNodeType.SELECTOR;
        else return false;
    }

    public static int getNodeType(ArrayNode astArray) {
        return astArray.get(0).asInt();
    }

    public static long hash(JsonNode arr) {
        long result = 0;

        if (arr.isContainerNode()) {
            for (JsonNode node : arr) {
                result += hash(node);
            }
        }

        if (arr.isValueNode()) {
            result += arr.asText().hashCode();
        }

        return result;
    }

    public static long countNodes(JsonNode arr) {
        long result = 0;

        if (arr.isContainerNode()) {
            for (JsonNode node : arr) {
                result += countNodes(node) + 1;
            }
        }

        if (arr.isValueNode()) {
            result += 1;
        }

        return result;
    }
}
