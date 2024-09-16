package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getUri();
        response.setView(uri);
        response.setStatus(StatusCode.OK);
    }
}
