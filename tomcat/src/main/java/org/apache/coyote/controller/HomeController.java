package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;

public class HomeController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected String doGet(final HttpRequest httpRequest) {
        return DEFAULT_MESSAGE;
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
