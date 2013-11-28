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
package ru.histone.utils;

import java.util.Collection;

/**
 * User: sazonovkirill@gmail.com
 * Date: 25.12.12
 */
public class Assert {
    public static void notNull(Object o) {
        if (o == null) throw new IllegalArgumentException();
    }

    public static void notNull(Object... objs) {
        if (objs == null) throw new IllegalArgumentException();

        for (Object o : objs) {
            if (o == null) throw new IllegalArgumentException();
        }
    }

    public static void notNull(Object o, String parameterName) {
        if (o == null) throw new IllegalArgumentException(parameterName);
    }

    public static void notBlank(String s) {
        if (StringUtils.isBlank(s)) throw new IllegalArgumentException();
    }

    public static void notBlank(String... ss) {
        if (ss == null) throw new IllegalArgumentException();

        for (String s : ss) {
            if (StringUtils.isBlank(s)) throw new IllegalArgumentException();
        }
    }

    public static void notBlank(String s, String parameterName) {
        if (StringUtils.isBlank(s)) throw new IllegalArgumentException(parameterName);
    }

    public static void isTrue(boolean value) {
        if (!value) throw new AssertException();
    }

    public static void notEmpty(Collection collection) {
        if (collection == null) throw new IllegalArgumentException();
        if (collection.size() == 0) throw new AssertException();
    }
}
