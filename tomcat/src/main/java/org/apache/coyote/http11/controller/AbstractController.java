package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod httpMethod = request.getRequestLine().getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (httpMethod == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }
}
