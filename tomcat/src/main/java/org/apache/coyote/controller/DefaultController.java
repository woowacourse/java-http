package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultController implements Controller {

    @Override
    public HttpResponse run(HttpRequest request) {
        return null;
    }
}
