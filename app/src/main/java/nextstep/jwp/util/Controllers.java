package nextstep.jwp.util;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ExtraController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public enum Controllers {
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private static final Controller extraController = new ExtraController();

    private final String uri;
    private final Controller controller;

    Controllers(String uri, Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller mathController(HeaderLine headerLine) {
        final String urlWithoutQuery = headerLine.getRequestURLWithoutQuery();

        return Arrays.stream(Controllers.values())
            .filter(element -> element.isSameURIs(urlWithoutQuery))
            .findAny()
            .map(element -> element.controller)
            .orElse(extraController);
    }

    private boolean isSameURIs(String targetURI) {
        return this.uri.equals(targetURI);
    }

}
