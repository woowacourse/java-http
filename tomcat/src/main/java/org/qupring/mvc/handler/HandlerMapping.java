package org.qupring.mvc.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.qupring.annotation.Route;

public class HandlerMapping {

    private final Map<MappingTarget, Method> controllerMappings = new HashMap<>();
    private final Map<String, String> resourcesMappings = new HashMap<>();

    public void addResourceMappings(Map<String, String> mappings) {
        resourcesMappings.putAll(mappings);
    }

    public String getResource(String path) {
        return resourcesMappings.get(path);
    }

    public void addControllerMappings(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                Route route = method.getAnnotation(Route.class);
                if(route == null){
                    continue;
                }
                controllerMappings.put(new MappingTarget(route.path(), route.method()), method);
            }
        }
    }

    public Method getControllerMethod(String path, HttpMethod method) {
        return controllerMappings.get(new MappingTarget(path, method));
    }


}
