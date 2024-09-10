package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class RestController extends Controller {

    public boolean service(HttpRequest request, HttpResponse response) {
        return switch (request.getHttpMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
        };
    }

    abstract boolean doGet(HttpRequest request, HttpResponse response);

    abstract boolean doPost(HttpRequest request, HttpResponse response);
}
