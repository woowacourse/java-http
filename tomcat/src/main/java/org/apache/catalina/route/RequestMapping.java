package org.apache.catalina.route;

import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public interface RequestMapping {

    Optional<Controller> getController(HttpRequest request);

    void register(AbstractController controller);
}
