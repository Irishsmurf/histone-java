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

/**
 * Array utils
 */
public final class ArrayUtils {
    private ArrayUtils() {
    }

    /**
     * Checks if java array is null or empty
     * @param array array to check
     * @return true if array is null or empty
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if java array is not null nor empty
     * @param array array to check
     * @return true if array is not null nor empty
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }
}
