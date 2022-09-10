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

    private static Map<HttpRequestMapping, Controller> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put(new HttpRequestMapping("/login", "POST"), new AuthController());
        handlers.put(new HttpRequestMapping("/login.html", "GET"), new AuthController());
        handlers.put(new HttpRequestMapping("/login", "GET"), new AuthController());
        handlers.put(new HttpRequestMapping("/index.html", "GET"), new DashBoardController());
        handlers.put(new HttpRequestMapping("/", "GET"), new HomeController());
        handlers.put(new HttpRequestMapping("/register.html", "GET"), new AuthController());
        handlers.put(new HttpRequestMapping("/register", "POST"), new AuthController());

        handlers.put(new HttpRequestMapping("static-resource", "GET"), new StaticResourceController());
    }

    public static Controller getHandler(final HttpRequest httpRequest) {

        return handlers.keySet()
                .stream()
                .filter(httpRequestMapping -> httpRequestMapping.match(httpRequest))
                .map(httpRequestMapping -> handlers.get(httpRequestMapping))
                .findAny()
                .orElseGet(() -> handlers.get(new HttpRequestMapping("static-resource", "GET")));
    }
}
