package org.qupring.mvc.handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {

    private final Map<String, String> resourcesMappings = new HashMap<>();

    public void addResourceMappings(Map<String, String> mappings) {
        resourcesMappings.putAll(mappings);
    }

    public String getResource(String path) {
        return resourcesMappings.get(path);
    }

/*
    private final Map<MappingTarget, Method> controllerMappings = new HashMap<>();

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

    public void addControllerMappings(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                Route route = method.getAnnotation(Route.class);
                controllerMappings.put(new MappingTarget(route.path(), route.method()), method);
            }
        }
    }

    public Method getControllerMethod(String path, HttpMethod method) {
        return controllerMappings.get(new MappingTarget(path, method));
    }

 */
}
