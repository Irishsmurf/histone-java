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

import java.util.Collection;
import java.util.Map;

/**
 * Collection utils
 */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * Checks if collection is null or empty
     *
     * @param source collection to check
     * @return true if collection is null or empty
     */
    public static boolean isEmpty(Collection<?> source) {
        return source == null || source.size() == 0;
    }

    /**
     * Checks if collection is not null nor empty
     *
     * @param source collection to check
     * @return true if collection is not null nor empty
     */
    public static boolean isNotEmpty(Collection<?> source) {
        return !isEmpty(source);
    }

    /**
     * Checks if map is null or empty
     *
     * @param source map to check
     * @return true if map is null or empty
     */
    private static boolean isEmpty(Map source) {
        return source == null || source.isEmpty();
    }

    /**
     * Checks if map is not null or empty
     *
     * @param source map to check
     * @return true if map is not null nor empty
     */
    public static boolean isNotEmpty(Map source) {
        return !isEmpty(source);
    }

    /**
     * Returns collection elements separated by colon symbol followed by space ', '
     *
     * @param source collection to use
     * @return collection elements separated by colon symbol followed by space ', '
     */
    public static String printElements(Collection<?> source) {
        return printElements(source, ", ");
    }

    /**
     * Returns collection elements separated by specified symbol
     *
     * @param source    collection to use
     * @param separator separator to use
     * @return collection elements separated by specififed symbol
     */
    public static String printElements(Collection<?> source, String separator) {
        if (CollectionUtils.isEmpty(source)) {
            return "";
        }
        int sourceLength = source.size();
        StringBuilder result = new StringBuilder(2 * sourceLength - 1);
        boolean addSeparator = false;
        for (Object element : source) {
            if (addSeparator) {
                result.append(separator);
            }
            result.append(element.toString());
            addSeparator = true;
        }
        return result.toString();
    }
}
