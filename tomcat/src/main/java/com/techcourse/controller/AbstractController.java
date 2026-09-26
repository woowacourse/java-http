package com.techcourse.controller;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
        String method = request.getRequestLine().getMethod();

        if ("GET".equals(method)) {
            doGet(request, response);
            return;
        }
        if ("POST".equals(method)) {
            doPost(request, response);
            return;
        }
        return;
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
