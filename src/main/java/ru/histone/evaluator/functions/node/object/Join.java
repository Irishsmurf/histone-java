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
import ru.histone.evaluator.nodes.ObjectNode;
import ru.histone.evaluator.nodes.StringNode;

/**
 * Joins array elements to single string using specified separator<br/>
 * If separator isn't specified, then space symbol will be used instead
 */
public class Join implements NodeFunction<ObjectNode> {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public Node execute(ObjectNode target, Node... args) {
        String separator = "";
        if (args.length > 0) {
            separator = args[0].getAsString().getValue();
        }
        boolean addSeparator = false;
        StringBuilder buffer = new StringBuilder();
        for (Node element : target.getElements().values()) {
            if (addSeparator) {
                buffer.append(separator);
            }
            buffer.append(element.getAsString().getValue());
            addSeparator = true;
        }
        return StringNode.create(buffer.toString());
    }
}
