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
package ru.histone.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Iterator;

/**
 * Googls JSON library additional utils
 */
public class GsonUtils {

    /**
     * Removes last element from JsonArray and returns it
     *
     * @param arr gson array to use
     * @return last element from array or null if array is empty
     */
    public static JsonElement removeLast(JsonArray arr) {
        Iterator<JsonElement> iter = arr.iterator();
        JsonElement result = null;
        while (iter.hasNext()) {
            result = iter.next();
        }
        iter.remove();
        return result;
    }

    /**
     * Removes first element from JsonArray and returns it
     *
     * @param arr gson array to use
     * @return first element from array or null if array is empty
     */
    public static JsonElement removeFirst(JsonArray arr) {
        Iterator<JsonElement> iter = arr.iterator();
        JsonElement result = null;
        iter.remove();
        while (iter.hasNext()) {
            result = iter.next();
        }
        return result;
    }

}
