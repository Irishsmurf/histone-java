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

import com.google.gson.Gson;
import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.StringNode;

/**
 * Convert current target to JSON string
 */
public class ToJson implements NodeFunction<Node> {
    private Gson gson;

    public ToJson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String getName() {
        return "toJSON";
    }

    @Override
    public Node execute(Node target, Node... args) {
        String json = gson.toJson(target.getAsJsonElement());
        return StringNode.create(json);
    }
}
