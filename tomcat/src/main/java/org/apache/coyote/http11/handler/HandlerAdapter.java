package org.apache.coyote.http11.handler;

import org.apache.catalina.SessionManger;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestUri;

public class HandlerAdapter {

    public static final String LOGIN_URI = "/login";
    public static final String DEFAULT_URI = "/";
    public static final String REGISTER_URI = "/register";

    private static final SessionManger sessionManager = new SessionManger();

    public RequestHandler find(final HttpRequest request) {
        final HttpRequestUri uri = request.getUri();

        if (uri.samePath(LOGIN_URI)) {
            return new LoginHandler(sessionManager);
        }
        if (uri.samePath(DEFAULT_URI)) {
            return new DefaultHandler(sessionManager);
        }
        if (uri.samePath(REGISTER_URI)) {
            return new RegisterHandler(sessionManager);
        }
        return new ResourceHandler(sessionManager);
    }
}
