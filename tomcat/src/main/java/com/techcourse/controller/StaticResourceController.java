package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class StaticResourceController extends AbstractController {

    private static final StaticResourceController INSTANCE = new StaticResourceController();

    public static StaticResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (request.getPath().endsWith(".html")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, request.getPath());
        }
        if (request.getPath().endsWith(".css")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.CSS, request.getPath());
        }
        if (request.getPath().endsWith(".js")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.JAVASCRIPT, request.getPath());
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html");
    }
}
