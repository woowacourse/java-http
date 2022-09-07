package org.apache.coyote.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected String doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return DEFAULT_MESSAGE;
    }

    @Override
    public boolean isRest() {
        return true;
    }
}
