package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;

public class HomeController implements Controller {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public String service(final HttpRequest httpRequest) {
        return DEFAULT_MESSAGE;
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
