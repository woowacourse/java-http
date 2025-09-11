package com.techcourse.handler;

import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;

public class RequestMapping {

    private final HttpVersion httpVersion;

    public RequestMapping(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public RequestHandler getRequestHandler(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return new RootRequestHandler(httpVersion);
        }

        if (httpRequest.getFilePath().equals("/login.html")) {
            return new LoginRequestHandler(httpVersion);
        }
        if (httpRequest.getFilePath().equals("/register.html")) {
            return new RegisterRequestHandler(httpVersion);
        }

        return new StaticResourceRequestHandler(httpVersion);
    }
}
