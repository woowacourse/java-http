package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpVersion.HTTP11;
import static org.apache.coyote.http11.header.ContentType.HTML;
import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpStatus.OK;

import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeHandler implements Handler {
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = DEFAULT_MESSAGE;
        final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF_8.getValue());
        final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.length()));

        return HttpResponse.of(HTTP11, OK, body, contentType, contentLength);
    }
}
