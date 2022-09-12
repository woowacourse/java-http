package org.apache.coyote.http11;

import nextstep.jwp.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Handler {
    public AbstractController() {
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }

        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
    }
}
