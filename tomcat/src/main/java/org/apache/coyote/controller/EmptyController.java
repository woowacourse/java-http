package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;

public class EmptyController extends AbstractController {
    @Override
    Http11Response doGet(HttpRequest httpRequest) {
        return null;
    }

    @Override
    Http11Response doPost(HttpRequest httpRequest) {
        return null;
    }

    @Override
    public Http11Response service(HttpRequest httpRequest) throws RuntimeException {
        final String requestUri = httpRequest.getRequestLine().getRequestURI();
        final String resourcePath = RESOURCE_PATH + requestUri;
        return new Http11Response(getClass().getClassLoader().getResource(resourcePath), 200, "OK");

    }
}
