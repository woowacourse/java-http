package org.apache.catalina.controller;

import com.techcourse.controller.LogInController;
import com.techcourse.controller.RegisterAbstractController;
import java.util.Map;
import org.apache.catalina.exception.ControllerNotMatchedException;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private static final Map<String, AbstractController> HANDLER_MAPPER = Map.of(
            "/login", new LogInController(),
            "/register", new RegisterAbstractController()
    );

    public AbstractController getController(HttpRequest request) {
        if (HANDLER_MAPPER.containsKey(request.getPath())) {
            return HANDLER_MAPPER.get(request.getPath());
        }

        throw new ControllerNotMatchedException();
    }
}
