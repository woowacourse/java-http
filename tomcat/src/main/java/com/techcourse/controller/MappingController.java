package com.techcourse.controller;

import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public abstract class MappingController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            doGet(request, response);
        }
        if (httpMethod == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected String getContentType(String contentType) {
        if (contentType.endsWith(".css")) {
            return "text/css ";
        }
        return "text/html;charset=utf-8 ";
    }
}
