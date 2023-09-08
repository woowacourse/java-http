package org.apache.coyote.http11.controller.controllermapping;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public class ControllerMatcher {

    public static final String CONTROLLER_PACKAGE_PATH = "org.apache.coyote.http11.controller";
    private static final List<ControllerMatchingStrategy> MATCH_STRATEGIES = List.of(
            new UriEqualsExactlyControllerMatchingStrategy(),
            new UriRegexControllerMatchingStrategy()
    );

    public ControllerMatcher() {
    }

    public Controller matchController(HttpPath httpPath) throws Exception {
        File controllerDirectory = findControllerDirectory();

        if (controllerDirectory.exists()) {
            File[] files = controllerDirectory.listFiles((dir, name) -> name.endsWith(".class"));

            if (files != null) {
                return findController(httpPath, files);
            }
        }

        return null;
    }

    private static Controller findController(final HttpPath httpPath, final File[] files)
            throws Exception {

        final List<Class<?>> controllerClasses = Arrays.stream(files)
                .map(file -> {
                    try {
                        return findControllerClass(file);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                })
                .collect(Collectors.toList());

        Optional<Controller> controller;
        for (ControllerMatchingStrategy matchStrategy : MATCH_STRATEGIES) {
            controller = matchStrategy.findController(httpPath, controllerClasses);
            if (controller.isPresent()) {
                return controller.get();
            }
        }

        return null;
    }

    private static Class<?> findControllerClass(final File file) throws ClassNotFoundException {
        String className = CONTROLLER_PACKAGE_PATH + "." + file.getName().replace(".class", "");
        return Class.forName(className);
    }

    private static File findControllerDirectory() throws URISyntaxException {
        String packagePath = CONTROLLER_PACKAGE_PATH.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new File(classLoader.getResource(packagePath).toURI());
    }
}
