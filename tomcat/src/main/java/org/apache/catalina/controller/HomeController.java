package org.apache.catalina.controller;

import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.HTML;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;

import java.util.Map;

import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.message.response.HttpStatusLine;

public class HomeController extends AbstractController {

    private final String endPoint = "/";

    @Override
    public boolean canHandle(final HttpRequest request) {
        return matchRequestUriWithBaseUri(request, endPoint);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);

        final String responseBody = "Hello world!";
        final HttpBody httpBody = new HttpBody(responseBody);

        final HttpHeaders responseHeader = new HttpHeaders(Map.of(
                CONTENT_TYPE.getValue(), HTML.getValue() + ";charset=utf-8",
                CONTENT_LENGTH.getValue(), String.valueOf(responseBody.length())
        ));

        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        /* NOOP */
    }
}
