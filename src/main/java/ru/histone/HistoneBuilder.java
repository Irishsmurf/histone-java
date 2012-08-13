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
package ru.histone;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.EvaluatorBootstrap;
import ru.histone.evaluator.functions.global.GlobalFunction;
import ru.histone.evaluator.functions.global.GlobalFunctionsManager;
import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionsManager;
import ru.histone.evaluator.nodes.BooleanNode;
import ru.histone.evaluator.nodes.GlobalObjectNode;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NullNode;
import ru.histone.evaluator.nodes.NumberNode;
import ru.histone.evaluator.nodes.ObjectNode;
import ru.histone.evaluator.nodes.StringNode;
import ru.histone.optimizer.AstImportResolver;
import ru.histone.optimizer.AstInlineOptimizer;
import ru.histone.optimizer.AstMarker;
import ru.histone.optimizer.AstOptimizer;
import ru.histone.parser.Parser;
import ru.histone.resourceloaders.DefaultResourceLoader;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.tokenizer.TokenizerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class for preparing Histone template engine<br/>
 * <br/>
 * <b>Initiate default Histone engine</b><br/>
 * <code>HistoneBuilder builder = new HistoneBuilder();<br/>
 * Histone histone = builder.build();</code>
 * <br/>
 * <b>Initiate Histone with preconfigured Gson engine</b><br/>
 * <code>HistoneBuilder builder = new HistoneBuilder();<br/>
 * builder.setGson(gson);<br/>
 * Histone histone = builder.build();</code>
 * <br/>
 * <p/>
 * This class is not thread-safe!
 */
public class HistoneBuilder {
    private static final Logger log = LoggerFactory.getLogger(HistoneBuilder.class);

    private Gson gson = new Gson();

    private ConcurrentHashMap<GlobalProperty, Node> globalProperties = new ConcurrentHashMap<GlobalProperty, Node>();
    private ConcurrentHashMap<String, GlobalFunction> globalFunctions = new ConcurrentHashMap<String, GlobalFunction>();
    private ConcurrentHashMap<Class<? extends Node>, ConcurrentHashMap<String, NodeFunction>> nodeFunctions = new ConcurrentHashMap<Class<? extends Node>, ConcurrentHashMap<String, NodeFunction>>();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    public HistoneBuilder() {
    }

    /**
     * Set preconfigured Gson engine<br/>
     * In most cases you don't need to have custom Gson engine, and defaut Histone Gson engine will be enoughe.
     *
     * @param gson
     */
    public void setGson(Gson gson) {
        log.debug("setGson({})", gson);
        this.gson = gson;
    }

    /**
     * Set custom resource loader
     *
     * @param resourceLoader custom resource loader to use
     * @throws IllegalArgumentException if resource loader object is null
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if (resourceLoader == null) {
            throw new IllegalArgumentException("Resource loader object can't be null");
        }
        this.resourceLoader = resourceLoader;
    }

    /**
     * Update all global functions in HistoneBuilder<br/>
     * This method removes all previously added global functions and adds new from specified Set
     *
     * @param globalFunctions Set with global functions to update in HistoneBuilder
     * @throws IllegalArgumentException is thrown if there is at least one global function in the Set and in the HistoneBuilder with the same name
     * @see #addGlobalFunction(GlobalFunction)
     */
    public void setGlobalFunctions(Set<GlobalFunction> globalFunctions) throws HistoneException {
        log.debug("setGlobalFunctions({}({}))", new Object[]{globalFunctions, globalFunctions.size()});
        this.globalFunctions.clear();
        for (GlobalFunction function : globalFunctions) {
            addGlobalFunction(function);
        }
    }

    /**
     * Adds new global function to HistoneBuilder<br/>
     *
     * @param globalFunction global function to add
     * @throws IllegalArgumentException is thrown if HistoneBuilder already has global function with the same name
     * @see #setGlobalFunctions(Set)
     */
    public void addGlobalFunction(GlobalFunction globalFunction) throws HistoneException {
        log.debug("addGlobalFunction({})", new Object[]{globalFunction});
        if (this.globalFunctions.putIfAbsent(globalFunction.getName(), globalFunction) != null) {
            throw new IllegalArgumentException(String.format("GlobalFunction with name '%s' already registered", globalFunction.getName()));
        }
    }

