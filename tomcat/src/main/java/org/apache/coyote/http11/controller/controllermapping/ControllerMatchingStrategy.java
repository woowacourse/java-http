package org.apache.coyote.http11.controller.controllermapping;

import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpPath;

public interface ControllerMatchingStrategy {
    Optional<Controller> findController(final HttpPath httpPath, List<Class<?>> controllerClasses) throws Exception;
}
