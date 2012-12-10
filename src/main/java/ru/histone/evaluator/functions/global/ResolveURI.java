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
package ru.histone.evaluator.functions.global;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Returns the URI to be resolved against given URI <br/>
 */
public class ResolveURI extends GlobalFunction {
    public ResolveURI(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "resolveURI";
    }

    @Override
    public Node execute(Node... args) throws GlobalFunctionExecutionException {
		String resolved = args[0].getAsString().getValue();
		String against = args[1].getAsString().getValue();
        String result = null;
    	try {
			URI uri = new URI(against);
			result = uri.resolve(resolved).toString();
		} catch (URISyntaxException e) {
			throw new GlobalFunctionExecutionException("resolveURI function execution error", e);
		}
		return getNodeFactory().string(result);
    }
}
