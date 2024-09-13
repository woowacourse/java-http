package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class HomeController extends AbstractController {

    private static final String HELLO_WORLD = "Hello world!";
    public static final String TEXT_HTML = "text/html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        buildOkResponse(HELLO_WORLD, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private void buildOkResponse(String responseBody, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(responseBody);
        response.addHeader(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length));
        response.addHeader(HttpHeader.CONTENT_TYPE.getValue(), TEXT_HTML);
    }
}
