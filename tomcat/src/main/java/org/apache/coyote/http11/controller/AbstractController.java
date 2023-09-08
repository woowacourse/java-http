package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isMethodEqualTo(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isMethodEqualTo(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {};

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {};
}
