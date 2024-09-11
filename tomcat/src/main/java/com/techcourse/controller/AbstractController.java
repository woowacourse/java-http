package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.RequestMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        RequestMethod method = request.getMethod();
        if (method.isGetMethod()) {
            doGet(request, response);
        }

        if (method.isPostMethod()) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response);
}
