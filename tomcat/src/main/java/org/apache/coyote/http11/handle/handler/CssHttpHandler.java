package org.apache.coyote.http11.handle.handler;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class CssHttpHandler extends StaticResourceHandler {

    private static final CssHttpHandler instance = new CssHttpHandler();

    private static final String CSS_CONTENT_TYPE_HEADER_VALUE = "text/css;charset=utf-8 ";

    private CssHttpHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final String body = readFile(request.uri());
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", CSS_CONTENT_TYPE_HEADER_VALUE);
        headers.addHeader("Content-Length", body.getBytes().length + " ");

        return new HttpResponse(
                request.protocolVersion(),
                HttpStatus.OK,
                headers,
                body
        );
    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        return request.uri().endsWith(".css");
    }

    public static CssHttpHandler getInstance() {
        return instance;
    }
}
