package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;

public class HomeController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        String responseBody = "Hello world!";

        response.generate200Response(path, responseBody);
    }
}
