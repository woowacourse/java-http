package com.techcourse.controller;

import org.apache.coyote.http11.StaticResourceReader;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class StaticController extends AbstractController {
    private final StaticResourceReader staticResourceReader = new StaticResourceReader();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = staticResourceReader.read(request.getRequestTarget());
        if (body == null) {
            return;
        }

        response.setBody(body);
    }
}
