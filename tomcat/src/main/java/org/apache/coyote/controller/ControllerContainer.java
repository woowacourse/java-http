package org.apache.coyote.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerContainer {

    private static final Logger log = LoggerFactory.getLogger(ControllerContainer.class);

    private static Set<Class<?>> classes;
    private static Set<AbstractController> controllers = new HashSet<>();
    private static Map<BiPredicate<String, List<String>>, AbstractController> classMap = new HashMap<>();

    private ControllerContainer() {
    }

    public static void scanPackage(final String packagePath) {
        try {
            scanPackageInternal(packagePath);
        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scanPackageInternal(final String packagePath)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Reflections reflections = new Reflections(packagePath);
        classes = reflections.get(Scanners.SubTypes.of(AbstractController.class).asClass());
        for (final Class<?> clazz : classes) {
            final AbstractController controller = (AbstractController) clazz.getDeclaredConstructor().newInstance();
            controllers.add(controller);
        }
    }

    public static Controller findFromUri(final String uri, final String httpMethod) {
        return controllers.stream()
                .filter(it -> it.support(uri, httpMethod))
                .findAny()
                .orElse(null);
    }
}
