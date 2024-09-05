package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.Http11Helper;

import com.techcourse.exception.UnsupportedMethodException;

public abstract class Controller {
    private static final Http11Helper http11Helper = Http11Helper.getInstance();
    protected static final String POST = "POST";
    protected static final String GET = "GET";

    protected abstract String doPost(String request) throws IOException;

    protected abstract String doGet(String request) throws IOException;

    public String operate(String request) throws IOException {
        String method = http11Helper.extractHttpMethod(request);
        if(POST.equalsIgnoreCase(method)){
            return doPost(request);
        }
        if(GET.equalsIgnoreCase(method)) {
            return doGet(request);
        }
        throw new UnsupportedMethodException("Method is not supported");
    }
}
