package org.apache.servlet;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.servlet.customservlet.BadRequestServlet;
import org.reflections.Reflections;

public class ServletContainer {

    private static final String SERVLET_COMPONENT_SCAN_PACKAGE = "org.apache.servlet.customservlet";
    private static final SimpleHttpServlet DEFAULT_ERROR_HANDLING_SERVLET = new BadRequestServlet();

    private static final Map<String, SimpleServlet> customServlets = scanPredefinedPackage();

    private ServletContainer() {
    }

    public static Map<String, SimpleServlet> scanPredefinedPackage() {
        Map<String, SimpleServlet> customServlets = new HashMap<>();
        Reflections reflections = new Reflections(SERVLET_COMPONENT_SCAN_PACKAGE);
        Set<Class<?>> declaredClasses = reflections.getTypesAnnotatedWith(SimpleWebServlet.class);
        for (final Class<?> clazz : declaredClasses) {
            SimpleWebServlet annotation = clazz.getAnnotation(SimpleWebServlet.class);
            String[] uris = annotation.value();
            try {
                SimpleServlet servlet = (SimpleServlet) clazz.getConstructor().newInstance();
                for (final String uri : uris) {
                    customServlets.put(uri, servlet);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                     InstantiationException | InvocationTargetException e) {
                throw new ServletContainerInitiationException();
            }
        }
        return customServlets;
    }

    public static SimpleServlet find(HttpRequest request){
        for (Entry<String, SimpleServlet> entry : customServlets.entrySet()) {
            String url = request.getUrl();
            Pattern pattern = Pattern.compile(entry.getKey());
            if (pattern.matcher(url).matches()) {
                return entry.getValue();
            }
        }
        return DEFAULT_ERROR_HANDLING_SERVLET;
    }
}
