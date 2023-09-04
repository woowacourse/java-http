package org.apache.coyote;

import org.apache.coyote.controller.LoginController;

public enum ControllerMapper {

    LOGIN("/login"),
    REGISTER("/register"),
    ;

    private final String uri;

    ControllerMapper(final String uri) {
        this.uri = uri;
    }

    public static Controller getController(final String parsedUri) {
        if (parsedUri.contains(LOGIN.uri) || parsedUri.contains(REGISTER.uri)) {
            return new LoginController();
        }

        return null;
    }
}
