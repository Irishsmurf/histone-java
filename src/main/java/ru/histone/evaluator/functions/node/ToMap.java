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
package ru.histone.evaluator.functions.node;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * If current target is array, then return this array, in other cases - return array containing only current target as it's single element
 */
public class ToMap extends NodeFunction<Node> {
    public ToMap(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "toMap";
    }

    @Override
    public Node execute(Node target, Node... args) {
        if (target.isObject()) {
            return target;
        } else {
            return getNodeFactory().object(target);
        }
    }
}
