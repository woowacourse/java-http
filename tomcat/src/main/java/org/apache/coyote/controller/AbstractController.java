package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
            return ;
        }
        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
            return ;
        }
        throw new UnsupportedOperationException("Not allowed method " + request.getMethodName());
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethodName());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethodName());
    }
}
