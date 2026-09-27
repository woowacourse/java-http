package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        methodNotAllowed(response);
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        methodNotAllowed(response);
    }

    private void methodNotAllowed(final HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
