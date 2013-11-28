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
package ru.histone.evaluator.functions.global;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.utils.PathUtils;

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
        if (args != null && args.length > 1) {
            String result = PathUtils.resolveUrl(args[0].getAsString().getValue(), args[1].getAsString().getValue());
            return getNodeFactory().string(result);
        }
        return getNodeFactory().UNDEFINED;
    }
}
