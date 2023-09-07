package org.apache.catalina.controller.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.reflections.Reflections;

public class ControllerMapping {

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    private ControllerMapping() {
    }

    public static void initialize() {
        Reflections reflections = new Reflections("org.apache.catalina.controller");
        Set<Class<?>> declaredClasses = reflections.getTypesAnnotatedWith(RequestMapping.class);
        for (final Class<?> clazz : declaredClasses) {
            RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
            String[] uris = annotation.value();
            try {
                Controller controller = (Controller) clazz.getConstructor().newInstance();
                for (final String uri : uris) {
                    controllers.put(uri, controller);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                     InstantiationException |
                     InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getPath());
        if (controller == null) {
            return controllers.get(request.getPath() + ".html");
        }
        return controller;
    }
}
