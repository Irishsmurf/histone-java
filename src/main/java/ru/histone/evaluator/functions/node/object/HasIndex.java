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
package ru.histone.evaluator.functions.node.object;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * Checks if array has element with specified index<br/>
 * This is the same as 'array.size()&lt;idx'
 */
public class HasIndex extends NodeFunction<ObjectHistoneNode> {

    public HasIndex(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "hasIndex";
    }

    @Override
    public Node execute(ObjectHistoneNode target, Node... args) {
        if (args.length > 0 && args[0].isInteger()) {
            int idx = args[0].getAsNumber().getValue().intValue();
            return (idx >= 0 && idx < target.getElements().size()) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        }

        return getNodeFactory().UNDEFINED;
    }
}
