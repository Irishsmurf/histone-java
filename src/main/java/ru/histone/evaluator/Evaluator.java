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
package ru.histone.evaluator;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.GlobalProperty;
import ru.histone.Histone;
import ru.histone.HistoneException;
import ru.histone.evaluator.functions.global.*;
import ru.histone.evaluator.functions.node.*;
import ru.histone.evaluator.functions.node.number.*;
import ru.histone.evaluator.functions.node.object.*;
import ru.histone.evaluator.functions.node.object.Slice;
import ru.histone.evaluator.functions.node.string.*;
import ru.histone.evaluator.functions.node.string.Size;
import ru.histone.evaluator.nodes.*;
import ru.histone.parser.AstNodeType;
import ru.histone.parser.Parser;
import ru.histone.parser.ParserException;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoadException;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.utils.ArrayUtils;
import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Histone AST evaluator<br/>
 * This class takes AST for input and evaluates it's nodes producing output
 */
public class Evaluator {
    private static final Logger log = LoggerFactory.getLogger(Evaluator.class);

    private final Parser parser;
    private final Gson gson;
    private final ResourceLoader resourceLoader;
    private final GlobalFunctionsManager globalFunctionsManager;
    private final NodeFunctionsManager nodeFunctionsManager;
    private final GlobalObjectNode global;


    public Evaluator(EvaluatorBootstrap bootstrap) {
        this.parser = bootstrap.getParser();
        this.gson = bootstrap.getGson();
        this.resourceLoader = bootstrap.getResourceLoader();
        this.globalFunctionsManager = registerMandatoryGlobalFunctions(bootstrap);
        this.nodeFunctionsManager = registerMandatoryNodeFunctions(bootstrap);
        this.global = bootstrap.getGlobal();
    }

