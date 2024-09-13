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
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        buildOkResponse(HELLO_WORLD, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private void buildOkResponse(String responseBody, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.OK)
                .withResponseBody(responseBody)
                .addHeader(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length))
                .addHeader(HttpHeader.CONTENT_TYPE.getValue(), TEXT_HTML);
    }
}
