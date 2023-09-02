package org.apache.coyote.http11.web;

import java.util.Map;
import java.util.Optional;
import nextstep.web.HelloController;
import nextstep.web.IndexController;
import nextstep.web.LoginController;
import nextstep.web.RegisterController;
import org.apache.coyote.http11.request.HttpRequestStartLine;

public class HandlerMapping {
    private static final String HTML_EXTENSION = ".html";

    private final Map<String, Controller> controllerMap = Map.of(
            "/", new HelloController(),
            "/index", new IndexController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Optional<Controller> findController(final HttpRequestStartLine requestStartLine) {
        String requestURI = removeHtmlExtension(requestStartLine.getRequestURI());
        return Optional.ofNullable(controllerMap.get(requestURI));
    }

    private String removeHtmlExtension(final String requestURI) {
        if (requestURI.endsWith(HTML_EXTENSION)) {
            return requestURI.substring(0, requestURI.length() - HTML_EXTENSION.length());
        }

        return requestURI;
    }
}
