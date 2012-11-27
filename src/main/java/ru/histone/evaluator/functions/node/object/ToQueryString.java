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

import java.util.Map.Entry;
import java.util.Set;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.ObjectHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 * Transforms key to parameter map to query string. <br/>
 */
public class ToQueryString extends NodeFunction<ObjectHistoneNode> {

    public ToQueryString(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "toQueryString";
    }

    @Override
	public Node execute(ObjectHistoneNode target, Node... args) {
		Set<Entry<Object, Node>> entries = target.getElements().entrySet();
		StringBuilder b = new StringBuilder();
		for (Entry<Object, Node> entry : entries) {
			final String key = entry.getKey().toString();
			final String value = entry.getValue().getAsString().getValue();
			b.append("&").append(key).append("=").append(value);
		}
		String result = entries.size() == 0 ? "" : b.substring(1);
		return getNodeFactory().string(result);
	}
}
