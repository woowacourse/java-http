package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.exception.UnsupportedMethodException;

public abstract class Controller {
    protected abstract void handle(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    protected void operate(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getHttpMethod();
        if (method.isPost()) {
            doPost(request, response);
            return;
        }
        if (method.isGet()) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedMethodException("Method is not supported: " + method);
    }
}
