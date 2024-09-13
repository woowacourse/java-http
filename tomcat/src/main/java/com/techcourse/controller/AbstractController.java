package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void requestMapping(HttpRequest request, HttpResponse httpResponse) {

        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, httpResponse);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, httpResponse);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse httpResponse) {
    }

    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
    }
}
