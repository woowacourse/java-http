package org.apache.coyote;

import java.io.IOException;
import java.util.Arrays;
import nextstep.jwp.controller.CommonController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public enum RequestMapping {

    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    ;

    private final String uri;
    private final Controller controller;

    RequestMapping(final String uri, final Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static void handle(final HttpRequest request, final HttpResponse response) throws IOException {
        final Controller controller = getController(request.getPath());
        controller.service(request, response);
    }

    private static Controller getController(final String key) {
        return Arrays.stream(RequestMapping.values())
                .filter(requestMapping -> requestMapping.uri.equals(key))
                .map(requestMapping -> requestMapping.controller)
                .findAny()
                .orElse(new CommonController());
    }
}
