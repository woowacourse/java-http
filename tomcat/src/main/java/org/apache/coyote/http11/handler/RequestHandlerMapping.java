package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.DashBoardController;
import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.StaticResourceController;
import nextstep.jwp.presentation.AuthController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestMapping;
import org.apache.coyote.http11.QueryParam;

public class RequestHandlerMapping {

    private static final Map<HttpRequestMapping, Controller> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put(new HttpRequestMapping("/login"), new AuthController());
        handlers.put(new HttpRequestMapping("/login.html"), new AuthController());
        handlers.put(new HttpRequestMapping("/index.html"), new DashBoardController());
        handlers.put(new HttpRequestMapping("/"), new HomeController());
        handlers.put(new HttpRequestMapping("/register.html"), new AuthController());
        handlers.put(new HttpRequestMapping("/register"), new AuthController());

        handlers.put(new HttpRequestMapping("static-resource"), new StaticResourceController());
    }

    public static Controller getHandler(final HttpRequest httpRequest) {

        return handlers.keySet()
                .stream()
                .filter(httpRequestMapping -> httpRequestMapping.match(httpRequest))
                .map(handlers::get)
                .findAny()
                .orElseGet(() -> handlers.get(new HttpRequestMapping("static-resource")));
    }
}
