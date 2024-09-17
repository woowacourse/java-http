package org.apache.catalina.controller;

import static org.apache.coyote.http11.message.header.HttpHeaderAcceptType.PLAIN;
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

    public HomeController(final String baseUri) {
        super(baseUri);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String responseBody = "Hello world!";
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);
        final HttpHeaders responseHeader = new HttpHeaders(Map.of(
                CONTENT_TYPE.getValue(), PLAIN.getValue(),
                CONTENT_LENGTH.getValue(), String.valueOf(responseBody.length())
        ));
        final HttpBody httpBody = new HttpBody(responseBody);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        // TODO : 지원하지 않는 기능을 어떻게 처리할지 개선 필요
    }
}
