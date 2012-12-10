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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OptimizerContext {
    private Deque<Set<String>> stacksVars;
    private Deque<Map<String, Set<String>>> stacksMacros;

    public OptimizerContext() {
        this.stacksVars = new ArrayDeque<Set<String>>();
        this.stacksMacros = new ArrayDeque<Map<String, Set<String>>>();
        save();
    }

    public void save() {
        stacksVars.push(new HashSet<String>());
        stacksMacros.push(new HashMap<String, Set<String>>());
    }

    public void restore() {
        if (stacksVars.size() == 0) {
            throw new RuntimeException("Can't restore, when saveState wasn't run");
        }
        if (stacksMacros.size() == 0) {
            throw new RuntimeException("Can't restore, when saveState wasn't run");
        }
        stacksVars.pollFirst();
        stacksMacros.pollFirst();
    }

    public boolean isVarSafe(String name) {
        for (Set<String> stack : stacksVars) {
            if (stack.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public void addSafeVar(String name) {
        stacksVars.getFirst().add(name);
    }

    public boolean isMacroSafe(String name) {
        for (Map<String, Set<String>> stack : stacksMacros) {
            if (stack.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public void addSafeMacro(String name, Set<String> argNames) {
        stacksMacros.getFirst().put(name, argNames);
    }
}
