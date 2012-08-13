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
package ru.histone.evaluator.functions.node.string;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NumberNode;
import ru.histone.evaluator.nodes.StringNode;

/**
 * Return ASCII char code of target value for specified char index
 */
public class CharCodeAt implements NodeFunction<StringNode> {

    @Override
    public String getName() {
        return "charCodeAt";
    }

    @Override
    public Node execute(StringNode target, Node... args) {
        if (args.length > 0 && args[0].isInteger()) {

            int charIdx = args[0].getAsNumber().getValue().intValue();
            String value = target.getValue();
            if (charIdx >= 0 && charIdx < value.length()) {
                return NumberNode.create(value.codePointAt(charIdx));
            }

        }

        return Node.UNDEFINED;
    }
}
