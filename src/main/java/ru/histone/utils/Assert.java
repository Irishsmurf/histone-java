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
