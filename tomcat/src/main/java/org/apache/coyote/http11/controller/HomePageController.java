package org.apache.coyote.http11.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomePageController extends AbstractController {

    private static final HomePageController instance = new HomePageController();

    private HomePageController() {}

    public static HomePageController getInstance() {
        return instance;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String body = "Hello world!";
        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        response.addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.addBody(body);
        response.writeResponse();
    }
}
