package org.apache.coyote.controller;

import java.util.Objects;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        String method = request.getMethod();

        if (Objects.equals(method, "GET")) {
            doGet(request, response);
            return;
        }
        if (Objects.equals(method, "POST")) {
            doPost(request, response);
            return;
        }
        handleUnsupportedMethod(request, response);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception { /* NOOP */ }

    protected void handleUnsupportedMethod(final HttpRequest request, final HttpResponse response) {
        response.setProtocol(request.getProtocol());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
