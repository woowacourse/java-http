package com.techcourse.controller;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.getRequestMethod();

        if (POST.equals(method)) {
            doPost(request, response);
        }

        if (GET.equals(method)) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
