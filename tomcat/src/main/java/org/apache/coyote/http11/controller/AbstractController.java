package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }
}
