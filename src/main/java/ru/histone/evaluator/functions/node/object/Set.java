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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * Resize given array
 */
public class Set extends NodeFunction<ObjectHistoneNode> {

    public Set(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
	public Node execute(ObjectHistoneNode target, Node... args) {
		if (args.length != 2)
			return getNodeFactory().UNDEFINED;
		ObjectHistoneNode result = getNodeFactory().object();
		//we can add only string or number
		String key = null;
		if (args[0].isNumber()) 
			key = args[0].getAsNumber().getValue().toPlainString();
		else if (args[0].isString()) 
			key = args[0].getAsString().getValue();
		if (key != null && !args[1].isUndefined())
			result.add(key, args[1]);
		return result;
	}
    
    
}
