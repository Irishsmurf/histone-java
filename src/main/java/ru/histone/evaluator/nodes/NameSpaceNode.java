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
package ru.histone.evaluator.nodes;


import ru.histone.evaluator.MacroFunc;

import java.util.HashMap;
import java.util.Map;

public class NameSpaceNode extends ObjectHistoneNode{

    private Map<NodeKey, MacroFunc> macros = new HashMap<NodeKey, MacroFunc>();

    protected NameSpaceNode(NodeFactory nodeFactory) {
        super(nodeFactory);
    }
    
    
    public void addMacro(String key, MacroFunc value){
        if (key == null) throw new IllegalArgumentException();
        if (value == null) throw new IllegalArgumentException();

        macros.put(new NodeKey(key), value);
    }

    public boolean hasMacro(String name) {
        return getMacro(name) != null;
    }

    /**
     * Return macro object
     *
     * @param name macro name
     * @return macro object
     */
    public MacroFunc getMacro(String name) {
        NodeKey key = new NodeKey(name);
        if (macros.containsKey(key)) {
            return macros.get(key);
        }
        return null;
    }

}
