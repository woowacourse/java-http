package org.apache.controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;

public class RootController implements Controller {

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getUri().equals("/");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody("Hello world!");
    }
}
