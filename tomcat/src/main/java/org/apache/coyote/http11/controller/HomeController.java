package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class HomeController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            doGet(request, response);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var responseBody = "Hello world!";
        response.setFileType("html");
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setResponseBody(responseBody);
    }
}
