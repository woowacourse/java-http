package org.apache.coyote.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.mark.Service;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.Scanners;
import org.reflections.util.QueryFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonContainer {

    private static final Logger log = LoggerFactory.getLogger(SingletonContainer.class);
    private static final String CONTROLLER_PATH = "nextstep.jwp.presentation";
    private static final String SERVICE_PATH = "nextstep.jwp.application";

    private static Set<AbstractController> controllers = new HashSet<>();
    private static Map<Class<?>, Object> singletons = new HashMap<>();

    private SingletonContainer() {
    }

    public static void registerSingletons() {
        try {
            registerControllers();
            registerServices();
            registerFrameworkObjects();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getObject(final Class<T> clazz) {
        return (T) singletons.get(clazz);
    }

    public static Map<Class<?>, Object> getAllObjects() {
        return Collections.unmodifiableMap(singletons);
    }

    private static Set<AbstractController> getAbstractControllers() {
        return singletons.values().stream()
                .filter(clazz -> clazz instanceof AbstractController)
                .map(clazz -> (AbstractController) clazz)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void registerControllers() throws Exception {
        registerInternal(CONTROLLER_PATH, Scanners.SubTypes.of(AbstractController.class));
    }

    private static void registerServices() throws Exception {
        registerInternal(SERVICE_PATH, Scanners.TypesAnnotated.of(Service.class));
    }

    private static <T> void registerInternal(final String path,
                                             final QueryFunction<Store, String> queryFunction)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Reflections reflections = new Reflections(path);
        final Set<Class<?>> classes = reflections.get(queryFunction.asClass());
        for (final Class<?> aClass : classes) {
            final T object = (T) aClass.getDeclaredConstructor().newInstance();
            singletons.put(object.getClass(), object);
            log.info("Create Object: {}", object.getClass().getSimpleName());
        }
    }

    private static void registerFrameworkObjects() {
        final Set<AbstractController> classes = getAbstractControllers();
        final HandlerMapping handlerMapping = new HandlerMapping(classes);
        singletons.put(HandlerMapping.class, handlerMapping);
        log.info("Create Object: {}", handlerMapping.getClass().getSimpleName());
    }
}
