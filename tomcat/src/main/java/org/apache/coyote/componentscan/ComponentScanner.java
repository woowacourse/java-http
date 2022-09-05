package org.apache.coyote.componentscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.Application;

public class ComponentScanner {

    private static final String PACKAGE_SEPARATOR = ".";
    private static final String CLASS_EXTENSION = ".class";
    private static final Integer START_OF_RESOURCE = 0;
    private static final String INIT_BASE_PACKAGE = Application.class.getPackageName();

    public static Set<Class<?>> scan() {
        final Set<Class<?>> classes = new HashSet<>();
        addClass(INIT_BASE_PACKAGE, classes);
        return classes;
    }

    private static Set<Class<?>> addClass(final String basePackageName, final Set<Class<?>> classes) {
        final List<String> resourcesOfPackage = findResourcesOfPackage(basePackageName);
        for (String resource : resourcesOfPackage) {
            if (isClass(resource)) {
                classes.add(findClass(basePackageName, resource));
                continue;
            }
            addClass(makeResourcePath(basePackageName, resource), classes);
        }
        return classes;
    }

    private static boolean isClass(final String resources) {
        return resources.endsWith(CLASS_EXTENSION);
    }

    private static List<String> findResourcesOfPackage(final String basePackageName) {
        try (final InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(basePackageName.replace(PACKAGE_SEPARATOR, File.separator));
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("InputStream을 읽어들이는 과정에서 문제가 생겼습니다.");
        }
    }

    private static Class<?> findClass(final String packageName, final String resources) {
        try {
            return Class.forName(makeResourcePath(
                    packageName,
                    resources.substring(START_OF_RESOURCE, resources.lastIndexOf(PACKAGE_SEPARATOR))));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("해당 경로의 클래스를 찾을 수 없습니다.");
        }
    }

    private static String makeResourcePath(final String basePackageName, final String resources) {
        return basePackageName + PACKAGE_SEPARATOR + resources;
    }
}
