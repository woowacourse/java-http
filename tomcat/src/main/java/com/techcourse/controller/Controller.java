package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.exception.UnsupportedMethodException;

public abstract class Controller {
    private static final String POST = "POST";
    private static final String GET = "GET";

    protected abstract HttpResponse handle(HttpRequest request) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest request) throws IOException;

    protected abstract HttpResponse doGet(HttpRequest request) throws IOException;

    protected HttpResponse operate(HttpRequest request) throws IOException {
        String method = request.getHttpMethod();
        if (POST.equalsIgnoreCase(method)) {
            return doPost(request);
        }
        if (GET.equalsIgnoreCase(method)) {
            return doGet(request);
        }
        throw new UnsupportedMethodException("Method is not supported");
    }
}
