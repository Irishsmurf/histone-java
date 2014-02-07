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
package ru.histone.optimizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

public class ASTTreeElementsCounter {

    public static int count(JsonNode node) {
        int count = 0;

        if (node != null) {
            if (node.isObject()) {
                count = count + 1 + countObject((ObjectNode) node);
            } else if (node.isArray()) {
                count = count + 1 + countArray((ArrayNode) node);
            } else {
                count++;
            }
        }

        return count;
    }

    private static int countArray(ArrayNode node) {
        int count = 0;

        for (int i = 0; i < node.size(); i++) {
            count = count + count(node.get(i));
        }

        return count;
    }

    private static int countObject(ObjectNode node) {
        int count = 0;

        Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            count = count + 1 + count(entry.getValue());
        }

        return count;
    }
}
