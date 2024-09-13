package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class HomePageController extends AbstractController {

    private static final String HOME_PAGE_BODY = "Hello world!";
    private static final HomePageController INSTANCE = new HomePageController();

    private HomePageController() {}

    public static HomePageController getInstance() {
        return INSTANCE;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.addStatusLine(HttpStatusCode.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        response.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(HOME_PAGE_BODY.getBytes().length));
        response.addBody(HOME_PAGE_BODY);
        response.writeResponse();
    }
}
