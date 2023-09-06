package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestUri;

public class HandlerAdapter {

    public static final String LOGIN_URI = "/login";
    public static final String DEFAULT_URI = "/";
    public static final String REGISTER_URI = "/register";

    public RequestHandler find(final HttpRequest request) {
        final HttpRequestUri uri = request.getUri();

        if (uri.containsPath(LOGIN_URI)) {
            return new LoginHandler();
        }
        if (uri.samePath(DEFAULT_URI)) {
            return new DefaultHandler();
        }
        if (uri.containsPath(REGISTER_URI)) {
            return new RegisterHandler();
        }
        return new ResourceHandler();
    }
}
