package com.techcourse.controller;

import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getHttpMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> throw new UncheckedHttpException(new IllegalArgumentException("지원하지 않는 HTTP Method 형식입니다."));
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }
}
