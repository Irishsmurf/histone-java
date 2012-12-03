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
package ru.histone.evaluator.functions.node;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.Set;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 * Transforms to  boolean. If node is number not equal 0 then return TRUE. <br/>
 */
public class ToBoolean extends NodeFunction<Node> {
	public ToBoolean(NodeFactory nodeFactory) {
		super(nodeFactory);
	}

	@Override
	public String getName() {
		return "toBoolean";
	}

	@Override
	public Node execute(Node target, Node... args) {
		if (target.isNumber()) {
			if (!BigDecimal.ZERO.equals(target.getAsNumber().getValue())) {
				return getNodeFactory().TRUE;
			} else {
				return getNodeFactory().FALSE;
			}
		} else if (target.isBoolean()) {
			if (target.getAsBoolean().getValue()) {
				return getNodeFactory().TRUE;
			} else {
				return getNodeFactory().FALSE;
			}
		} else if (target.isString()) {
			if ("".equals(target.getAsString().getValue())) {
				return getNodeFactory().FALSE;
			} else {
				return getNodeFactory().TRUE;
			}
		} else if (target.isObject()) {
			return getNodeFactory().TRUE;
		} else if (target.isNull() || target.isUndefined()) {
			return getNodeFactory().FALSE;
		}
		return getNodeFactory().UNDEFINED;
	}
}
