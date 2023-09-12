package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isGetMethod()) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }
}
