package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getRequestLine().getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }

        if (request.getRequestLine().getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
