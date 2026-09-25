package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String responseBody = StaticResourceLoader.load(path);
        if (responseBody == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(ContentType.HTML, StaticResourceLoader.load(NOT_FOUND_PAGE));
            return;
        }
        response.setBody(ContentType.from(path), responseBody);
    }
}
