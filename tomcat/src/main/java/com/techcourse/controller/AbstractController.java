package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.requestLine().method() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.requestLine().method() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }
}
