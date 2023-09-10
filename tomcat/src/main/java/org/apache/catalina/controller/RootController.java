package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;

import org.apache.catalina.ResponseContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootController extends AbstractController {

    private static final String RESPONSE_BODY = "Hello world!";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpVersion(request.getHttpVersion())
                .setStatusCode(HttpStatusCode.OK)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(CONTENT_LENGTH, RESPONSE_BODY.getBytes().length)
                .setResponseBody(new HttpResponseBody(RESPONSE_BODY));
    }
}
