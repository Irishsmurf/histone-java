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
package ru.histone.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

/**
 * AST node factory<br/>
 * Creates AST nodes based on node type and node items
 */
public abstract class AstNodeFactory {

    /**
     * Create AST node using node type and node items
     *
     * @param type  node type
     * @param items items to put into node
     * @return AST node
     */
    public static JsonArray createNode(int type, Object... items) {
        JsonArray result = new JsonArray();
        result.add(new JsonPrimitive(type));

        for (Object item : items) {
            if (item instanceof String) {
                result.add(new JsonPrimitive((String) item));
            } else if (item instanceof Number) {
                result.add(new JsonPrimitive((Number) item));
            } else if (item instanceof JsonElement) {
                result.add((JsonElement) item);
            } else if (item == null) {
                result.add(JsonNull.INSTANCE);
            }
        }
        return result;
    }

    /**
     * Create JSON array using specified items
     *
     * @param items items to pout into array
     * @return JSON array
     */
    public static JsonArray createArray(Object... items) {
        JsonArray result = new JsonArray();

        for (Object item : items) {
            if (item instanceof String) {
                result.add(new JsonPrimitive((String) item));
            } else if (item instanceof Number) {
                result.add(new JsonPrimitive((Number) item));
            } else if (item instanceof JsonElement) {
                result.add((JsonElement) item);
            } else if (item == null) {
                result.add(JsonNull.INSTANCE);
            }
        }
        return result;
    }


}
