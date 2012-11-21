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

import java.math.BigDecimal;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.NumberHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 * Convert target string to number<br/>
 */
public class ToNumber extends NodeFunction<StringHistoneNode> {

    public ToNumber(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "toNumber";
    }

    @Override
    public Node execute(StringHistoneNode target, Node... args) {
        NumberHistoneNode result;
        try {
            BigDecimal v = new BigDecimal(strip(target.getAsString().getValue()));
            result = getNodeFactory().number(v);
        } catch (NumberFormatException nfe) {
            result = getNodeFactory().UNDEFINED_NUMBER;
        }
        return result;
    }

    private String strip(String str) {
        String stripChars = " \r\n\t";

        final int strLen = str.length();

        int start = 0;
        while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
            start++;
        }

        int end = str.length();
        while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
            end--;
        }

        if (start >= end) {
            return "";
        } else {
            return str.substring(start, end);
        }
    }


}
