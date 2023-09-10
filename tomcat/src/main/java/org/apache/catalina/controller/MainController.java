package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;

public class MainController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello world!";

        response.copy(HttpResponse.of(statusLine, contentType, responseBody));
    }
}
