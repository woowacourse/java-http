package org.apache.coyote.http11;

import java.util.Optional;

public interface ControllerResolver {

    Optional<Controller> getController(HttpRequest request);
}
