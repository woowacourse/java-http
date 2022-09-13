package org.apache.coyote.http11;

import java.util.stream.Stream;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;

public enum HandlerMapping {

    HOME("/", new HomeController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController()),
    STATIC_RESOURCE("", new StaticResourceController()),
    ;

    private final String path;
    private final Controller controller;

    HandlerMapping(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller findController(HttpRequest httpRequest) {
        final String resource = httpRequest.getPath().getResource();

        if (httpRequest.isStaticResource()) {
            return STATIC_RESOURCE.controller;
        }

        final HandlerMapping handlerMapping = Stream.of(values())
                .filter(it -> it.path.equals(resource))
                .findAny()
                .orElseThrow(ResourceNotFoundException::new);

        return handlerMapping.controller;
    }
}
