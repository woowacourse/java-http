package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootController extends AbstractController {

    private static final String RESPONSE_BODY = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(HttpStatusCode.OK)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addContentLengthHeader(RESPONSE_BODY.getBytes().length)
                .setResponseBody(new HttpResponseBody(RESPONSE_BODY));
    }
}
