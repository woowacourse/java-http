package org.apache.coyote.http11.controller.controllermapping;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public class UriRegexControllerMatchingStrategy implements ControllerMatchingStrategy {
    @Override
    public Optional<Controller> findController(final HttpPath httpPath, final List<Class<?>> controllerClasses)
            throws Exception {
        for (Class<?> clazz : controllerClasses) {
            if (isControllerType(clazz) && hasRequestMappingAnnotation(clazz)) {
                final RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                if (matchesRegex(httpPath, requestMapping)) {
                    final Constructor<?> constructor = clazz.getDeclaredConstructor();
                    return Optional.of((Controller) constructor.newInstance());
                }
            }
        }
        return Optional.empty();
    }

    private static boolean matchesRegex(final HttpPath httpPath, final RequestMapping requestMapping) {
        return httpPath.getEndPoint().matches(requestMapping.regex());
    }

    private static boolean hasRequestMappingAnnotation(final Class<?> clazz) {
        return clazz.isAnnotationPresent(RequestMapping.class);
    }

    private static boolean isControllerType(final Class<?> clazz) {
        return Controller.class.isAssignableFrom(clazz);
    }
}
