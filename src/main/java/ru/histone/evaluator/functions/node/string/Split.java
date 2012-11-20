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

import java.util.regex.Pattern;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 * Split target value into array using specified separator<br/>
 * If separator isn't specified, then space symbol will be used instead.
 */
public class Split extends NodeFunction<StringHistoneNode> {

    public Split(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "split";
    }

    @Override
    public Node execute(StringHistoneNode target, Node... args) {
        String value = target.getValue();
        if (args.length > 0 && args[0].isString()) {
            String separator = args[0].getAsString().getValue();
            return getNodeFactory().object(split(value, separator));
        } else {
            return getNodeFactory().object(split(value, ""));
        }

    }

    private String[] split(String value, String separator) {
        String result[] = null;

        if (separator.isEmpty()) {
            result = new String[value.length()];
            for (int i = 0; i < value.length(); i++) {
                result[i] = String.valueOf(value.charAt(i));
            }
        } else {
            result = value.split(Pattern.quote(separator));
        }

        return result;
    }

}
