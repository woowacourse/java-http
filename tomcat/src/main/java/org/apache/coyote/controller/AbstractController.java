package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;

public abstract class AbstractController implements Controller {

    @Override
    public String service(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected String doGet(final HttpRequest httpRequest) {
        return null;
    }

    protected String doPost(final HttpRequest httpRequest) {
        return null;
    }
}
