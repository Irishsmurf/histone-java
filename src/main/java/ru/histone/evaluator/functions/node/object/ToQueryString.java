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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.Set;

import ru.histone.Histone;
import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionExecutionException;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.ObjectHistoneNode;

/**
 * Transforms key-to-parameter map to query string. <br/>
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
		if (args.length > 2) {
			Histone.runtime_log_warn("Function toQueryString() can have one argument, but you provided '{}' arguments", args.length);
			return getNodeFactory().UNDEFINED;
		}
		final String keyPrefix = args.length > 0 && !args[0].isNull() ? args[0].getAsString().getValue() : null;
		final String separator = args.length > 1 ? args[1].getAsString().getValue() : "&";
		return getNodeFactory().string(toQueryString(target, keyPrefix, separator));
	}

	public static String toQueryString(final ObjectHistoneNode node, final String keyPrefix, final String separator) {
		Set<Entry<Object, Node>> entries = node.getElements().entrySet();
		StringBuilder b = new StringBuilder();
		for (Entry<Object, Node> entry : entries) {
			String key = entry.getKey().toString();
			// if key is integer then it is just a index in array. Add given
			// prefix before
			key = entry.getKey() instanceof Integer && keyPrefix != null ? keyPrefix + key : key;
			addValue(keyPrefix, separator, b, key, entry.getValue());
		}
		String result = entries.size() == 0 ? "" : b.substring(1);
		return result;
	}

	private static void addValue(final String keyPrefix, final String separator, final StringBuilder b, final String key, Node node) {
		if (node.isObject()) {
			Set<Entry<Object, Node>> entries = node.getAsObject().getElements().entrySet();
			for (Entry<Object, Node> entry : entries) {
				String newKey = entry.getKey().toString();
				newKey = key + "[" + newKey + "]";
				addValue(keyPrefix, separator, b, newKey, entry.getValue());
			}
		} else {
			if (node.isUndefined())
				return;
			String value = node.getAsString().getValue();
			try {
				value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
			} catch (UnsupportedEncodingException e) {
				throw new NodeFunctionExecutionException("Can't encode value: " + value, e);
			}
			b.append(separator).append(key).append("=").append(value);
		}
	}
}