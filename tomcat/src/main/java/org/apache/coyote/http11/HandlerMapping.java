package org.apache.coyote.http11;

import java.util.List;
import nextstep.jwp.controller.FileController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootPathController;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private static final Manager SESSION_MANAGER = SessionManager.getInstance();
    private static final List<Controller> handlers = List.of(
            new RootPathController(), new FileController(),
            new LoginController(SESSION_MANAGER), new RegisterController());

    public static Controller findHandler(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.isSuitable(httpRequest))
                .findAny()
                .orElse(new NotFoundController());
    }
}
