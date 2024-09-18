package com.techcourse.controller;

import com.techcourse.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doGet(request, response);
        }
        throw new UnsupportedHttpMethodException(request.getMethod().name());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement post method before invoke.");
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("implement get method before invoke.");
    }
}
