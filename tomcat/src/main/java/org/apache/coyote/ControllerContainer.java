package org.apache.coyote;

import java.util.List;
import org.apache.coyote.exception.ControllerNotFoundException;
import org.apache.coyote.http11.Request;

public class ControllerContainer {
    private static final List<Controller> controllers = List.of(
            new LoginController(),
            new StaticFileController(),
            new RegisterController()
    );

    public static Controller findController(final Request request) {
        return controllers.stream()
                .filter(controller -> controller.isRunnable(request))
                .findFirst()
                .orElseThrow(ControllerNotFoundException::new);
    }
}
