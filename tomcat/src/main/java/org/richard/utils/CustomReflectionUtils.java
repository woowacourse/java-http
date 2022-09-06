package org.richard.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class CustomReflectionUtils {

    private CustomReflectionUtils() {
    }

    public static <T> T newInstance(final Class<? extends T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, R> Function<T, R> runnableByMethod(final Object instance, final Method method) {
        return new Function<T, R>() {
            @Override
            public Object apply(final Object o) {
                try {
                    return (R) (method.invoke(instance, (T) o));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
