package org.apache.coyote.http11.handler;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HelloController;
import com.techcourse.controller.LoginController;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private final List<Controller> controllers = List.of(
            new HelloController(),
            new LoginController()
    );

    public Optional<Controller> getController(HttpRequest httpRequest) {
        return controllers.stream()
                .filter(controller -> controller.getMethod().equals(httpRequest.getMethod())
                        && controller.getPath().equals(httpRequest.getPath()))
                .findFirst();
    }
}
