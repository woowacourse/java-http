package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.ContentType;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String body = "Hello world!";
        response.setStatusCode(StatusCode.OK);
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue() + CHARSET_UTF_8);
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setBody(body);
    }
}
