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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.NumberHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * Resize given array
 */
public class Resize extends NodeFunction<ObjectHistoneNode> {

    public Resize(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "resize";
    }

    @Override
	public Node execute(ObjectHistoneNode target, Node... args) {
		if (args.length != 1)
			return getNodeFactory().UNDEFINED;
		BigDecimal newSize = null;
		if (args[0].isNumber()) {
			newSize = args[0].getAsNumber().getValue();
		} else if (args[0].isString()) {
			try {
				newSize = new BigDecimal(args[0].getAsString().getValue());
			} catch (Exception e) {
				// if wrong format then no resize
			}
		}
		if (newSize == null || newSize.intValue() < 0) {
			return getNodeFactory().object(target.getElements().values());
		}

		Iterator<Node> it = target.getElements().values().iterator();
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < newSize.intValue(); i++) {
			Node node;
			if (it.hasNext()) {
				node = it.next();
			} else {
				node = getNodeFactory().number(0);
			}
			nodes.add(node);
		}
		return getNodeFactory().object(nodes);
	}
}
