package com.techcourse.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;

public abstract class AbstractRequestController implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.isGet()) {
            get(httpRequest, httpResponse);
        }
        if (httpRequest.isPost()) {
            post(httpRequest, httpResponse);
        }
    }

    protected abstract void get(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;

    protected abstract void post(HttpRequest httpRequest, HttpResponse httpResponse);
}
