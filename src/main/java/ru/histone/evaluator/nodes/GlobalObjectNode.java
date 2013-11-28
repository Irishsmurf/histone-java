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
package ru.histone.evaluator.nodes;

/**
 * Special class representing 'global' object in Histone syntax
 */
public class GlobalObjectNode extends ObjectHistoneNode {
    public GlobalObjectNode(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    public GlobalObjectNode(NodeFactory nodeFactory, ObjectHistoneNode node) {
        super(nodeFactory);
        if (node != null) {
            for (Object key : node.getElements().keySet()) {
                this.add(key, node.getElements().get(key));
            }
        }
    }

    @Override
    public StringHistoneNode getAsString() {
        return getNodeFactory().string();
    }

    @Override
    public boolean isGlobalObject() {
        return true;
    }
}
