package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class ErrorController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        return HttpResponse.toNotFound();
    }
}
