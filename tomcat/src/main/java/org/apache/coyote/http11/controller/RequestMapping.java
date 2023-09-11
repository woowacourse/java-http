package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.web.HelloController;
import nextstep.web.IndexController;
import nextstep.web.LoginController;
import nextstep.web.RegisterController;
import org.apache.coyote.http11.request.HttpRequestLine;

public class RequestMapping {
    private static final String HTML_EXTENSION = ".html";
    private static final Map<String, Controller> controllerMap = Map.of(
            "/", new HelloController(),
            "/index", new IndexController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequestLine requestStartLine) {
        final String requestURI = removeHtmlExtension(requestStartLine.getRequestURI());
        return controllerMap.getOrDefault(requestURI, new ForwardController(requestStartLine.getRequestURI()));
    }

    private static String removeHtmlExtension(final String requestURI) {
        if (requestURI.endsWith(HTML_EXTENSION)) {
            return requestURI.substring(requestURI.lastIndexOf(HTML_EXTENSION));
        }

        return requestURI;
    }
}
