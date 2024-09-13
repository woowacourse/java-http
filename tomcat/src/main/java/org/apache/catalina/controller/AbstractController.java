package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final String method = request.getRequestLine().getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {}

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {}
}
