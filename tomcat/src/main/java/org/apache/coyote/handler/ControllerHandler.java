package org.apache.coyote.handler;

import org.apache.coyote.Controller;
import org.apache.coyote.controller.FileController;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;

import java.util.Arrays;
import java.util.function.Supplier;

public enum ControllerHandler {

    FILE_HANDLER(null, null),
    LOGIN("/login", LoginController::getController),
    REGISTER("/register", RegisterController::getController),
    ;

    private final String uri;
    private final Supplier<Controller> getController;

    ControllerHandler(final String uri, final Supplier<org.apache.coyote.Controller> getControllerInstance) {
        this.uri = uri;
        this.getController = getControllerInstance;
    }

    public static Controller findController(final String uri) {
        return Arrays.stream(ControllerHandler.values())
                     .filter(handler -> handler.uri != null && handler.uri.equals(uri))
                     .map(handler -> handler.getController.get())
                     .findAny()
                     .orElseGet(FileController::from);
    }
}
