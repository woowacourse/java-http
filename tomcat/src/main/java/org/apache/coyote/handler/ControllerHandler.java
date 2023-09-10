package org.apache.coyote.handler;

import org.apache.coyote.Controller;
import org.apache.coyote.controller.FileController;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;

import java.util.Arrays;

public enum ControllerHandler {

    FILE_HANDLER(null, new FileController()),
    LOGIN("/login", new LoginController(new SessionManager())),
    REGISTER("/register", new RegisterController()),
    ;

    private final String uri;
    private final Controller controller;

    ControllerHandler(final String uri, final Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller findController(final String uri) {
        return Arrays.stream(ControllerHandler.values())
                     .filter(handler -> handler.uri != null && handler.uri.equals(uri))
                     .map(handler -> handler.controller)
                     .findAny()
                     .orElseGet(() -> FILE_HANDLER.controller);
    }
}