    /**
     * Register Histone built-in node functions, forcing them to override any custom user functions
     *
     * @param bootstrap bootstrap object
     * @return node functions manager
     */
    private static NodeFunctionsManager registerMandatoryNodeFunctions(EvaluatorBootstrap bootstrap) {
        NodeFunctionsManager nodeFunctionsManager = bootstrap.getNodeFunctionsManager();
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new HasIndex());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new Join());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new Slice());

        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new HasKey());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new Keys());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new Values());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new Remove());
        nodeFunctionsManager.registerBuiltInFunction(ObjectNode.class, new ru.histone.evaluator.functions.node.object.Size());

        nodeFunctionsManager.registerBuiltInFunction(NumberNode.class, new Abs());
        nodeFunctionsManager.registerBuiltInFunction(NumberNode.class, new Ceil());
        nodeFunctionsManager.registerBuiltInFunction(NumberNode.class, new Floor());
        nodeFunctionsManager.registerBuiltInFunction(NumberNode.class, new Round());
        nodeFunctionsManager.registerBuiltInFunction(NumberNode.class, new ToChar());

        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new CharCodeAt());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new ru.histone.evaluator.functions.node.string.Slice());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new Split());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new Strip());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new ToLowerCase());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new ToNumber());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new ToUpperCase());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new Test());
        nodeFunctionsManager.registerBuiltInFunction(StringNode.class, new Size());

        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsBoolean());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsFloat());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsInteger());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsNull());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsNumber());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsMap());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsString());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new IsUndefined());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new ToJson(bootstrap.getGson()));
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new ToString());
        nodeFunctionsManager.registerBuiltInFunction(Node.class, new ToMap());

        return nodeFunctionsManager;
    }

    /**
     * Register Histone built-in global functions, forcing them to override any custom user functions
     *
     * @param bootstrap bootstrap object
     * @return global functions manager
     */
    private static GlobalFunctionsManager registerMandatoryGlobalFunctions(EvaluatorBootstrap bootstrap) {
        GlobalFunctionsManager globalFunctionsManager = bootstrap.getGlobalFunctionsManager();
        globalFunctionsManager.registerBuiltInFunction(new Min());
        globalFunctionsManager.registerBuiltInFunction(new Max());
        globalFunctionsManager.registerBuiltInFunction(new UniqueId());
        globalFunctionsManager.registerBuiltInFunction(new Range());
        globalFunctionsManager.registerBuiltInFunction(new DayOfWeek());
        globalFunctionsManager.registerBuiltInFunction(new DaysInMonth());
        return globalFunctionsManager;
    }

    public void setGlobalProperty(GlobalProperty property, String value) {
        global.add(property.getName(), StringNode.create(value));
    }

    /**
     * Process and evaluate template using specified evaluator context
     *
     * @param input   template content
     * @param jsonCtx json object for evaluator context
     * @return evaluation result
     * @throws ru.histone.HistoneException in case of eny errors
     */
    public String process(String input, JsonElement jsonCtx) throws HistoneException {
        JsonArray ast = parser.parse(input);
        return process(ast, jsonCtx);
    }

    /**
     * Process and evaluate template using specified evaluator context
     *
     * @param input   template content
     * @param context special context object for evaluator context
     * @return evaluation result
     * @throws ru.histone.HistoneException in case of eny errors
     */
    public String process(String input, EvaluatorContext context) throws HistoneException {
        JsonArray ast = parser.parse(input);
        return process(ast, context);
    }

    /**
     * Evaluate template AST using specified evaluator context
     *
     * @param ast     tempalte AST in json representation
     * @param jsonCtx json object for evaluator context
     * @return evaluation result
     * @throws ru.histone.HistoneException in case of eny errors
     */
    public String process(JsonArray ast, JsonElement jsonCtx) throws HistoneException {
        return process(ast, EvaluatorContext.createFromJson(global, jsonCtx));
    }

    /**
     * Evaluate template AST using specified evaluator context
     *
     * @param ast     tempalte AST in json representation
     * @param context special context object for evaluator context
     * @return evaluation result
     * @throws ru.histone.HistoneException in case of eny errors
     */
    public String process(JsonArray ast, EvaluatorContext context) throws HistoneException {
        return processInternal(ast, context);
    }

    private String processInternal(JsonElement jsonElement, EvaluatorContext context) throws EvaluatorException {
        return processInternal(jsonElement.getAsJsonArray(), context);
    }

    private String processInternal(JsonArray ast, EvaluatorContext context) throws EvaluatorException {
        log.debug("processInternal(): template={}, context={}", ast, context);

        StringBuilder out = new StringBuilder();
        for (JsonElement element : ast) {
            log.debug("process(): fragment={}", element);
            Node node = processNode(element, context);
            log.debug("process(): node={}", node);
            out.append(node.getAsString().getValue());
        }
        return out.toString();
    }

    private boolean isString(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    private Node processNode(JsonElement jsonElement, EvaluatorContext context) throws EvaluatorException {
        log.debug("processNode(): element={}, context={}", new Object[]{jsonElement, context});

        if (isString(jsonElement)) {
            return StringNode.create(jsonElement.getAsJsonPrimitive().getAsString());
        }

        if (!jsonElement.isJsonArray()) {
            Histone.runtime_log_warn("Invalid JSON element! Neither 'string', nor 'array'. Element: '{}'", jsonElement.toString());
            return Node.UNDEFINED;
        }

        JsonArray astArray = jsonElement.getAsJsonArray();

        int nodeType = astArray.getAsJsonArray().get(0).getAsJsonPrimitive().getAsInt();
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

            case AstNodeType.MAP:
                return processMap(astArray.get(1).getAsJsonArray(), context);
//
//            case AstNodeType.ARRAY:
//                return processArray(astArray.get(1).getAsJsonArray(), context);
//
//            case AstNodeType.OBJECT:
//                return processObject(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.ADD:
                return processAdd(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.SUB:
                return processSub(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MUL:
                return processMul(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.DIV:
                return processDiv(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.MOD:
                return processMod(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.NEGATE:
                return processNegate(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.OR:
                return processOr(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.AND:
                return processAnd(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT:
                return processNot(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.EQUAL:
                return processEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.NOT_EQUAL:
                return processNotEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.LESS_OR_EQUAL:
                return processLessOrEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.LESS_THAN:
                return processLessThan(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_OR_EQUAL:
                return processGreaterOrEqual(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.GREATER_THAN:
                return processGreaterThan(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), context);
            case AstNodeType.TERNARY:
                return processTernary(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), (astArray.size() > 3) ? astArray.get(3).getAsJsonArray() : null, context);
            case AstNodeType.IF:
                return processIf(astArray.get(1).getAsJsonArray(), context);
            case AstNodeType.FOR:
                return processFor(astArray.get(1).getAsJsonArray(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);

            case AstNodeType.STATEMENTS:
                return processStatements(astArray.get(1), context);

            case AstNodeType.VAR:
                return processVar(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), context);

            case AstNodeType.SELECTOR:
                return processSelector(astArray.get(1).getAsJsonArray(), context);

            case AstNodeType.CALL:
                return processCall(astArray.get(1), astArray.get(2), astArray.get(3), context);

            case AstNodeType.IMPORT:
                return processImport(astArray.get(1), context);

            case AstNodeType.MACRO:
                return processMacro(astArray.get(1).getAsJsonPrimitive(), astArray.get(2).getAsJsonArray(), astArray.get(3).getAsJsonArray(), context);

            default:
                Histone.runtime_log_error("Unknown nodeType", null, nodeType);
                return Node.UNDEFINED;
        }
    }

    private JsonArray slice(JsonArray astArray, int fromIndex) {
        JsonArray result = new JsonArray();

        for (int i = fromIndex; i < astArray.size(); i++) {
            result.add(astArray.get(i));
        }

        return result;
    }

    private Node processMacro(JsonPrimitive ident, JsonArray args, JsonArray statements, EvaluatorContext context) throws EvaluatorException {
        String name = ident.getAsString();

        MacroFunc func = new MacroFunc();
        func.setArgs(args);
        func.setStatements(statements);
        func.setBaseURI(getContextBaseURI(context));
        context.putMacro(name, func);

        return Node.UNDEFINED;
    }

    private Node runMacro(String name, List<Node> args, EvaluatorContext context) throws EvaluatorException {
        MacroFunc macro = context.getMacro(name);
        if (macro == null) {
            Histone.runtime_log_warn("No macro found by name = '{}'", name);
            return Node.UNDEFINED;
        }
        ObjectNode self = ObjectNode.create();
        ObjectNode argsNode = ObjectNode.create(args);
        self.add("arguments", argsNode);
        context.putProp("self", self);

        Iterator<Node> argsItr = args.iterator();
        for (JsonElement macroArg : macro.getArgs()) {
            context.putProp(macroArg.getAsString(), (argsItr.hasNext() ? argsItr.next() : Node.UNDEFINED));
        }
        String currentBaseURI = getContextBaseURI(context);
        String macroBaseURI = macro.getBaseURI();
        if (macroBaseURI != null/* && macroBaseURI.isAbsolute() && !macroBaseURI.isOpaque()*/) {
            context.setGlobalValue(GlobalProperty.BASE_URI, StringNode.create(macroBaseURI));
        }
        StringNode result = StringNode.create(processInternal(macro.getStatements(), context));
        context.setGlobalValue(GlobalProperty.BASE_URI, currentBaseURI == null ? Node.NULL : StringNode.create(currentBaseURI));
        return result;
    }

    private Node processImport(JsonElement pathElement, EvaluatorContext context) throws EvaluatorException {
        if (!isString(pathElement)) {
            Histone.runtime_log_warn("Invalid path to imported template: '{}'", pathElement.toString());
            return Node.UNDEFINED;
        }
        String path = pathElement.getAsJsonPrimitive().getAsString();
        Resource resource = null;
        InputStream resourceStream = null;
        try {
            String currentBaseURI = getContextBaseURI(context);
            String resourceFullPath = resourceLoader.resolveFullPath(path, currentBaseURI);

            if (context.hasImportedResource(resourceFullPath)) {
                Histone.runtime_log_info("Resource already imported.");
                return Node.UNDEFINED;
            } else {
                resource = resourceLoader.load(path, currentBaseURI);
                if (resource == null) {
                    Histone.runtime_log_warn("Can't import resource by path = '{}'. Resource was not found.", path);
                    return Node.UNDEFINED;
                }
                resourceStream = resource.getInputStream();
                if (resourceStream == null) {
                    Histone.runtime_log_warn("Can't import resource by path = '{}'. Resource is unreadable", path);
                    return Node.UNDEFINED;
                }
                String templateContent = IOUtils.toString(resourceStream); //yeah... full file reading, because of our tokenizer is regexp-based :(

                // Add this resource full path to context
                context.addImportedResource(resourceFullPath.toString());

                JsonElement parseResult = parser.parse(templateContent);
                URI resourceURI = (resource.getBaseHref() != null) ? URI.create(resource.getBaseHref()) : null;
                if (resourceURI != null && resourceURI.isAbsolute() && !resourceURI.isOpaque()) {
                    context.setGlobalValue(GlobalProperty.BASE_URI, StringNode.create(resourceURI.toString()));
                }
                StringNode.create(processInternal(parseResult, context));
                context.setGlobalValue(GlobalProperty.BASE_URI, currentBaseURI == null ? Node.NULL : StringNode.create(currentBaseURI.toString()));

                return Node.UNDEFINED;
            }
        } catch (ResourceLoadException e) {
            Histone.runtime_log_warn_e("Resource import failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (IOException e) {
            Histone.runtime_log_warn_e("Resource import failed! Resource reading error.", e);
            return Node.UNDEFINED;
        } catch (ParserException e) {
            Histone.runtime_log_warn_e("Resource import failed! Resource parsing error.", e);
            return Node.UNDEFINED;
        } finally {
            IOUtils.closeQuietly(resourceStream, log);
            IOUtils.closeQuietly(resource, log);
        }
    }

    private String getContextBaseURI(EvaluatorContext context) {
        Node value = context.getGlobalValue(GlobalProperty.BASE_URI);
        if (value == null || value.isNull() || !value.isString()) {
            return null;
        }
        return value.getAsString().getValue();
    }

    private Node processCall(JsonElement target, JsonElement nameElement, JsonElement args, EvaluatorContext context) throws EvaluatorException {
        if (nameElement.isJsonArray()) {
            Node functionNameNode = processNode(nameElement.getAsJsonArray(), context);
            nameElement = functionNameNode.getAsJsonElement();
        }
        if (!isString(nameElement)) {
            Histone.runtime_log_warn("call to undefined function '{}'", nameElement.toString());
            return Node.UNDEFINED;
        }
        String name = nameElement.getAsJsonPrimitive().getAsString();
        if (name == null || name.length() == 0) {
            Histone.runtime_log_warn("call to undefined anonymous function");
            return Node.UNDEFINED;
        }

        List<Node> argsList = new ArrayList<Node>();
        if (!args.isJsonNull()) {
            for (JsonElement arg : args.getAsJsonArray()) {
                Node argNode = processNode(arg, context);
                argsList.add(argNode);
            }
        }

        try {
            context.saveState();


            if (!target.isJsonNull()) {
                // if target is not null, then it means we want to run Node fucntion or global.functionName()

                Node targetNode = processNode(target.getAsJsonArray(), context);
                // if target is reserved word 'global' then we will run ObjectNode.function() or global function
                if (targetNode.isObject() && targetNode.getAsObject().isGlobalObject()) {


                    if (nodeFunctionsManager.hasFunction(targetNode, name)) {
                        // if we have such function for ObjectNode type (e.g. global.isObject())
                        return runNodeFunc(targetNode, name, argsList);
                    } else {
                        // next we need to check if we have such global function
                        if (globalFunctionsManager.hasFunction(name)) {
                            return runGlobalFunc(name, argsList);
                        } else {
                            return Node.UNDEFINED;
                        }
                    }
                } else {
                    // if target wasn't global object, then we need to check if we have Node function
                    if (!nodeFunctionsManager.hasFunction(targetNode, name)) {
                        Histone.runtime_log_warn("'{}' is undefined function for type '{}'", name, targetNode.toString());
                        return Node.UNDEFINED;
                    }
                    return runNodeFunc(targetNode, name, argsList);
                }
            }

            // next we will call macro if it exists
            if (context.hasMacro(name)) {
                return runMacro(name, argsList, context);
            }

            // if we don't have macro with such name, then check for globalFunction
            if (globalFunctionsManager.hasFunction(name)) {
                return runGlobalFunc(name, argsList);
            }
            if ("include".equals(name)) {
                // we need to be able to override include function via user GlobalFunction,
                // that's why we need to check this here, after GlobalFunctionManager check
                return processInclude(argsList, context);
            }
            if ("loadJSON".equals(name)) {
                // we need to be able to override loadJSON function via user GlobalFunction,
                // that's why we need to check this here, after GlobalFunctionManager check
                return processLoadJSON(argsList, context);
            }
            if ("loadText".equals(name)) {
                // we need to be able to override loadText function via user GlobalFunction,
                // that's why we need to check this here, after GlobalFunctionManager check
                return processLoadText(argsList, context);
            }

            return Node.UNDEFINED;
        } finally {
            context.restoreState();
        }
    }

    private Node processLoadJSON(List<Node> argsList, EvaluatorContext context) {
        Node[] args = argsList.toArray((Node[]) Array.newInstance(Node.class, argsList.size()));
        if (ArrayUtils.isEmpty(args)) {
            return Node.UNDEFINED;
        }
        Node arg = args[0];
        if (!arg.isString()) {
            throw new GlobalFunctionExecutionException("Non-string path for JSON resource location: " + arg.getAsString().getValue());
        }
        String path = arg.getAsString().getValue();
        String currentBaseURI = getContextBaseURI(context);

        Resource resource = null;
        InputStream resourceStream = null;
        InputStreamReader reader = null;
        try {
            resource = resourceLoader.load(path, currentBaseURI, args);
            if (resource == null) {
                Histone.runtime_log_warn(String.format("Can't load resource by path = '%s'. Resource was not found.", path));
                return Node.UNDEFINED;

            }
            resourceStream = resource.getInputStream();
            if (resourceStream == null) {
                Histone.runtime_log_warn(String.format("Can't load resource by path = '%s'. Resource is unreadable.", path));
                return Node.UNDEFINED;
            }
            reader = new InputStreamReader(resourceStream);
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            if (json == null) {
                Histone.runtime_log_warn("Invalid JSON data found by path: " + path);
                return Node.UNDEFINED;
            }
            return Node.jsonToNode(json);
        } catch (ResourceLoadException e) {
            Histone.runtime_log_warn_e("Resource loadJSON failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (JsonSyntaxException e) {
            Histone.runtime_log_warn_e("Resource loadJSON failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (JsonIOException e) {
            Histone.runtime_log_warn_e("Resource loadJSON failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (IOException e) {
            Histone.runtime_log_warn_e("Resource loadJSON failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } finally {
            IOUtils.closeQuietly(reader, log);
            IOUtils.closeQuietly(resourceStream, log);
            IOUtils.closeQuietly(resource, log);
        }
    }

    private Node processLoadText(List<Node> argsList, EvaluatorContext context) {
        Node[] args = argsList.toArray((Node[]) Array.newInstance(Node.class, argsList.size()));
        if (ArrayUtils.isEmpty(args)) {
            return Node.UNDEFINED;
        }
        Node arg = args[0];
        if (!arg.isString()) {
            Histone.runtime_log_warn("Non-string path for text resource location: " + arg.getAsString().getValue());
            return Node.UNDEFINED;
        }
        String path = arg.getAsString().getValue();
        String currentBaseURI = getContextBaseURI(context);

        Resource resource = null;
        InputStream resourceStream = null;
        try {
            resource = resourceLoader.load(path, currentBaseURI, args);
            if (resource == null) {
                Histone.runtime_log_warn(String.format("Can't load resource by path = '%s'. Resource was not found.", path));
                return Node.UNDEFINED;
            }
            resourceStream = resource.getInputStream();
            if (resourceStream == null) {
                Histone.runtime_log_warn(String.format("Can't load resource by path = '%s'. Resource is unreadable.", path));
                return Node.UNDEFINED;
            }
            return StringNode.create(resourceStream);
        } catch (ResourceLoadException e) {
            Histone.runtime_log_warn_e("Resource loadText failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (IOException e) {
            Histone.runtime_log_warn_e("Resource loadText failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } finally {
            IOUtils.closeQuietly(resourceStream, log);
            IOUtils.closeQuietly(resource, log);
        }
    }


    private Node processInclude(List<Node> args, EvaluatorContext context) {
        if (args.size() == 0) {
            return Node.UNDEFINED;
        }
        Node uriNode = args.get(0);
        if (!uriNode.isString()) {
            throw new GlobalFunctionExecutionException("Non-string path for template location: " + args.get(0).getAsString().getValue());
        }
        String path = args.get(0).getAsString().getValue();

        Resource resource = null;
        InputStream resourceStream = null;
        try {
            String currentBaseURI = getContextBaseURI(context);
            resource = resourceLoader.load(path, currentBaseURI);
            if (resource == null) {
                Histone.runtime_log_warn("Can't include resource by path = '{}'. Resource was not found.", path);
                return Node.UNDEFINED;
            }
            resourceStream = resource.getInputStream();
            if (resourceStream == null) {
                Histone.runtime_log_warn("Can't include resource by path = '{}'. Resource is unreadable", path);
                return Node.UNDEFINED;
            }
            String templateContent = IOUtils.toString(resourceStream); //yeah... full file reading, because of our tokenizer is regexp-based :(
            JsonArray parseResult = parser.parse(templateContent);
            GlobalObjectNode globalCopy = new GlobalObjectNode(global);
            URI resourceUri = (resource.getBaseHref() != null) ? URI.create(resource.getBaseHref()) : null;
            if (resourceUri != null && resourceUri.isAbsolute() && !resourceUri.isOpaque()) {
                globalCopy.add(GlobalProperty.BASE_URI.getName(), StringNode.create(resourceUri.toString()));
            }
            if (args.size() <= 1) {
                String includeOutput = processInternal(parseResult, EvaluatorContext.createEmpty(globalCopy));
                return StringNode.create(includeOutput);
            }
            return StringNode.create(processInternal(parseResult, EvaluatorContext.createFromJson(globalCopy, args.get(1).getAsJsonElement())));
        } catch (ResourceLoadException e) {
            Histone.runtime_log_warn_e("Resource include failed! Unresolvable resource.", e);
            return Node.UNDEFINED;
        } catch (IOException e) {
            Histone.runtime_log_warn_e("Resource include failed! Resource reading error.", e);
            return Node.UNDEFINED;
        } catch (ParserException e) {
            Histone.runtime_log_warn_e("Resource include failed! Resource parsing error.", e);
            return Node.UNDEFINED;
        } catch (EvaluatorException e) {
            Histone.runtime_log_warn_e("Resource include failed! Resource evaluation error.", e);
            return Node.UNDEFINED;
        } finally {
            IOUtils.closeQuietly(resourceStream, log);
            IOUtils.closeQuietly(resource, log);
        }
    }

    private Node runNodeFunc(Node targetNode, String name, List<Node> args) throws EvaluatorException {
        try {
            return nodeFunctionsManager.execute(targetNode, name, args.toArray((Node[]) Array.newInstance(Node.class, args.size())));
        } catch (NodeFunctionExecutionException e) {
            Histone.runtime_log_warn_e("Node function '{}' execution on node '{}' failed!", e, name, targetNode.getAsString());
            return Node.UNDEFINED;
        }
    }

    private Node runGlobalFunc(String name, List<Node> args) throws EvaluatorException {
        try {
            return globalFunctionsManager.execute(name, args.toArray((Node[]) Array.newInstance(Node.class, args.size())));
        } catch (GlobalFunctionExecutionException e) {
            Histone.runtime_log_warn("Global function '%s' execution failed!", e, name);
            return Node.UNDEFINED;
        }
    }

    private Node processVar(JsonPrimitive ident, JsonArray expression, EvaluatorContext context) throws EvaluatorException {
        log.debug("processVar(): ident={}, expression={}, context={}", new Object[]{ident, expression, context});

        Node exprNode = processNode(expression, context);
        context.putProp(ident.getAsString(), exprNode);
        return Node.UNDEFINED;
    }

    private Node processStatements(JsonElement jsonElement, EvaluatorContext context) throws EvaluatorException {
        log.debug("processStatements(): jsonElement={}, context={}", new Object[]{jsonElement, context});

        return StringNode.create(processInternal(jsonElement, context));
    }

    private Node processFor(JsonArray iterator, JsonArray collection, JsonArray statements, EvaluatorContext context) throws EvaluatorException {
        log.debug("processFor(): iterator={}, collection={}, statements={}, context={}", new Object[]{iterator, collection, statements, context});

        StringBuilder sb = new StringBuilder();

        String iterVal = iterator.get(0).getAsString();
        String iterKey = (iterator.size() > 1) ? iterator.get(1).getAsString() : null;


        Node collectionNode = processNode(collection.getAsJsonArray(), context);

        ObjectNode self = ObjectNode.create();
        context.putProp("self", self);

        // Save context state
        context.saveState();

        if (collectionNode.isObject()) {
            int idx = 0;
            self.add("last", NumberNode.create(collectionNode.getAsObject().size() - 1));
            Map<Object, Node> elements = collectionNode.getAsObject().getElements();
            for (Object key : elements.keySet()) {
                self.add("index", NumberNode.create(idx));

                context.putProp(iterVal, elements.get(key));
                if (iterKey != null) {
                    context.putProp(iterKey, StringNode.create(key.toString()));
                }

                sb.append(processInternal(statements.get(0), context));

                idx++;
            }
        } else if (statements.size() > 1) {
            String result = processInternal(statements.get(1), context);
            sb.append(result);
        }

        context.restoreState();

        return StringNode.create(sb.toString());
    }

    private Node processIf(JsonArray conditions, EvaluatorContext context) throws EvaluatorException {
        log.debug("processIf(): conditions={} context={}", new Object[]{conditions, context});

        StringNode result = StringNode.create();

        context.saveState();

        for (JsonElement condition : conditions) {
            Node conditionResult = processNode(condition.getAsJsonArray().get(0).getAsJsonArray(), context);

            if (conditionResult.getAsBoolean().getValue()) {
                result = StringNode.create(processInternal(condition.getAsJsonArray().get(1), context));
                break;
            }
        }

        context.restoreState();

        return result;
    }

    private Node processTernary(JsonArray condition, JsonArray trueNode, JsonArray falseNode, EvaluatorContext context) throws EvaluatorException {
        log.debug("processTernary(): conditions={}, trueNode={}, falseNode={}, context={}", new Object[]{condition, trueNode, falseNode, context});

        Node conditionResult = processNode(condition, context);
        Node result;

        if (conditionResult.getAsBoolean().getValue()) {
            result = processNode(trueNode, context);
        } else if (falseNode != null) {
            result = processNode(falseNode, context);
        } else {
            result = Node.UNDEFINED;
        }

        return result;
    }

    private Node processGreaterThan(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processGreaterThan(): left={}, right={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processGreaterThan(): left={}, right={}, context={}", new Object[]{left, right, context});

        return left.oper_greaterThan(right);
    }

    private Node processGreaterOrEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processGreaterOrEqual(): left={}, right={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processGreaterOrEqual(): left={}, right={}, context={}", new Object[]{left, right, context});

        return left.oper_greaterOrEqual(right);
    }

    private Node processLessThan(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processLessThan(): left={}, right={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processLessThan(): left={}, right={}, context={}", new Object[]{left, right, context});

        return left.oper_lessThan(right);
    }

    private Node processLessOrEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processLessOrEqual(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processLessOrEqual(): left={}, right={}, context={}", new Object[]{left, right, context});

        return left.oper_lessOrEqual(right);
    }

    private Node processNotEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processNotEqual(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processNotEqual(): left={}, right={}, context={}", new Object[]{left, right, context});

        return left.oper_notEqual(right);
    }

    private Node processEqual(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processEqual(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        log.trace("processEqual(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft.getClass(), nodeRight.getClass(), context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processEqual(): nodeLeft={}, nodeRight={}, context={}", new Object[]{left.getClass(), right.getClass(), context});

        return left.oper_equal(right);
    }

    private Node processNot(JsonArray node, EvaluatorContext context) throws EvaluatorException {
        log.debug("processNot(): node={}, context={}", new Object[]{node, context});
        Node left = processNode(node, context);
        log.trace("processNot(): node={}, context={}", new Object[]{node.getClass(), context});

        return left.oper_not();
    }

    private Node processAnd(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("pricessAnd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        log.trace("pricessAnd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft.getClass(), nodeRight.getClass(), context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("pricessAnd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{left.getClass(), right.getClass(), context});

        return left.oper_and(right);
    }

    private Node processOr(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("pricessOr(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        log.trace("pricessOr(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft.getClass(), nodeRight.getClass(), context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("pricessOr(): nodeLeft={}, nodeRight={}, context={}", new Object[]{left.getClass(), right.getClass(), context});

        return left.oper_or(right);
    }

    private Node processAdd(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processAdd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});

        log.trace("processAdd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft.getClass(), nodeRight.getClass(), context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);
        log.trace("processAdd(): nodeLeft={}, nodeRight={}, context={}", new Object[]{left.getClass(), right.getClass(), context});

        return left.oper_add(right);
    }

    private Node processSub(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processSub(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);

        return left.oper_sub(right);
    }

    private Node processMul(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processMul(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);

        return left.oper_mul(right);
    }

    private Node processDiv(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processDiv(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);

        return left.oper_div(right);
    }

    private Node processMod(JsonArray nodeLeft, JsonArray nodeRight, EvaluatorContext context) throws EvaluatorException {
        log.debug("processMod(): nodeLeft={}, nodeRight={}, context={}", new Object[]{nodeLeft, nodeRight, context});
        Node left = processNode(nodeLeft, context);
        Node right = processNode(nodeRight, context);

        return left.oper_mod(right);
    }

    private Node processNegate(JsonArray node, EvaluatorContext context) throws EvaluatorException {
        log.debug("processNegate(): node={}, context={}", new Object[]{node, context});
        Node left = processNode(node, context);
        return left.oper_negate();
    }

    private Node processMap(JsonArray element, EvaluatorContext context) throws EvaluatorException {
        log.debug("processMap(): element={}, context={}", new Object[]{element, context});
        ObjectNode result = ObjectNode.create();

        for (JsonElement item : element) {
            JsonElement key = item.getAsJsonArray().get(0);
            if (key.isJsonNull()) {
                result.add(processNode(item.getAsJsonArray().get(1).getAsJsonArray(), context));
            } else {
                if (key.getAsJsonPrimitive().isNumber()) {
                    result.add(key.getAsInt(), processNode(item.getAsJsonArray().get(1).getAsJsonArray(), context));
                } else if (key.getAsJsonPrimitive().isString()) {
                    result.add(key.getAsString(), processNode(item.getAsJsonArray().get(1).getAsJsonArray(), context));
                }
            }
        }

        return result;
    }

    private Node processSelector(JsonArray element, EvaluatorContext context) throws EvaluatorException {
        log.debug("processSelector(): element={}, context={}", new Object[]{element, context});

        int startIdx = 0;
        Node ctx;
        if (element.get(0).isJsonArray()) {
            ctx = processNode(element.get(startIdx++).getAsJsonArray(), context);
        } else if ("this".equals(element.get(0).getAsString())) {
            ctx = context.getInitialContext();
            startIdx++;
        } else if ("global".equals(element.get(0).getAsString())) {
            ctx = context.getGlobal();
            startIdx++;
        } else {
            ctx = context.getAsNode();
        }

        for (int j = startIdx; j < element.size(); j++) {
            JsonElement selector = element.get(j);
            String propName;

            if (selector.isJsonPrimitive() && selector.getAsJsonPrimitive().isString()) {
                // selector is written as it is
                propName = selector.getAsString();
            } else {
                // selector is written inside ['..'], like access to array
                propName = processNode(selector.getAsJsonArray(), context).getAsString().getValue();
            }

            if (ctx.hasProp(propName)) {
                ctx = ctx.getProp(propName);
            } else {
                Histone.runtime_log_warn("Selector: in selector '{}' object '{}' doesn't have property '{}'", element, ctx, propName);
                ctx = null;
                break;
            }

            // }
        }

        Node result;

        if (ctx == null) {
            Histone.runtime_log_warn("Property value was null, returning 'undefined()'");
            result = Node.UNDEFINED;
        } else {
            result = ctx;
        }

        log.debug("processSelector(): result={}", new Object[]{result});

        return result;
    }
}
