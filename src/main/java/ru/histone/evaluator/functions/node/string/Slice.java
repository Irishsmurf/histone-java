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
import ru.histone.evaluator.nodes.StringNode;

/**
 * Return substring of target string<br/>
 * Substring borders are specified by start and stop indexes. They could have negative values, and it will mean number of elements from opposite side.
 */
public class Slice implements NodeFunction<StringNode> {

    @Override
    public String getName() {
        return "slice";
    }

    @Override
    public Node execute(StringNode target, Node... args) {
        if (args.length > 0 && args[0].isInteger()) {
            int startIdx = args[0].getAsNumber().getValue().intValue();
            String value = target.getValue();
            int stopIdx = value.length();

            if (args.length > 1 && args[1].isInteger()) {
                stopIdx = args[1].getAsNumber().getValue().intValue();
            }

            if (startIdx < 0) {
                startIdx = value.length() + startIdx;
            }
            if (startIdx < 0) {
                startIdx = 0;
            }

            if (stopIdx < 0) {
                stopIdx = value.length() + stopIdx;
            } else {
                stopIdx = startIdx + stopIdx;
            }

            if (stopIdx > value.length()) {
                stopIdx = value.length();
            }

            if (startIdx > stopIdx) {
                return StringNode.create();
            } else {

                if (startIdx < 0) {
                    startIdx = 0;
                }

                if (stopIdx < 0) {
                    stopIdx = 0;
                }

                return StringNode.create(value.substring(startIdx, stopIdx));
            }
        }

        return Node.UNDEFINED;
    }
}