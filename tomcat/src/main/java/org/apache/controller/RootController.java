package org.apache.controller;

import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;
import org.apache.http.StatusCode;

public class RootController implements Controller {

    private final String processableUri = "/";

    @Override
    public boolean isProcessableRequest(HttpRequestMessage request) {
        return processableUri.equals(request.getUri());
    }

    @Override
    public void processRequest(HttpRequestMessage request, HttpResponseMessage response) {
        response.setHttpVersion(request.getVersion());
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody("Hello world!");
    }
}
