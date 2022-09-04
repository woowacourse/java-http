package org.apache.support;

import java.util.Set;

public interface ReflectionLoader {

    Set<Class<?>> getClassesFromBasePackage(final String basePackage);
}
