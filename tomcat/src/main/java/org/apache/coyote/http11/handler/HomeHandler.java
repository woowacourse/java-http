package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.apache.coyote.http11.HttpVersion.HTTP11;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpHeader;

public class HomeHandler implements Handler {
    @Override
    public HttpResponse handle() {
        final HttpVersion version = HTTP11;
        final HttpStatus status = OK;
        final String body = "Hello world!";
        final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE,
                ContentType.HTML.getValue(),
                ContentType.UTF_8.getValue());
        final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH,
                String.valueOf(body.length()));

        return HttpResponse.of(version, status, body, contentType, contentLength);
    }
}
