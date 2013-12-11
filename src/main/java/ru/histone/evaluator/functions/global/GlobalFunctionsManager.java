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
package ru.histone.evaluator.functions.global;

import ru.histone.evaluator.nodes.Node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all global functions
 */
public class GlobalFunctionsManager {
    private final ConcurrentHashMap<String, GlobalFunction> globalFunctions;

    /**
     * Create function manager without any registered functions
     */
    public GlobalFunctionsManager() {
        this.globalFunctions = new ConcurrentHashMap<String, GlobalFunction>();
    }

    /**
     * Create function manager and registers function from specified Map
     */
    public GlobalFunctionsManager(Map<String, GlobalFunction> functionsMap) {
        this();
        this.globalFunctions.putAll(functionsMap);
    }

    /**
     * Register new function in function manager<br/>
     * If function with same name exists it will be overwritten<br/>
     * This method is used from internal code of evaluator. it's purpose is to force some predefined Histone functions registration
     *
     * @param function function object
     */
    public void registerFunction(GlobalFunction function) {
        this.globalFunctions.put(function.getName(), function);
    }

    /**
     * Same as {@link #registerFunction(GlobalFunction)} but will not override any functions
     *
     * @param function function object
     */
    public void registerBuiltInFunction(GlobalFunction function) {
        if (!hasFunction(function.getName())) {
            registerFunction(function);
        }
    }

    /**
     * Return if function with specified name is registered in function manager
     *
     * @param name function name
     * @return true if function with such name registered
     */
    public boolean hasFunction(String name) {
        return globalFunctions.containsKey(name);
    }

    /**
     * Execute function with specified name and arguments<br/>
     *
     * @param name function name
     * @param args arguments values passed to function
     * @return function result
     * @throws GlobalFunctionExecutionException if no such function registered, or exception occurs during function execution
     */
    public Node execute(String name, Node... args) throws GlobalFunctionExecutionException {
        GlobalFunction function = globalFunctions.get(name);
        if (function == null) {
            throw new GlobalFunctionExecutionException(String.format("No GlobalFunction found by name = '%s'", name));
        }

        Node result;
        try {
            result = function.execute(args);
        } catch (GlobalFunctionStopTheWorldException e) {
            throw e;
        } catch (Exception e) {
            throw new GlobalFunctionExecutionException(String.format("GlobalFunction '%s' execution error", name), e);
        }

        return result;
    }
}
