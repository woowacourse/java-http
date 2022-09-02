package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMapper {

    INDEX("/", new IndexController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private final String path;
    private final Controller controller;

    RequestMapper(final String path, final Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller mapToController(final HttpRequest httpRequest) {
        final var path = httpRequest.getPath();
        return Arrays.stream(RequestMapper.values())
                .filter(v -> v.path.equals(path))
                .map(v -> v.controller)
                .findFirst()
                .orElse(new StaticResourceController());
    }
}
