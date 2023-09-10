package org.apache.coyote.http11.controller.controllermapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public class ControllerMatcher {

    public static final String CONTROLLER_PACKAGE_PATH = "org.apache.coyote.http11.controller";
    private static final List<ControllerMatchingStrategy> match_strategies;
    private static final List<Controller> controllers;

    static {
        match_strategies = List.of(
                new UriEqualsExactlyControllerMatchingStrategy(),
                new UriRegexControllerMatchingStrategy()
        );

        try {
            controllers = initializeControllers();
        } catch (Exception e) {
            throw new Error("Controllers Could not be initialized");
        }
    }

    public static Controller findController(final HttpPath httpPath)
            throws Exception {
        for (ControllerMatchingStrategy matchStrategy : match_strategies) {
            Optional<Controller> controller = matchStrategy.findController(httpPath, controllers);
            if (controller.isPresent()) {
                return controller.get();
            }
        }

        return null;
    }


    private static List<Controller> initializeControllers() throws Exception {
        File controllerDirectory = findControllerDirectory();
        File[] files = controllerDirectory.listFiles((dir, name) -> name.endsWith(".class"));

        if (Objects.isNull(files)) {
            throw new FileNotFoundException();
        }

        final List<Class<?>> controllerClasses = Arrays.stream(files)
                .map(ControllerMatcher::convertFileToClass)
                .collect(Collectors.toList());

        return convertClassToController(controllerClasses);
    }

    private static File findControllerDirectory() throws URISyntaxException {
        String packagePath = CONTROLLER_PACKAGE_PATH.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new File(classLoader.getResource(packagePath).toURI());
    }

    private static Class<?> convertFileToClass(final File file) {
        try {
            String className = CONTROLLER_PACKAGE_PATH + "." + file.getName().replace(".class", "");
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Controller> convertClassToController(final List<Class<?>> controllerClasses)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final List<Controller> controllers = new ArrayList<>();
        for (Class<?> clazz : controllerClasses) {
            if (isControllerType(clazz) && hasRequestMappingAnnotation(clazz)) {
                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                controllers.add((Controller) constructor.newInstance());
            }
        }
        return controllers;
    }

    private static boolean isControllerType(final Class<?> clazz) {
        return Controller.class.isAssignableFrom(clazz);
    }

    private static boolean hasRequestMappingAnnotation(final Class<?> clazz) {
        return clazz.isAnnotationPresent(RequestMapping.class);
    }
}
