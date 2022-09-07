package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.ok()
                .contentType(ContentType.TEXT_PLAIN)
                .body("Hello world!")
                .flushBuffer();
    }
}
