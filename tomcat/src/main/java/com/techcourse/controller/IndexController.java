package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class IndexController extends AbstractController {

    private static final String HELLO_PAGE = "/hello.html";

    public void doGet(HttpRequest request, HttpResponse response) {
        response.setHeader(Header.CONTENT_TYPE.value(), ContentType.HTML.getMimeType());
        response.setBody(readStaticResource(HELLO_PAGE));
    }
}
