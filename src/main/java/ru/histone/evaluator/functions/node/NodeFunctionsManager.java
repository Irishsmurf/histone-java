/**
 *    Copyright 2013 MegaFon
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
package ru.histone.evaluator.functions.node;

import ru.histone.evaluator.nodes.Node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all type functions
 */
public class NodeFunctionsManager {
    private final ConcurrentHashMap<Class<? extends Node>, ConcurrentHashMap<String, NodeFunction>> nodesFunctions;


    /**
     * Create function manager without any registered functions
     */
    public NodeFunctionsManager() {
        this.nodesFunctions = new ConcurrentHashMap<Class<? extends Node>, ConcurrentHashMap<String, NodeFunction>>();
    }

    /**
     * Create function manager and registers function from specified Map
     */
    public NodeFunctionsManager(Map<Class<? extends Node>, ConcurrentHashMap<String, NodeFunction>> functionsMap) {
        this();
        for (Class<? extends Node> nodeClass : functionsMap.keySet()) {
            this.nodesFunctions.putIfAbsent(nodeClass, new ConcurrentHashMap<String, NodeFunction>());
            this.nodesFunctions.get(nodeClass).putAll(functionsMap.get(nodeClass));
        }
    }

    /**
     * Register new function in function manager<br/>
     * If function with same type and name exists it will be overwritten<br/>
     * This method is used from internal code of evaluator. it's purpose is to force some predefined Histone functions registration
     *
     * @param function function object
     */
    public void registerFunction(Class<? extends Node> nodeClass, NodeFunction function) {
        // make sure we have non-null map for specified nodeClass
        this.nodesFunctions.putIfAbsent(nodeClass, new ConcurrentHashMap<String, NodeFunction>());

        // store function
        this.nodesFunctions.get(nodeClass).put(function.getName(), function);
    }

    /**
     * Same as {@link #registerFunction(Class, NodeFunction)} but will not override any functions
     *
     * @param function function object
     */
    public void registerBuiltInFunction(Class<? extends Node> nodeClass, NodeFunction<? extends Node> function) {
        if (!hasFunction(nodeClass, function.getName())) {
            registerFunction(nodeClass, function);
        }
    }

    /**
     * Return if function with specified name is registered in function manager
     *
     * @param node node object (funciton will be chekced against node object class)
     * @param name function name
     * @return true if function with such name registered
     */
    public boolean hasFunction(Node node, String name) {
        return hasFunction(node.getClass(), name);
    }

    /**
     * Return if function with specified name is registered in function manager
     *
     * @param nodeClass node type to check function for
     * @param name      function name
     * @return true if function with such name registered
     */
    public boolean hasFunction(Class<? extends Node> nodeClass, String name) {
        return getFunction(nodeClass, name) != null;
    }

    public boolean isFunctionSafe(Class<? extends Node> nodeClass, String name) {
        return getFunction(nodeClass, name).isSafe();
    }

    /**
     * Execute function with specified name and arguments on specified target<br/>
     *
     * @param node target to execute function on
     * @param name function name
     * @param args arguments values passed to function
     * @return function result
     * @throws NodeFunctionExecutionException
     *          if no such function registered, or exception occurs during function execution
     */
    @SuppressWarnings("unchecked")
    public Node execute(Node node, String name, Node... args) throws NodeFunctionExecutionException {
        Class<? extends Node> nodeClass = node.getClass();
        NodeFunction function = getFunction(nodeClass, name);
        if (function == null) {
            throw new NodeFunctionExecutionException(String.format("No NodeFunction '%s' found for node class '%s'", name, nodeClass.getSimpleName()));
        }
        Node result = null;

        try {
            result = function.execute(node, args);
        } catch (Exception e) {
            throw new NodeFunctionExecutionException(String.format("Error executing NodeFunction '%s' for node class '%s'", name, nodeClass.getSimpleName()), e);
        }

        return result;
    }

    /**
     * Return function for specified node class and name<br/>
     * If there is no such function, then check for superclass of specified node class
     * @param nodeClass node class to check
     * @param name function name
     * @return node function object
     */
    private NodeFunction getFunction(Class nodeClass, String name) {
        if (!Node.class.isAssignableFrom(nodeClass)) {
            return null;
        }
        ConcurrentHashMap<String, NodeFunction> nodeFunctions = nodesFunctions.get(nodeClass);
        if (nodeFunctions == null) {
            return getFunction(nodeClass.getSuperclass(), name);
        }
        NodeFunction function = nodeFunctions.get(name);
        if (function == null) {

            return getFunction(nodeClass.getSuperclass(), name);
        }
        return function;
    }
}
