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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implement Map.search() - search for index/key for specified element
 */
public class Search extends NodeFunction<ObjectHistoneNode> {

    public Search(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "search";
    }

    @SuppressWarnings("unchecked")
	@Override
	public Node execute(ObjectHistoneNode target, Node... args) {
		if (args.length != 1 && args.length != 2)
			return getNodeFactory().UNDEFINED;
		final int start = args.length == 1 ? 0 : args[1].getAsNumber().getValue().intValue();
		Object result = null;
		Map<Object, Node> map = target.getElements();
		Entry<Object, Node>[] entries = map.entrySet().toArray(new Entry[0]);
		// search if start index is positive
		if (start >= 0) {
			for (int i = start; i < entries.length; i++) {
				if (isEquals(entries[i].getValue(), args[0])) {
					result = entries[i].getKey();
					break;
				}
			}
		}
		// search if start index is negative
		else {
			for (int i = entries.length + start; i >= 0; i--) {
				if (isEquals(entries[i].getValue(), args[0])) {
					result = entries[i].getKey();
					break;
				}
			}
		}
		//
		if (result instanceof Number)
			return getNodeFactory().number(BigDecimal.valueOf(((Number) result).longValue()));
		else if (result instanceof String)
			return getNodeFactory().string((String) result);
		else
			return getNodeFactory().UNDEFINED;
	}

	private boolean isEquals(Node arrayValue, Node argValue) {
		boolean result = false;
		if (arrayValue.isBoolean() && argValue.isBoolean()) {
			result = argValue.getAsBoolean().getValue().equals(arrayValue.getAsBoolean().getValue());
		}
		else if (arrayValue.isNumber() && argValue.isNumber()) {
			long arrayNumber = arrayValue.getAsNumber().getValue().longValue();
			long argNumber = argValue.getAsNumber().getValue().longValue();
			result = arrayNumber == argNumber;
		}
		else if (arrayValue.isString() && argValue.isString()) {
			result = argValue.getAsString().getValue().equals(arrayValue.getAsString().getValue());
		}
		return result;
	}
}
