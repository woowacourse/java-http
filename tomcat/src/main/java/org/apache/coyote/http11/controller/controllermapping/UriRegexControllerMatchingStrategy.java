package org.apache.coyote.http11.controller.controllermapping;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public class UriRegexControllerMatchingStrategy implements ControllerMatchingStrategy {
    @Override
    public Optional<Controller> findController(final HttpPath httpPath, final List<Controller> controllers)
            throws Exception {
        for (Controller controller : controllers) {
            final Class<? extends Controller> clazz = controller.getClass();
            final RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            if (matchesRegex(httpPath, requestMapping)) {
                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                return Optional.of((Controller) constructor.newInstance());
            }
        }
        return Optional.empty();
    }

    private static boolean matchesRegex(final HttpPath httpPath, final RequestMapping requestMapping) {
        return httpPath.getEndPoint().matches(requestMapping.regex());
    }
}
