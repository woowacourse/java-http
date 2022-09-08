package org.apache.coyote.http11;

import java.util.Map;
import nextstep.jwp.view.IndexController;
import nextstep.jwp.view.LoginController;
import nextstep.jwp.view.RegisterController;
import org.apache.coyote.common.controller.Controller;
import org.apache.coyote.common.request.Request;

public class HandlerMapper {

    private static final Map<String, Controller> cache;
    private static final Controller staticResourceController;

    static {
        cache = Map.ofEntries(
                Map.entry("GET /", new IndexController()),
                Map.entry("GET /login", new LoginController()),
                Map.entry("POST /login", new LoginController()),
                Map.entry("POST /register", new RegisterController()),
                Map.entry("GET /register", new RegisterController())
        );
        staticResourceController = new StaticResourceController();
    }

    public static Controller of(final Request request) {
        return cache.getOrDefault(request.getRequestIdentifier(), staticResourceController);
    }
}
