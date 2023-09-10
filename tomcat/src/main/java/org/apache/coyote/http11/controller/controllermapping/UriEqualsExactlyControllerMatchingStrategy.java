package org.apache.coyote.http11.controller.controllermapping;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public class UriEqualsExactlyControllerMatchingStrategy implements ControllerMatchingStrategy {
    @Override
    public Optional<Controller> findController(final HttpPath httpPath, List<Controller> controllers) throws Exception {
        for (Controller controller : controllers) {
            final Class<? extends Controller> clazz = controller.getClass();
            final RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            if (equalsExactly(httpPath, requestMapping)) {
                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                return Optional.of((Controller) constructor.newInstance());
            }
        }
        return Optional.empty();
    }

    private static boolean equalsExactly(final HttpPath httpPath, final RequestMapping requestMapping) {
        return Objects.equals(HttpPath.from(requestMapping.uri()), httpPath);
    }
}
