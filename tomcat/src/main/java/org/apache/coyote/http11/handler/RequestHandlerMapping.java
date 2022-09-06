package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.DashBoardController;
import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.AuthController;
import nextstep.jwp.presentation.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.exception.NoHandlerFoundException;

public class RequestHandlerMapping {

    private static Map<HttpRequest, Controller> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put(new HttpRequest("/login", "POST"), new AuthController());
        handlers.put(new HttpRequest("/login.html", "GET"), new AuthController());
        handlers.put(new HttpRequest("/login", "GET"), new AuthController());

        handlers.put(new HttpRequest("/index.html", "GET"), new DashBoardController());
        handlers.put(new HttpRequest("/", "GET"), new HomeController());
        handlers.put(new HttpRequest("/register.html", "GET"), new AuthController());
        handlers.put(new HttpRequest("/register", "POST"), new AuthController());

        handlers.put(new HttpRequest("static-resource", "GET"), new StaticResourceController());
    }

    public static Controller getHandler(String startLine) {
        final String method = getMethod(startLine);
        final String uri= getUri(startLine);

        if (isHandler(uri, method)) {
            return handlers.keySet()
                    .stream()
                    .filter(httpRequest -> httpRequest.equals(new HttpRequest(uri, method)))
                    .map(httpRequest -> handlers.get(httpRequest))
                    .findAny()
                    .orElseThrow(NoHandlerFoundException::new);
        }
        return handlers.get(new HttpRequest("static-resource", "GET"));
    }

    private static String getMethod(final String startLine) {
        return startLine.split(" ")[0];
    }

    private static String getUri(final String startLine) {
        String uri = startLine.split(" ")[1];
        if (QueryParam.isQueryParam(uri)) {
            return uri.split("\\?")[0];
        }
        return startLine.split(" ")[1];
    }

    private static boolean isHandler(final String uri, final String method) {
        return handlers.keySet()
                .stream()
                .anyMatch(httpRequest -> httpRequest.equals(new HttpRequest(uri, method)));
    }
}
