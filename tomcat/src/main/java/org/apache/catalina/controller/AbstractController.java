package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> methodNotAllowed(response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected abstract String allowedMethods();

    private void methodNotAllowed(final HttpResponse response) {
        response.setStatus(405, "Method Not Allowed");
        response.setHeader("Allow", allowedMethods());
    }
}
