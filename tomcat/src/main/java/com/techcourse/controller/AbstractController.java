package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getRequestLine().getMethod()) {
            case GET_METHOD -> doGet(request, response);
            case POST_METHOD -> doPost(request, response);
            default -> throw new UnsupportedOperationException("지원하지 않는 메서드입니다.");
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
