package org.apache.catalina.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        final String method = request.getMethod();
        if (method.equals("GET")) {
            doGet(request, response);
        }
        if (method.equals("POST")) {
            doPost(request, response);
        }
    }

    protected void doGet(final Request request, final Response response) throws Exception { /* NOOP */ }

    protected void doPost(final Request request, final Response response) throws Exception { /* NOOP */ }
}
