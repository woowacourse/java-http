package org.apache.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PureJavaApiReflectionLoader implements ReflectionLoader {

    public Set<Class<?>> getClassesFromBasePackage(final String basePackage) {
        final Set<Class<?>> classes = new HashSet<>();
        addClasses(basePackage, classes);
        return classes;
    }

    private void addClasses(final String packageName, final Set<Class<?>> classes) {
        final Set<String> lines = readLinesFromPackage(packageName);

        lines.forEach(line -> {
            if (line.endsWith(".class")) {
                classes.add(
                        readClass(packageName, line)
                );
                return;
            }

            addClasses(packageName + "." + line, classes);
        });
    }

    private Class<?> readClass(final String packageName, final String classWithExtension) {
        try {
            return Class.forName(
                    packageName + "." + classWithExtension.substring(0, classWithExtension.lastIndexOf('.'))
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("패키지 경로를 읽는데 문제가 생겼습니다.");
        }
    }

    private Set<String> readLinesFromPackage(final String packageName) {
        try (final InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", File.separator));
             final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("I/O Stream에 문제가 있습니다!", e);
        }
    }
}
