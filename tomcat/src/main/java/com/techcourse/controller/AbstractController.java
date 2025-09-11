package com.techcourse.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMappingLine().getMethod()) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> sendMethodNotAllowed(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}

    private void sendMethodNotAllowed(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_ALLOWED);
    }

    protected void ok(HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addHeader();
    }

    protected void ok(HttpResponse httpResponse, String body) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setBody(body.getBytes());
        httpResponse.addHeader(body.getBytes());
    }

    protected void redirect(HttpResponse httpResponse, String location) {
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.addHeader(Map.of("Location", location));
    }

    protected void setCookie(HttpResponse httpResponse, String name, String value) {
        httpResponse.addHeader(Map.of("Set-Cookie", name + "=" + value + "; "));
    }
}