    /**
     * Update all node functions in HistoneBuilder<br/>
     * This method removes all previously added node functions and adds new from specified Set
     *
     * @param nodeFunctions Map with node functions to update in HistoneBuilder
     * @throws IllegalArgumentException is thrown if there is at least one node function in the Map and in the HistoneBuilder with the same name for same node classes
     * @see #addNodeFunction(Class, NodeFunction)
     */
    public void setNodeFunctions(Map<Class<? extends Node>, Set<NodeFunction<? extends Node>>> nodeFunctions) throws HistoneException {
        log.debug("setNodeFunctions({}({}))", new Object[]{nodeFunctions, nodeFunctions.size()});
        this.nodeFunctions.clear();
        for (Map.Entry<Class<? extends Node>, Set<NodeFunction<? extends Node>>> entry : nodeFunctions.entrySet()) {
            Class<? extends Node> nodeClass = entry.getKey();
            for (NodeFunction function : entry.getValue()) {
                addNodeFunction(nodeClass, function);
            }
        }
    }

    /**
     * Adds new node function to HistoneBuilder<br/>
     *
     * @param nodeClass node class to add node function to
     * @param function  node function to add
     * @throws IllegalArgumentException is thrown if HistoneBuilder already has node function with the same name for specified node class
     * @see #setNodeFunctions(Map)
     */
    public void addNodeFunction(Class<? extends Node> nodeClass, NodeFunction function) throws HistoneException {
        this.nodeFunctions.putIfAbsent(nodeClass, new ConcurrentHashMap<String, NodeFunction>());
        if (this.nodeFunctions.get(nodeClass).putIfAbsent(function.getName(), function) != null) {
            throw new IllegalArgumentException(String.format("NodeFunction for class '%s', with name '%s' already registered", nodeClass, function.getName()));
        }
    }

    /**
     * Update specified global property value with String value
     *
     * @param property property name
     * @param value    property value
     */
    public void addGlobalProperty(GlobalProperty property, String value) {
        globalProperties.put(property, StringNode.create(value));
    }

    /**
     * Update specified global property value with Integer value
     *
     * @param property property name
     * @param value    property value
     * @see #addGlobalProperty(GlobalProperty, Long)
     * @see #addGlobalProperty(GlobalProperty, BigDecimal)
     * @see #addGlobalProperty(GlobalProperty, String)
     * @see #addGlobalProperty(GlobalProperty, Boolean)
     * @see #addGlobalProperty(GlobalProperty, JsonElement)
     */
    public void addGlobalProperty(GlobalProperty property, Integer value) {
        globalProperties.put(property, NumberNode.create(value));
    }

    /**
     * Update specified global property value with Long value
     *
     * @param property property name
     * @param value    property value
     * @see #addGlobalProperty(GlobalProperty, Integer)
     * @see #addGlobalProperty(GlobalProperty, BigDecimal)
     * @see #addGlobalProperty(GlobalProperty, String)
     * @see #addGlobalProperty(GlobalProperty, Boolean)
     * @see #addGlobalProperty(GlobalProperty, JsonElement)
     */
    public void addGlobalProperty(GlobalProperty property, Long value) {
        globalProperties.put(property, NumberNode.create(value));
    }

    /**
     * Update specified global property value with BigDecimal value
     *
     * @param property property name
     * @param value    property value
     * @see #addGlobalProperty(GlobalProperty, Integer)
     * @see #addGlobalProperty(GlobalProperty, Long)
     * @see #addGlobalProperty(GlobalProperty, String)
     * @see #addGlobalProperty(GlobalProperty, Boolean)
     * @see #addGlobalProperty(GlobalProperty, JsonElement)
     */
    public void addGlobalProperty(GlobalProperty property, BigDecimal value) {
        globalProperties.put(property, NumberNode.create(value));
    }

    /**
     * Update specified global property value with Boolean value
     *
     * @param property property name
     * @param value    property value
     * @see #addGlobalProperty(GlobalProperty, Integer)
     * @see #addGlobalProperty(GlobalProperty, Long)
     * @see #addGlobalProperty(GlobalProperty, BigDecimal)
     * @see #addGlobalProperty(GlobalProperty, String)
     * @see #addGlobalProperty(GlobalProperty, JsonElement)
     */
    public void addGlobalProperty(GlobalProperty property, Boolean value) {
        globalProperties.put(property, value ? BooleanNode.TRUE : BooleanNode.FALSE);
    }

