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

import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.histone.evaluator.MacroFunc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sazonovkirill@gmail.com
 */
public class InlineOptimizerContext {
    private Deque<Map<String, ArrayNode>> stacksVar;
    private Deque<Map<String, MacroFunc>> stacksMacro;

    public InlineOptimizerContext() {
        this.stacksMacro = new ArrayDeque<Map<String, MacroFunc>>();
        this.stacksVar = new ArrayDeque<Map<String, ArrayNode>>();
        saveState();
    }


    public void saveState() {
        stacksMacro.push(new HashMap<String, MacroFunc>());
        stacksVar.push(new HashMap<String, ArrayNode>());
    }

    public void restoreState() {
        if (stacksMacro.size() == 0 || stacksVar.size() == 0) {
            throw new RuntimeException("Can't restore, when saveState wasn't run");
        }
        stacksMacro.pollFirst();
        stacksVar.pollFirst();
    }

    public MacroFunc getMacro(String name) {
        for (Map<String, MacroFunc> stack : stacksMacro) {
            if (stack.containsKey(name)) {
                return stack.get(name);
            }
        }

        return null;
    }

    public boolean hasMacro(String name) {
        return getMacro(name) != null;
    }

    public void putMacro(String name, MacroFunc macro) {
        stacksMacro.getFirst().put(name, macro);
    }


    public ArrayNode getVar(String name) {
        for (Map<String, ArrayNode> stack : stacksVar) {
            if (stack.containsKey(name)) {
                return stack.get(name);
            }
        }

        return null;
    }

    public boolean hasVar(String name) {
        return getVar(name) != null;
    }

    public void putVar(String name, ArrayNode varAst) {
        stacksVar.getFirst().put(name, varAst);
    }
}
