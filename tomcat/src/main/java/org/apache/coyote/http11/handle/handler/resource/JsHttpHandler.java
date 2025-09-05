package org.apache.coyote.http11.handle.handler.resource;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class JsHttpHandler extends StaticResourceHandler {

    private static final JsHttpHandler instance = new JsHttpHandler();

    private static final String JS_CONTENT_TYPE_HEADER_VALUE = "application/javascript;charset=utf-8 ";

    private JsHttpHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final String body = readFile(request.uri());
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", JS_CONTENT_TYPE_HEADER_VALUE);
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
        return request.uri().endsWith(".js");
    }

    public static JsHttpHandler getInstance() {
        return instance;
    }
}
