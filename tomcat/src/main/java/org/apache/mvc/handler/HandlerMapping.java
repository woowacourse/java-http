package org.apache.mvc.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.annotation.Route;
import org.apache.http.HttpMethod;

public class HandlerMapping {

    private final Map<MappingTarget, Method> controllerMappings = new HashMap<>();
    private final Map<String, String> resourcesMappings = new HashMap<>();

    public HandlerMapping(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                Route route = method.getAnnotation(Route.class);

                if (route != null) {
                    controllerMappings.put(new MappingTarget(route.path(), route.method()), method);
                }
            }
        }
    }

    public void addResourceMappings(Map<String, String> mappings) {
        resourcesMappings.putAll(mappings);
    }

    public Method getControllerHandler(String path, HttpMethod method) {
        return controllerMappings.get(new MappingTarget(path, method));
    }

    public String getResource(String path) {
        return resourcesMappings.get(path);
    }

}
