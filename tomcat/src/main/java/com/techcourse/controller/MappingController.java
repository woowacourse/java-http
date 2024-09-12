package com.techcourse.controller;

import org.apache.catalina.ResourceResolver;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public abstract class MappingController implements Controller {

    protected final ResourceResolver resourceResolver = new ResourceResolver();

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
}
