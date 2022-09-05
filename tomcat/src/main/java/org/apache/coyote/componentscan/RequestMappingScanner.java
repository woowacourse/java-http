package org.apache.coyote.componentscan;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.annotation.RequestMapping;

public class RequestMappingScanner {

    private final static Set<Method> requestMappingMethods = new HashSet<>();

    public static Set<Method> scan() {
        final Set<Class<?>> controllers = ControllerScanner.scan();

        for (final Class<?> controller : controllers) {
            final Method[] methods = controller.getDeclaredMethods();
            for (final Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    requestMappingMethods.add(method);
                }
            }
        }
        return requestMappingMethods;
    }
}
