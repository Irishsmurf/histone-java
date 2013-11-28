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
package ru.histone.evaluator.functions.node.object;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

import java.util.Map;

/**
 * Return slice from current array<br/>
 * Slice is specified by start and end indexes. They could have negative values, and it will mean number of elements from opposite side.
 */
public class Slice extends NodeFunction<ObjectHistoneNode> {

    public Slice(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "slice";
    }

    @Override
    public Node execute(ObjectHistoneNode target, Node... args) {
        if (args.length == 0) {
            return target;
        }

        Node start = args[0];
        if (!start.isInteger()) {
            return getNodeFactory().UNDEFINED;
        }


        int startIdx = start.getAsNumber().getValue().intValue();
        Map<Object, Node> elements = target.getElements();
        int elementsSize = elements.size();
        int stopIdx = elementsSize;

        if (args.length > 1 && args[1].isInteger()) {
            stopIdx = args[1].getAsNumber().getValue().intValue();
        }


        if (startIdx < 0) {
            startIdx = elementsSize + startIdx;
        }
        if (startIdx < 0) {
            startIdx = 0;
        }

        if (stopIdx < 0) {
            stopIdx = elementsSize + stopIdx;
        } else {
            stopIdx = startIdx + stopIdx;
        }

        if (stopIdx < 0) {
            stopIdx = 0;
        }

        if (stopIdx > elementsSize) {
            stopIdx = elementsSize;
        }

        if (startIdx >= stopIdx) {
            return getNodeFactory().object();
        } else {

            if (startIdx < 0) {
                startIdx = 0;
            }

            if (stopIdx < 0) {
                stopIdx = 0;
            }

            ObjectHistoneNode result = getNodeFactory().object();
            int currentIdx = 0;
            for (Object key : elements.keySet()) {
                if ((currentIdx >= startIdx) && (currentIdx < stopIdx)) {
                    result.add(key, elements.get(key));
                }
                currentIdx++;
            }
            return result;
        }
    }

}
