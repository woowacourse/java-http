package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected static final String ROOT_LOCATION = "http://localhost:8080/";
    protected final String JAVA_SESSION_ID = "JSESSIONID";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethodType().isPost()) {
            doPost(request, response);
        }
        if (request.getMethodType().isGet()) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