    /**
     * Update specified global property value with JSON value<br/>
     * Following JSON types are supported:<br/>
     * <li>array</li>
     * <li>object</li>
     * <li>null</li>
     * <li>boolean</li>
     * <li>string</li>
     * <li>number</li>
     *
     * @param property property name
     * @param value    property value
     * @see #addGlobalProperty(GlobalProperty, Integer)
     * @see #addGlobalProperty(GlobalProperty, Long)
     * @see #addGlobalProperty(GlobalProperty, BigDecimal)
     * @see #addGlobalProperty(GlobalProperty, String)
     * @see #addGlobalProperty(GlobalProperty, Boolean)
     */
    public void addGlobalProperty(GlobalProperty property, JsonElement value) {
        if (value.isJsonArray()) {
            globalProperties.put(property, ObjectNode.create(value.getAsJsonArray()));
        } else if (value.isJsonObject()) {
            globalProperties.put(property, ObjectNode.create(value.getAsJsonObject()));
        } else if (value.isJsonNull()) {
            globalProperties.put(property, NullNode.NULL);
        } else if (value.isJsonPrimitive()) {
            JsonPrimitive primitiveValue = value.getAsJsonPrimitive();
            if (primitiveValue.isBoolean()) {
                globalProperties.put(property, value.getAsBoolean() ? BooleanNode.TRUE : BooleanNode.FALSE);
            } else if (primitiveValue.isString()) {
                globalProperties.put(property, StringNode.create(primitiveValue.getAsString()));
            } else if (primitiveValue.isNumber()) {
                globalProperties.put(property, NumberNode.create(primitiveValue.getAsBigDecimal()));
            }
        }
    }

    /**
     * Updates all global properties in HistoneBuilder<br/>
     * This method removes all previously added global properties and adds new from specified Map
     *
     * @param globalProperties Map with global properties
     */
    public void setGlobalProperties(Map<GlobalProperty, String> globalProperties) {
        this.globalProperties.clear();
        for (Map.Entry<GlobalProperty, String> entry : globalProperties.entrySet()) {
            addGlobalProperty(entry.getKey(), entry.getValue());
        }
    }


    /**
     * Builds new Histone engine using current HistoneBuilder settings/resources loaders/etc<br/>
     * This method creates new Histone template engine
     *
     * @return Histone engine instance
     * @throws HistoneException if error during histone initialization occurs
     */
    public Histone build() throws HistoneException {
        log.debug("Building new Histone template engine");

        TokenizerFactory tokenizerFactory = new TokenizerFactory(HistoneTokensHolder.getTokens());
        Parser parser = new Parser(tokenizerFactory);

        EvaluatorBootstrap evaluatorBootstrap = new EvaluatorBootstrap();
        evaluatorBootstrap.setGson(gson);
        evaluatorBootstrap.setParser(parser);
        URI baseURI = extractBaseURI(globalProperties);
        if (baseURI != null) {
            if (!baseURI.isAbsolute()) {
                throw new HistoneException(String.format("baseURI = '%s' and it's not absolute URI!", baseURI.toString()));
            }
            if (baseURI.isOpaque()) {
                throw new HistoneException(String.format("baseURI = '%s' and it's opaque URI!", baseURI.toString()));
            }
        }
        GlobalFunctionsManager globalFunctionsManager = new GlobalFunctionsManager(globalFunctions);
        NodeFunctionsManager nodeFunctionsManager = new NodeFunctionsManager(nodeFunctions);

        evaluatorBootstrap.setResourceLoader(resourceLoader);
        evaluatorBootstrap.setGlobalFunctionsManager(globalFunctionsManager);
        evaluatorBootstrap.setNodeFunctionsManager(nodeFunctionsManager);

        GlobalObjectNode global = new GlobalObjectNode();
        for (Map.Entry<GlobalProperty, Node> entry : globalProperties.entrySet()) {
            global.add(entry.getKey().getName(), entry.getValue());
        }
        evaluatorBootstrap.setGlobal(global);

        Evaluator evaluator = new Evaluator(evaluatorBootstrap);

        AstImportResolver astImportResolver = new AstImportResolver(parser, resourceLoader);

        AstOptimizer astOptimizer = new AstOptimizer(evaluator);
        AstMarker astMarker = new AstMarker(astOptimizer);

        AstInlineOptimizer astInlineOptimizer = new AstInlineOptimizer();


        return new Histone(parser, evaluator, astImportResolver, astMarker, astInlineOptimizer, astOptimizer, gson);
    }

    private URI extractBaseURI(Map<GlobalProperty, Node> globalProperties) throws HistoneException {
        try {
            Node node = globalProperties.get(GlobalProperty.BASE_URI);
            if (node == null) {
                return null;
            }
            return new URI(node.getAsString().getValue()).resolve("");
        } catch (URISyntaxException e) {
            throw new HistoneException(e);
        }
    }

    //those methods are here for testing purposes only
    //we need to refactor classes dependencies in order to have better testable architecture
    protected Gson getGson() {
        return gson;
    }
}
