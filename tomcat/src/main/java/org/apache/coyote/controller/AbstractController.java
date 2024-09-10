package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET)) {
            doGet(request, response);
            return ;
        }
        if (method.equals(HttpMethod.POST)) {
            doPost(request, response);
            return ;
        }
        throw new UnsupportedOperationException(method.getMethod());
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }
}
