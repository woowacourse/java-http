package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }
}
