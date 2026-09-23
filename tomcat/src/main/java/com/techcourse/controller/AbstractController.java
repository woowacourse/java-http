package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {
    private static final String UNSUPPORTED_HTTP_METHOD_MESSAGE = "지원하지 않는 HTTP 메서드: ";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // HTTP method에 따라 doGet 또는 doPost로 분기한다.
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> throw new IllegalArgumentException(UNSUPPORTED_HTTP_METHOD_MESSAGE + request.getMethod());
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }
}
