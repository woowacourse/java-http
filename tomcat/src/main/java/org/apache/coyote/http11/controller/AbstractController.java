package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, Http11Response response) throws Exception {
        if ("GET".equals(request.getRequestLine().httpMethod())) {
            doGet(request, response);
        }
        if ("POST".equals(request.getRequestLine().httpMethod())) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, Http11Response response) throws Exception {
    }

    protected void doGet(HttpRequest request, Http11Response response) throws Exception {
    }
}
