package com.techcourse.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
    }

    abstract protected void doPost(HttpRequest request, HttpResponse response) throws Exception;

    abstract protected void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
