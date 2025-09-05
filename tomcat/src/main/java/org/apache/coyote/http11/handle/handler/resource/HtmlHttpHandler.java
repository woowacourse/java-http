package org.apache.coyote.http11.handle.handler.resource;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HtmlHttpHandler extends StaticResourceHandler {

    private static final HtmlHttpHandler instance = new HtmlHttpHandler();

    private static final String HTML_CONTENT_TYPE_HEADER_VALUE = "text/html;charset=utf-8 ";

    private HtmlHttpHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final String body = readFile(request.uri());
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", HTML_CONTENT_TYPE_HEADER_VALUE);
        headers.addHeader("Content-Length", body.getBytes().length + " ");

        return new HttpResponse(
                request.protocolVersion(),
                HttpStatus.OK,
                headers,
                body
        );
    }

    public HttpResponse handle(
            final String uri,
            final HttpProtocolVersion protocolVersion
    ) {
        final String body = readFile(uri);
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", HTML_CONTENT_TYPE_HEADER_VALUE);
        headers.addHeader("Content-Length", body.getBytes().length + " ");

        return new HttpResponse(
                protocolVersion,
                HttpStatus.OK,
                headers,
                body
        );
    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        return request.uri().endsWith(".html");
    }

    public static HtmlHttpHandler getInstance() {
        return instance;
    }
}
