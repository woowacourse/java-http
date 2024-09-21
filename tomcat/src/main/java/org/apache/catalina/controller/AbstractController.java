package org.apache.catalina.controller;

import org.apache.catalina.http.HeaderName;
import org.apache.catalina.http.HttpMethod;
import org.apache.catalina.http.StatusCode;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            if (request.isMethod(HttpMethod.GET)) {
                doGet(request, response);
                return;
            }
            if (request.isMethod(HttpMethod.POST)) {
                doPost(request, response);
                return;
            }
            throw new IllegalArgumentException("HttpMethod not found");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(HeaderName.LOCATION, "/500.html");
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new IllegalArgumentException("HttpMethod unsupported");
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw new IllegalArgumentException("HttpMethod unsupported");
    }
}

