package org.apache.controller;

import org.apache.http.HttpResponse;
import org.apache.http.request.HttpRequest;
import org.apache.http.value.HttpHeader;
import org.apache.http.value.HttpMethod;
import org.apache.http.value.StatusCode;

public class RootController implements Controller {

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET
                && request.getUri().equals("/");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), "text/html;charset=utf-8");
        response.setBody("Hello world!");
    }
}
