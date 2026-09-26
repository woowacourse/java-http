package org.apache.coyote.http11;

import java.util.Map;

public class RequestMapping {

    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";

    private final Map<String, Controller> controllers = Map.of(
            LOGIN, new LoginController(),
            REGISTER, new RegisterController());
    private final Controller defaultController = new StaticResourceController();

    public Controller getController(final HttpRequest request) {
        return controllers.getOrDefault(extractRequestPath(request.getUri()), defaultController);
    }

    private String extractRequestPath(final String requestUri) {
        final int queryStringIndex = requestUri.indexOf('?');
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }
}
