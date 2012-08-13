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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.StringNode;

/**
 * Check string against Regular Expression
 */
public class Test implements NodeFunction<StringNode> {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Node execute(StringNode target, Node... args) {
        if (args.length > 0) {

            String regexp = args[0].getAsString().getValue();
            Pattern pt = Pattern.compile(regexp);
            Matcher m = pt.matcher(target.getValue());
            return m.find() ? Node.TRUE : Node.FALSE;
        }

        return Node.UNDEFINED;
    }
}
