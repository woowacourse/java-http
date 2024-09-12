package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        String responseBody = "Hello world!";

        response.generate200Response(path, responseBody);
    }
}
