package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassScanner {

    private static final String DIRECTORY_DELIMITER = "/";
    private static final String PACKAGE_DELIMITER_REGEX = "[.]";
    private static final String PACKAGE_DELIMITER = ".";
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final char EXTENSION_DELIMITER = '.';
    private final String packagePath;

    public ClassScanner(final String packagePath) {
        this.packagePath = packagePath;
    }

    public <T> Set<T> scanAs(final Class<T> typeToScan) {
        final Set<Class<?>> classes = findAllClassesUsingClassLoader(packagePath, typeToScan);
        return classes.stream()
            .filter(this::hasConstructor)
            .filter(this::hasNoArgumentConstructor)
            .map(this::findNoArgumentConstructor)
            .map(constructor -> {
                try {
                    return typeToScan.cast(constructor.newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException(String.format("Cannot invoke constructor. (%s)", constructor.getName()));
                }
            })
            .collect(Collectors.toSet());
    }

    private boolean hasConstructor(final Class<?> clazz) {
        return clazz.getDeclaredConstructors().length != 0;
    }

    private boolean hasNoArgumentConstructor(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .anyMatch(constructor -> constructor.getParameterTypes().length == 0 && Modifier.isPublic(constructor.getModifiers()));
    }

    private Constructor<?> findNoArgumentConstructor(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterTypes().length == 0)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find no-argument constructor."));
    }

    private <T> Set<Class<?>> findAllClassesUsingClassLoader(String packageName, Class<T> typeToScan) {
        final InputStream stream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream(packageName.replaceAll(PACKAGE_DELIMITER_REGEX, DIRECTORY_DELIMITER));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
        return reader.lines()
            .filter(line -> line.endsWith(CLASS_FILE_EXTENSION))
            .map(line -> getClass(line, packageName))
            .filter(typeToScan::isAssignableFrom)
            .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        final String classPath = packageName + PACKAGE_DELIMITER
            + className.substring(0, className.lastIndexOf(EXTENSION_DELIMITER));
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Cannot find class. (%s)", classPath));
        }
    }
}
