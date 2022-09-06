package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.HttpRequest;

public class RegisterHandler implements Handler {

    private final HttpRequest httpRequest;

    public RegisterHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        return null;
    }
}
