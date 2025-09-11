package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class HomeController extends AbstractController {

    private static final HomeController INSTANCE = new HomeController();

    public static HomeController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
    }
}
