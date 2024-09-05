package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpRequest;

import com.techcourse.exception.UnsupportedMethodException;

public abstract class Controller {
    protected static final String POST = "POST";
    protected static final String GET = "GET";

    protected abstract String doPost(HttpRequest request) throws IOException;

    protected abstract String doGet(HttpRequest request) throws IOException;

    public String operate(HttpRequest request) throws IOException {
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
