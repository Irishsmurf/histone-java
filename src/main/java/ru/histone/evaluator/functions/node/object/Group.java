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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group extends NodeFunction<ObjectHistoneNode> {

    public Group(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public Node execute(ObjectHistoneNode target, Node... args) {
        ObjectHistoneNode result = getNodeFactory().object();
        if (args.length > 0) {
            if (!args[0].isString()) {
                return result;
            }
            String column = args[0].getAsString().getValue();

            Map<Object, Node> map = target.getElements();


            for (Map.Entry<Object, Node> entry : map.entrySet()) {
                Node val = entry.getValue().getProp(column);
                String key = val.getAsString().getValue();
                if (!result.hasProp(val.getAsString().getValue())) {
                    result.set(key, getNodeFactory().object());
                }
                result.getProp(key).getAsObject().add(entry.getValue());
            }
        }
        return result;
    }
}
