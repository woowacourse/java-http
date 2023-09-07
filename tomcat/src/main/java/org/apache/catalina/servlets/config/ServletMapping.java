package org.apache.catalina.servlets.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.servlets.Servlet;
import org.apache.coyote.http11.HttpRequest;
import org.reflections.Reflections;

public class ServletMapping {

    private static final Map<String, Servlet> servlets = new ConcurrentHashMap<>();

    private ServletMapping() {
    }

    public static void initialize() {
        Reflections reflections = new Reflections("org.apache.catalina.servlets");
        Set<Class<?>> declaredClasses = reflections.getTypesAnnotatedWith(RequestMapping.class);
        for (final Class<?> clazz : declaredClasses) {
            RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
            String[] uris = annotation.value();
            try {
                Servlet servlet = (Servlet) clazz.getConstructor().newInstance();
                for (final String uri : uris) {
                    servlets.put(uri, servlet);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                     InstantiationException |
                     InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Servlet getServlet(HttpRequest request) {
        Servlet servlet = servlets.get(request.getPath());
        if (servlet == null) {
            return servlets.get(request.getPath() + ".html");
        }
        return servlet;
    }
}
