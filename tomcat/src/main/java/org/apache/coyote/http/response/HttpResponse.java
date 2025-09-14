package org.apache.coyote.http.response;

import static common.HttpConstants.CONTENT_LENGTH_HEADER_NAME;
import static common.HttpConstants.CRLF;
import static common.HttpConstants.HEADER_VALUE_SEPARATOR;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponse {

    private final HttpStatusLine statusLine;
    private final HttpResponseHeader header;
    private final HttpResponseBody body;

    public static HttpResponse from(
            final HttpStatusLine statusLine,
            final HttpResponseHeader header,
            final HttpResponseBody body
    ) {
        return new HttpResponse(statusLine, header, body);
    }

    public void setCookie(final String name, final String value) {
        header.addSetCookie(name, value);
    }

    public int getContentLength() {
        return body.getContentLength();
    }

    @Override
    public String toString() {
        final String contentLengthHeader =
                CONTENT_LENGTH_HEADER_NAME + HEADER_VALUE_SEPARATOR + " " + getContentLength() + CRLF;
        return "%s%s%s%s%s".formatted(statusLine, header, contentLengthHeader, CRLF, body);
    }
}
